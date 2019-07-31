package com.busecarik.platformer.entities;

import com.busecarik.platformer.Config;
import com.busecarik.platformer.Jukebox;
import com.busecarik.platformer.utils.Utils;

public class Coin extends DynamicEntity {
    static final String TAG = "Coin";
    static final float COIN_JUMP_FORCE = -(GRAVITY/4);
    private boolean _isCollecting = false;
    private long _timer = 0;

    public Coin(String spriteName, int xpos, int ypos) {
        super(spriteName, xpos, ypos);
        _type = TAG;
    }

    @Override
    public void update(double dt) {
        if (_isCollecting) {
            if (System.currentTimeMillis() <_timer + Config.COOL_DOWN) {
                _y += Utils.clamp((float) (_velY * dt), -Config.MAX_DELTA, Config.MAX_DELTA);
            } else {
                _game._level.removeEntity(this);
                _isCollecting = false;
            }
        }
    }

    @Override
    public void onCollision(Entity that) {
        if (that._type.equals("Player") && !_isCollecting) {
            _timer = System.currentTimeMillis();
            _game._level._collectedCoin++;
            _game._level._levelCollectedCoin++;
            _velY = COIN_JUMP_FORCE;
            _isCollecting = true;
            _game.onGameEvent(Jukebox.GameEvent.CoinPickup, that);

        } else {
            super.onCollision(that);
        }
    }
}
