package com.busecarik.platformer.levels;

public abstract class LevelData {
    public static final String NULL_SPRITE = "null_sprite";
    public static final String PLAYER = "blue_left1";
    public static final String ENEMY = "enemyblockiron";
    public static final String COIN = "coinyellow";
    public static final String DOOR = "level_up";
    public static final String BACKGROUND = "background";
    public static final String GROUND = "ground";
    public static final String GROUND_LEFT = "ground_left";
    public static final String GROUND_RIGHT = "ground_right";
    public static final String GROUND_ROUND = "ground_round";
    public static final String MUD = "mud_square";
    public static final String MUD_LEFT = "mud_left";
    public static final String MUD_RIGHT = "mud_right";
    public static final String HEART = "heart";
    public static final String SPEAR = "spear";
    public static final int NO_TILE = 0;
    int [][] mTiles;
    int mHeight = 0;
    int mWidth = 0;

    public int getTile(final int x, final int y) {
        //y rows, x columns
        return mTiles[y][x];
    }

    int[] getRow(final int y) {
        return mTiles[y];
    }

    void updateLevelDimensions() {
        mHeight = mTiles.length;
        mWidth = mTiles[0].length;
    }

     public abstract String getSpriteName(final int tileType);
}
