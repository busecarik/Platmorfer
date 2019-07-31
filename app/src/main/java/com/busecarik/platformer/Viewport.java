package com.busecarik.platformer;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import com.busecarik.platformer.entities.Entity;

public class Viewport {
    private final PointF mLookAt = new PointF(0f,0f);
    private int mPixelsPerMeterX; //viewport "density"
    private int mPixelsPerMeterY;
    private int mScreenWidth; //resolution
    private int mScreenHeight;
    private int mScreenCenterY; //center screen
    private int mScreenCenterX;
    private float mMetersToShowX; //Field of View
    private float mMetersToShowY;
    private float mHalfDistX; //cached value (0.5*FOV)
    private float mHalfDistY;
    private final static float BUFFER = 1f; //overdraw, to avoid visual gaps

    public Viewport(final int screenWidth, final int screenHeight, final float metersToShowX, final float metersToShowY){
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        mScreenCenterX = mScreenWidth / 2;
        mScreenCenterY = mScreenHeight / 2;
        mLookAt.x = 0.0f;
        mLookAt.y = 0.0f;
        setMetersToShow(metersToShowX, metersToShowY);
    }

    //setMetersToShow calculates the number of physical pixels per meters
    //so that we can translate our game world (meters) to the screen (pixels)
    //provide the dimension(s) you want to lock. The viewport will automatically
    // size the other axis to fill the screen perfectly.
    private void setMetersToShow(float metersToShowX, float metersToShowY){
        if (metersToShowX <= 0f && metersToShowY <= 0f) throw new IllegalArgumentException("One of the dimensions must be provided!");
        //formula: new height = (original height / original width) x new width
        mMetersToShowX = metersToShowX;
        mMetersToShowY = metersToShowY;
        if(metersToShowX == 0f || metersToShowY == 0f){
            if(metersToShowY > 0f) { //if Y is configured, calculate X
                mMetersToShowX = ((float) mScreenWidth / mScreenHeight) * metersToShowY;
            }else { //if X is configured, calculate Y
                mMetersToShowY = ((float) mScreenHeight / mScreenWidth) * metersToShowX;
            }
        }
        mHalfDistX = (mMetersToShowX+BUFFER) * 0.5f;
        mHalfDistY = (mMetersToShowY+BUFFER) * 0.5f;
        mPixelsPerMeterX = (int)(mScreenWidth / mMetersToShowX);
        mPixelsPerMeterY = (int)(mScreenHeight / mMetersToShowY);
    }

    public void lookAt(final float x, final float y){
        mLookAt.x = x;
        mLookAt.y = y;
    }
    public void lookAt(final Entity obj){
        lookAt(obj.centerX(), obj.centerY());
    }
    public void lookAt(final PointF pos){
        lookAt(pos.x, pos.y);
    }

    public void worldToScreen(final float worldPosX, final float worldPosY, Point screenPos){
        screenPos.x = (int) (mScreenCenterX - ((mLookAt.x - worldPosX) * mPixelsPerMeterX));
        screenPos.y = (int) (mScreenCenterY - ((mLookAt.y - worldPosY) * mPixelsPerMeterY));
    }
    public void worldToScreen(final PointF worldPos, Point screenPos){
        worldToScreen(worldPos.x, worldPos.y, screenPos);
    }
    public void worldToScreen(final Entity e, final Point screenPos){
        worldToScreen(e._x, e._y, screenPos);
    }

    public boolean inView(final Entity e) {
        final float maxX = (mLookAt.x + mHalfDistX);
        final float minX = (mLookAt.x - mHalfDistX)-e._width;
        final float maxY = (mLookAt.y + mHalfDistY);
        final float minY  = (mLookAt.y - mHalfDistY)-e._height;
        return (e._x > minX && e._x < maxX)
                && (e._y > minY && e._y < maxY);
    }

    public boolean inView(final RectF bounds) {
        final float right = (mLookAt.x + mHalfDistX);
        final float left = (mLookAt.x - mHalfDistX);
        final float bottom = (mLookAt.y + mHalfDistY);
        final float top  = (mLookAt.y - mHalfDistY);
        return (bounds.left < right && bounds.right > left)
                && (bounds.top < bottom && bounds.bottom > top);
    }

    public float getHorizontalView(){
        return mMetersToShowX;
    }
    public float getVerticalView(){
        return mMetersToShowY;
    }
    public int getScreenWidth() {
        return mScreenWidth;
    }
    public int getScreenHeight(){
        return mScreenHeight;
    }
    public int getPixelsPerMeterX(){
        return mPixelsPerMeterX;
    }
    public int getPixelsPerMeterY(){
        return mPixelsPerMeterY;
    }
}