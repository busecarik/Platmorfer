package com.busecarik.platformer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;


public class RenderHUD {
    Context _context = null;
    private Paint _paint = null;
    private Typeface _typeface = null;

    RenderHUD(Context context) {
        _context = context;
        _paint = new Paint();
        //next line is a borrowing code
        _typeface = Typeface.createFromAsset(context.getAssets(), Config.FONT);
        _paint.setColor(Color.BLACK);
        _paint.setTextAlign(Paint.Align.LEFT);
        _paint.setTextSize(Config.TEXT_SIZE_HUD);
        //next line is a borrowing code
        _paint.setTypeface(_typeface);
    }

    void isContinue(final Canvas canvas, final int playerHealth, final int collectedCoin, final int remainingCoin, final int level) {
        canvas.drawText(_context.getString(R.string.player_health, playerHealth) , 10, Config.TEXT_SIZE_HUD, _paint);
        canvas.drawText(_context.getString(R.string.collected_coin, collectedCoin), 10, Config.TEXT_SIZE_HUD *2, _paint);
        canvas.drawText(_context.getString(R.string.remaining_coin, remainingCoin), 10, Config.TEXT_SIZE_HUD *3, _paint);
        canvas.drawText(_context.getString(R.string.level, level), 10, Config.TEXT_SIZE_HUD *4, _paint);
    }
}
