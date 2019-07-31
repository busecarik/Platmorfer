package com.busecarik.platformer.entities;

public class Door extends StaticEntity {
    public Door(String spriteName, int xpos, int ypos) {
        super(spriteName, xpos, ypos);
        _type = "Door";
    }

    @Override
    public void onCollision(Entity that) {
        if (that._type.equals("Player") && _game._level._remainingCoin == 0) {
            _game._level._levelNumber++;
            _game.levelUp();
        }
    }
}
