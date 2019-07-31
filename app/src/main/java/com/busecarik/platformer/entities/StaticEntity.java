package com.busecarik.platformer.entities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class StaticEntity extends Entity {
    protected Bitmap _bitmap = null;

    public StaticEntity(final String spriteName, final int xpos, final int ypos) {
        _x = xpos;
        _y = ypos;
        loadBitmap(spriteName, xpos, ypos);
    }

    protected void loadBitmap(final String spriteName, final int xpos, final int ypos) {
        _bitmap = _game._pool.createBitmap(spriteName, _width, _height);
    }

    @Override
    public void render(Canvas canvas, final Matrix transform, Paint paint) {
        canvas.drawBitmap(_bitmap, transform, paint);
    }

    @Override
    public void destroy() {
        if(_bitmap != null) {
            _bitmap.recycle();
            _bitmap = null;
        }
    }
}
