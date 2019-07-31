package com.busecarik.platformer.entities;

public class Enemy extends StaticEntity {

    static final String TAG = "Enemy";

    public Enemy(String spriteName, int xpos, int ypos) {
        super(spriteName, xpos, ypos);
        _type = TAG;
    }

}
