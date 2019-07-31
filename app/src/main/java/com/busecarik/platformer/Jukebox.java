package com.busecarik.platformer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;

import java.io.IOException;
import java.util.HashMap;

public class Jukebox {
    public enum GameEvent {
        LevelStart,
        Jump,
        Bump,
        CoinPickup,
        LevelGoal,
        GameOver,
        PowerUp
    }

    private static final String TAG = "Jukebox";

    private SoundPool _soundPool = null;
    private boolean _soundEnabled = true;
    private HashMap<GameEvent, Integer> _soundsMap = null;
    private Context _context = null;
    private boolean _musicEnabled = true;
    private MediaPlayer _bgPlayer = null;
    private SparseArray<String> _tracks = null;
    private int _levelNumber = 1;

    public Jukebox(Context context) {
        _context = context;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
        _soundEnabled = prefs.getBoolean(Config.SOUNDS_PREF_KEY, true);
        _musicEnabled = prefs.getBoolean(Config.MUSIC_PREF_KEY, true);
        loadIfNeeded();
    }

    private void loadTracks() {
        _tracks = new SparseArray<>();
        _tracks.put(1, Config.BACKGROUND_MUSIC1);
        _tracks.put(2, Config.BACKGROUND_MUSIC2);
        _tracks.put(3, Config.BACKGROUND_MUSIC3);
    }

    private void loadMusic(final int levelNumber){
        try{
            _bgPlayer = new MediaPlayer();
            AssetFileDescriptor afd = _context
                    .getAssets().openFd(_tracks.get(levelNumber));
            _bgPlayer.setDataSource(
                    afd.getFileDescriptor(),
                    afd.getStartOffset(),
                    afd.getLength());
            _bgPlayer.setLooping(true);
            _bgPlayer.setVolume(Config.DEFAULT_MUSIC_VOLUME, Config.DEFAULT_MUSIC_VOLUME);
            _bgPlayer.prepare();
        }catch(IOException e){
            _bgPlayer = null;
            _musicEnabled = false;
            Log.e(TAG, "loadEventMusic: error loading music " + e.toString());
        }
    }

    public void playMusicForBackground(final int levelNumber) {
        _levelNumber = levelNumber;
        loadMusic(levelNumber);
        resumeBgMusic();
    }

    private void unloadMusic(){
        if(_bgPlayer != null) {
            _bgPlayer.stop();
            _bgPlayer.release();
        }
    }

    public void pauseBgMusic(){
        if(!_musicEnabled){ return; }
        _bgPlayer.pause();
    }
    public void resumeBgMusic(){
        if(!_musicEnabled){ return; }
        _bgPlayer.start();
    }
    public void playSoundForGameEvent(GameEvent event){
        if(!_soundEnabled){return;}
        final float leftVolume = Config.DEFAULT_VOLUME;
        final float rightVolume = Config.DEFAULT_VOLUME;
        final int priority = 1;
        final int loop = 0;
        final float rate = 1.0f;
        final Integer soundID = _soundsMap.get(event);
        if(soundID != null){
            _soundPool.play(soundID, leftVolume, rightVolume, priority, loop, rate);
        }
    }

    private void loadIfNeeded(){
        if(_soundEnabled){
            loadSounds();
        }
        if(_musicEnabled){
            loadTracks();
            loadMusic(_levelNumber);
        }
    }

    private void loadSounds(){
        createSoundPool();
        _soundsMap = new HashMap<GameEvent, Integer>();
        loadEventSound(GameEvent.Jump, Config.JUMP);
        loadEventSound(GameEvent.CoinPickup, Config.COIN_PICKUP);
        loadEventSound(GameEvent.LevelGoal, Config.LEVEL_GOAL);
        loadEventSound(GameEvent.LevelStart, Config.START_LEVEL);
        loadEventSound(GameEvent.Bump, Config.BUMP);
        loadEventSound(GameEvent.GameOver, Config.GAME_OVER);
        loadEventSound(GameEvent.PowerUp, Config.POER_UP);
    }

    private void unloadSounds(){
        if(_soundPool != null) {
            _soundPool.release();
            _soundPool = null;
            _soundsMap.clear();
        }
    }

    private void loadEventSound(final GameEvent event, final String fileName){
        try {
            AssetFileDescriptor afd = _context.getAssets().openFd(fileName);
            int soundId = _soundPool.load(afd, 1);
            _soundsMap.put(event, soundId);
        }catch(IOException e){
            Log.e(TAG, "loadEventSound: error loading sound " + e.toString());
        }
    }

    public void toggleSoundStatus(){
        _soundEnabled = !_soundEnabled;
        if(_soundEnabled){
            loadSounds();
        }else{
            unloadSounds();
        }
        if (_soundEnabled) {
            PreferenceManager
                    .getDefaultSharedPreferences(_context)
                    .edit()
                    .putBoolean(Config.SOUNDS_PREF_KEY, _soundEnabled)
                    .commit();
        }
    }

    public void toggleMusicStatus(){
        _musicEnabled = !_musicEnabled;
        if(_musicEnabled){
            loadMusic(_levelNumber);
            _bgPlayer.start();
        }else{
            unloadMusic();
        }
        if (_musicEnabled) {
            PreferenceManager
                    .getDefaultSharedPreferences(_context)
                    .edit()
                    .putBoolean(Config.MUSIC_PREF_KEY, _soundEnabled)
                    .commit();
        }
    }

    @SuppressWarnings("deprecation")
    private void createSoundPool() {
        AudioAttributes attr = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        _soundPool = new SoundPool.Builder()
                .setAudioAttributes(attr)
                .setMaxStreams(Config.MAX_STREAMS)
                .build();
    }
}
