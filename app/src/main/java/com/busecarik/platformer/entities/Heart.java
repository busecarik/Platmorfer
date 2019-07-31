package com.busecarik.platformer.entities;

import com.busecarik.platformer.Jukebox;

public class Heart extends StaticEntity {
    static final String TAG = "Heart";
    public Heart(String spriteName, int xpos, int ypos) {
        super(spriteName, xpos, ypos);
        _width = DEFAULT_DIMENSION/1.25f;
        _height = DEFAULT_DIMENSION/1.25f;
        loadBitmap(spriteName, xpos, ypos);
        _type = "Heart";
    }

    @Override
    public void onCollision(Entity that) {
        if (that._type.equals("Player")) {
            _game._level._player._health++;
            _game._level.removeEntity(this);
            _game._jukebox.playSoundForGameEvent(Jukebox.GameEvent.PowerUp);
        } else {
            super.onCollision(that);
        }
    }
}
