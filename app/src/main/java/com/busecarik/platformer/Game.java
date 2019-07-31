package com.busecarik.platformer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.busecarik.platformer.entities.Entity;
import com.busecarik.platformer.input.InputManager;
import com.busecarik.platformer.levels.LevelManager;
import com.busecarik.platformer.utils.BitmapPool;

import java.util.ArrayList;


public class Game extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    public static final String TAG = "Game";
    private static final int BG_COLOR = Color.rgb(135, 206, 235);
    private static final float METERS_TO_SHOW_X = 16f; //set the value you want fixed
    private static final float METERS_TO_SHOW_Y = 0f;  //the other is calculated at runtime!
    private static final double NANOS_TO_SECONDS = 1.0 / 1000000000;
    private final Matrix _transform = new Matrix();
    private Thread _gameThread = null;
    private volatile boolean _isRunning = false;
    private SurfaceHolder _holder = null;
    private Paint _paint = new Paint();
    private Canvas _canvas;
    public LevelManager _level = null;
    private InputManager _controls = new InputManager();
    private Viewport _camera = null;
    public final ArrayList<Entity> _visibleEntities = new ArrayList<>();
    public BitmapPool _pool = null;
    private static final Point _position = new Point();
    private GameActivity _activity = null;
    public boolean _gameOver = false;
    private RenderHUD _hud = null;
    public Jukebox _jukebox = null;

    public Game(Context context) {
        super(context);
        init(context);
    }

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Game(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public Game(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        _camera = new Viewport(Config.STAGE_WIDTH, Config.STAGE_HEIGHT, METERS_TO_SHOW_X, METERS_TO_SHOW_Y);
        Log.d(TAG, _camera.toString());
        Entity._game = this;
        _activity = (GameActivity) context;
        _pool = new BitmapPool(this);
        _hud = new RenderHUD(context);
        _jukebox = new Jukebox(context);
        _level = new LevelManager(context, _pool);
        _holder = getHolder();
        _holder.addCallback(this);
        _holder.setFixedSize(Config.STAGE_WIDTH, Config.STAGE_HEIGHT);
        ((GameActivity) context).setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    public InputManager getControls() {
        return _controls;
    }
    public void setControls(final InputManager controls) {
        _controls.onPause();
        _controls.onStop();
        _controls = controls;
    }

    public float getWorldHeight() { return _level._levelHeight; }
    public float getWorldWidth() { return _level._levelWidth; }

    public int worldToScreenX(float worldDistance) {
        return (int) (worldDistance * _camera.getPixelsPerMeterX());
    }

    public int worldToScreenY(float worldDistance) {
        return (int) worldDistance * _camera.getPixelsPerMeterY();
    }

    public float screenToWorldX(int pixelDistance) {
        return (float) (pixelDistance / _camera.getPixelsPerMeterX());
    }

    public float screenToWorldY(int pixelDistance) {
        return (float) (pixelDistance / _camera.getPixelsPerMeterY());
    }

    @Override
    public void run() {
        long lastFrame = System.nanoTime();
        onGameEvent(Jukebox.GameEvent.LevelStart, null);
        while (_isRunning) {
            final double deltaTime = (System.nanoTime() - lastFrame) * NANOS_TO_SECONDS;
            lastFrame = System.nanoTime();
            update(deltaTime);
            buildVisibleSet();
            render(_camera, _visibleEntities);
        }
    }

    private void update(final double dt) {
        _camera.lookAt(_level._player);
        _controls.update((float) dt);
        _level.update(dt);
        _activity.getMusicStatus();
        _activity.getSoundStatus();
        checkGameOver();
    }

    private void buildVisibleSet() {
        _visibleEntities.clear();
        for (final Entity e : _level._entities) {
            if (_camera.inView(e)) {
                _visibleEntities.add(e);
            }
        }
    }

    public void restart() {
        onGameEvent(Jukebox.GameEvent.LevelStart, null);
        _level.restart();
        _gameOver = false;
    }

    public void levelUp() {
        if (_level._player._health >= 1) {
            if (_level._levelNumber > Config.LEVEL_NUMBER) {
                _level._levelNumber = 1;
            }
            onGameEvent(Jukebox.GameEvent.LevelGoal, null);
            onLevelChangeMusic();
            _level._isLevelChange = true;
            _level.levelChanged();
            onGameEvent(Jukebox.GameEvent.LevelStart, null);
        }
    }

    public void onLevelChangeMusic() {
        _jukebox.pauseBgMusic();
        _jukebox.playMusicForBackground(_level._levelNumber);
    }

    public void onGameEvent(Jukebox.GameEvent gameEvent, Entity e /*can be null!*/) {
        _jukebox.playSoundForGameEvent(gameEvent);
    }

    private void checkGameOver() {
        if (_level._player._health < 1) {
            _gameOver = true;
            onGameEvent(Jukebox.GameEvent.GameOver, null);
            restart();
        }
    }

    private void render(final Viewport camera, final ArrayList<Entity> visibleEntities) {
        if (!acquireAndLockCanvas()) {
            return;
        }
        try {
            _canvas.drawColor(BG_COLOR);

            for (final Entity e : visibleEntities) {
                _transform.reset();
                camera.worldToScreen(e, _position);
                _transform.postTranslate(_position.x, _position.y);
                e.render(_canvas, _transform, _paint);
                if (!_gameOver) {
                    _hud.isContinue(_canvas, _level._player._health, _level._collectedCoin, _level._remainingCoin, _level._levelNumber);
                }
            }
        }finally {
            _holder.unlockCanvasAndPost(_canvas);
        }
    }

    private boolean acquireAndLockCanvas() {
        if(!_holder.getSurface().isValid()) {
            return false;
        }
        _canvas = _holder.lockCanvas();
        return (_canvas != null);
    }

    protected void onPause() {
        Log.d(TAG, "onPause");
        _jukebox.pauseBgMusic();
        _controls.onPause();
        _isRunning = false;
        while (true) {
            try {
                _gameThread.join();
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d(TAG, Log.getStackTraceString(e.getCause()));
            }
        }
    }

    protected void onResume() {
        Log.d(TAG, "onResume");
        _isRunning = true;
        _jukebox.resumeBgMusic();
        _controls.onResume();
        _gameThread = new Thread(this);
    }

    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        _gameThread = null;
        if (_level != null) {
            _level.destroy();
            _level = null;
        }
        _controls = null;
        Entity._game = null;
        if (_pool != null) {
            _pool.empty();
        }
        _holder.removeCallback(this);
    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        Log.d(TAG, "Surface Created");
    }

    @Override
    public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
        Log.d(TAG, "Surface Changed");
        Log.d(TAG, "\t Width: " + width + " Height: " + height);
        if (_gameThread != null && _isRunning) {
            Log.d(TAG, "GameThread started!");
            _gameThread.start();
        }
    }

    @Override
    public void surfaceDestroyed(final SurfaceHolder holder) {
        Log.d(TAG, "Surface Destroyed");
    }
}
