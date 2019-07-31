package com.busecarik.platformer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.busecarik.platformer.input.Accelerometer;
import com.busecarik.platformer.input.InputManager;
import com.busecarik.platformer.input.TouchController;
import com.busecarik.platformer.input.VirtualJoystick;

public class GameActivity extends AppCompatActivity {
    Game _game;
    ImageButton _enableSound;
    ImageButton _enableMusic;
    private boolean _isSoundPress = true;
    private boolean _isMusicPress = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_screen);
        _game = findViewById(R.id.game);
        InputManager controls = null;;
        RelativeLayout touchControllerLayout = findViewById(R.id.touchControl);
        //next two lines are borrow code
        Intent i = getIntent();
        String controller = i.getStringExtra(Config.CONTROLLER);
        switch (controller) {
            case Config.TOUCH_CONTROLLER:
                controls = new TouchController(touchControllerLayout);
                break;
            case Config.JOYSTICK:
                touchControllerLayout.setVisibility(View.GONE);
                controls = new VirtualJoystick(findViewById(R.id.virtual_joystick));
                break;
            case Config.ACCELEROMETER:
                touchControllerLayout.setVisibility(View.GONE);
                controls = new Accelerometer(this);
                break;
        }
        _game.setControls(controls);
        _enableSound = findViewById(R.id.soundEnable);
        _enableMusic = findViewById(R.id.musicEnable);
    }

    public void getSoundStatus() {
        _enableSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _game._jukebox.toggleSoundStatus();
                if (_isSoundPress) {
                    _enableSound.setImageResource(R.drawable.ic_notifications_black_24dp);
                    _isSoundPress = false;
                } else {
                    _enableSound.setImageResource(R.drawable.ic_notifications_off_black_24dp);
                    _isSoundPress = true;
                }
            }
        });
    }

    public void getMusicStatus() {
        _enableMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _game._jukebox.toggleMusicStatus();
                if (_isMusicPress) {
                    _enableMusic.setImageResource(R.drawable.ic_volume_up_black_24dp);
                    _isMusicPress = false;
                } else {
                    _enableMusic.setImageResource(R.drawable.ic_volume_off_black_24dp);
                    _isMusicPress = true;
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        _game.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        _game.onResume();
    }

    @Override
    protected void onDestroy() {
        _game.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            return;
        }
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}
