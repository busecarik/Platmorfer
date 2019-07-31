package com.busecarik.platformer.entities;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.busecarik.platformer.Config;
import com.busecarik.platformer.Jukebox;
import com.busecarik.platformer.input.InputManager;
import com.busecarik.platformer.utils.Utils;

public class Player extends DynamicEntity {
    static final String TAG = "Player";
    static final float PLAYER_RUN_SPEED = 6.0f; //meter per second
    static final float PLAYER_JUMP_FORCE = -(GRAVITY/2); //whatever feels good
    static final float MIN_INPUT_TO_TURN = 0.05f; //5% joystick input before we start
    private final int LEFT = 1;
    private final int RIGHT = -1;
    private int mFacing = LEFT;
    public int _health = Config.PLAYER_HEALTH;
    private boolean _collision = false;
    private boolean _isHurt = false;
    private long _timer = System.currentTimeMillis();
    private Paint _paint = null;

    public Player(final String spriteName, final int xpos, final int ypos) {
        super(spriteName, xpos, ypos);
        _type = TAG;
        _paint = new Paint();
    }

    @Override
    public void onCollision(Entity that) {
        if (that._type.equalsIgnoreCase("Enemy") || that._type.equalsIgnoreCase("Spear")) {
            if (System.currentTimeMillis() >= _timer + Config.RECOVERY_TIME) {
                _timer = System.currentTimeMillis();
                _collision = true;
                _isHurt = true;
                if (that._type.equalsIgnoreCase("Enemy")) {
                    _game._level.removeEntity(that);
                }
                _health--;
                _game.onGameEvent(Jukebox.GameEvent.Bump, that);
            }
        } else {
            super.onCollision(that);
        }
    }

    private void checkTimer() {
        if (System.currentTimeMillis() >= _timer + Config.RECOVERY_TIME && _collision) {
            _collision = false;
        }
    }

    private void setAlphaValue() {
        checkTimer();
        if (_collision) {
            _paint.setAlpha(Config.TRANSPARENT_VALUE);
        } else {
            _paint.setAlpha(Config.ALPHA_VALUE);
        }
    }

    private void hurt(final double dt) {
        _velY = PLAYER_JUMP_FORCE;
        _y += Utils.clamp((float) (_velY * dt), -Config.MAX_DELTA, Config.MAX_DELTA);
    }

    @Override
    public void update(final double dt) {
        final InputManager controls = _game.getControls();
        final float direction = controls._horizontalFactor;
        _velX = direction * PLAYER_RUN_SPEED;
        updateFacingDirection(direction);
        if (controls._isJumping && _isOnGround) {
            _velY = PLAYER_JUMP_FORCE;
            _isOnGround = false;
            _game.onGameEvent(Jukebox.GameEvent.Jump, this);
        }
        if (_isHurt) {
            hurt(dt);
            _isHurt = false;
        }
        super.update(dt);
    }

    private void updateFacingDirection(final float controlDirection) {
        if (Math.abs(controlDirection) < MIN_INPUT_TO_TURN) { return; }
        if (controlDirection < 0) {
            mFacing = LEFT;
        } else if (controlDirection > 0) {
            mFacing = RIGHT;
        }
    }

    @Override
    public void render(Canvas canvas, Matrix transform, Paint paint) {
        transform.preScale(mFacing, 1.0f);
        if (mFacing == RIGHT) {
            final float offset = _game.worldToScreenX(_width);
            transform.postTranslate(offset, 0);
        }
        setAlphaValue();
        super.render(canvas, transform, _paint);
    }
}
