package com.busecarik.platformer.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;

public abstract class Utils {
    static Matrix _matrix= new Matrix();

    public static Bitmap flipBitmap(final Bitmap src, final boolean horizontal) {
        _matrix.reset();
        final int cx = src.getWidth() / 2;
        final int cy = src.getHeight() / 2;
        if (horizontal) {
            _matrix.postScale(1, -1, cx, cy);
        } else {
            _matrix.postScale(-1, 1, cx, cy);
        }
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), _matrix, true);
    }

    public static Bitmap scaleToTargetHeight(final Bitmap src, final int targetHeight) {
        float ratio = targetHeight / (float) src.getHeight();
        int newH = (int) (src.getHeight()*ratio);
        int newW = (int) (src.getWidth()*ratio);
        return Bitmap.createScaledBitmap(src, newW, newH, true);
    }

    public static float wrap( float val, final float min, final float max) {
        if (val < min) {
            val = max;
        } else if (val > max) {
            val = min;
        }
        return val;
    }

    public static float clamp( float val, final float min, final float max) {
        if (val  > max) {
            val = max;
        } else if (val < min) {
            val = min;
        }
        return val;
    }

    public static boolean findDirection (final float val, final float min, final float max, boolean direction) {
        if (val > max) {
            direction = true;
        } else if (val < min) {
            direction = false;
        }
        return direction;
    }

    public static int pxToDp(final int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(final int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
