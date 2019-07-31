package com.busecarik.platformer.levels;

import android.util.SparseArray;

public class LevelInfo extends LevelData {
    private final SparseArray<String> mTileIdToSpriteName = new SparseArray<>();

    public LevelInfo() {
            mTileIdToSpriteName.put(0, BACKGROUND);
            mTileIdToSpriteName.put(1, PLAYER);
            mTileIdToSpriteName.put(2, GROUND);
            mTileIdToSpriteName.put(3, GROUND_LEFT);
            mTileIdToSpriteName.put(4, GROUND_RIGHT);
            mTileIdToSpriteName.put(5, COIN);
            mTileIdToSpriteName.put(6, ENEMY);
            mTileIdToSpriteName.put(7, DOOR);
            mTileIdToSpriteName.put(8, MUD);
            mTileIdToSpriteName.put(9, MUD_LEFT);
            mTileIdToSpriteName.put(10, MUD_RIGHT);
            mTileIdToSpriteName.put(11, GROUND_ROUND);
            mTileIdToSpriteName.put(12, HEART);
            mTileIdToSpriteName.put(13, SPEAR);
    }

    @Override
    public String getSpriteName(int tileType) {
        final String fileName = mTileIdToSpriteName.get(tileType);
        if (fileName != null) {
            return fileName;
        }
        return NULL_SPRITE;
    }
}
