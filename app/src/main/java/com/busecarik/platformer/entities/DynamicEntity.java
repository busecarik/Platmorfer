package com.busecarik.platformer.entities;

import com.busecarik.platformer.Config;
import com.busecarik.platformer.utils.Utils;

public class DynamicEntity extends StaticEntity {
    static final float GRAVITY = 40f;
    public float _velX = 0;
    public float _velY = 0;
    public float _gravity = GRAVITY;
    boolean _isOnGround = false;

    public DynamicEntity(String spriteName, int xpos, int ypos) {
        super(spriteName, xpos, ypos);
    }

    @Override
    public void update(double dt) {
        _x += Utils.clamp((float) (_velX * dt), -Config.MAX_DELTA, Config.MAX_DELTA);

        if (!_isOnGround) {
            final float gravityThisTick = (float) (_gravity * dt);
            _velY += gravityThisTick;
        }
        _y += Utils.clamp((float) (_velY * dt), -Config.MAX_DELTA, Config.MAX_DELTA);
        if (_y > _game.getWorldHeight()) {
            _y = 0f;
        }
        _isOnGround = false;
    }

    @Override
    public void onCollision(Entity that) {
        Entity.getOverlap(this, that, Entity.overlap);
        _x += Entity.overlap.x;
        _y += Entity.overlap.y;
        if (Entity.overlap.y != 0) {
            _velY = 0;
            if (Entity.overlap.y < 0) { //we've hit our feet
                _isOnGround = true;
            } //if overlap.y > 0f, we've hit our head
        }
    }
}
