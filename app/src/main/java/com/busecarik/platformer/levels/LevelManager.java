package com.busecarik.platformer.levels;

import android.content.Context;
import android.util.SparseArray;

import com.busecarik.platformer.Config;
import com.busecarik.platformer.R;
import com.busecarik.platformer.entities.Coin;
import com.busecarik.platformer.entities.Door;
import com.busecarik.platformer.entities.Enemy;
import com.busecarik.platformer.entities.Entity;
import com.busecarik.platformer.entities.Heart;
import com.busecarik.platformer.entities.Player;
import com.busecarik.platformer.entities.Spear;
import com.busecarik.platformer.entities.StaticEntity;
import com.busecarik.platformer.utils.BitmapPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class LevelManager {
    public int _levelHeight = 0;
    public int _levelWidth = 0;
    public int _levelNumber = 1;
    public final ArrayList<Entity> _entities = new ArrayList<>();
    private final ArrayList<Entity> _entitiesToAdd = new ArrayList<>();
    private final ArrayList<Entity> _entitiesToRemove = new ArrayList<>();
    private final SparseArray<String> _levelMaps = new SparseArray<>();
    public Player _player = null;
    private BitmapPool _pool = null;
    public int _collectedCoin = 0;
    public int _levelCollectedCoin = 0;
    public int _totalLevelCoin = 0;
    public int _remainingCoin = 0;
    private Context _context = null;
    private LevelData _map = null;
    public boolean _isLevelChange = false;
    public boolean _isHitDoor = false;

    public LevelManager(final Context context, final BitmapPool pool) {
        _pool = pool;
        _context = context;
        _map = new LevelInfo();
        loadMapNames();
        loadMaps();
    }

    private void loadMapNames() {
        _levelMaps.put(1, Config.LEVEL_MAP1);
        _levelMaps.put(2, Config.LEVEL_MAP2);
        _levelMaps.put(3, Config.LEVEL_MAP3);
    }

    public void update(final double dt) {
        if (_isLevelChange) { return; }
        for (Entity e : _entities) {
            e.update(dt);
        }
        _remainingCoin = _totalLevelCoin - _levelCollectedCoin;
        checkCollisions();
        addAndRemoveEntities();
    }

    private void checkCollisions() {
        final int count = _entities.size();
        Entity a, b;
        for (int i = 0; i < count-1 && !_isLevelChange; i++) {
            a = _entities.get(i);
            for (int j = i+1; j < count && !_isLevelChange; j++) {
                b = _entities.get(j);
                if (a.isColliding(b)) {
                    a.onCollision(b);
                    b.onCollision(a);
                }
            }
        }
        _isLevelChange = false;
    }

    public void restart() {
        _collectedCoin = 0;
        loadMaps();
    }

    public void levelChanged() {
        int _playerHealth = _player._health;
        loadMaps();
        _player._health = _playerHealth;
    }

    private void loadMaps() {
        cleanup();
        BufferedReader reader;
        _map = new LevelInfo();
        String _line;
        String [] _values;
        _map.mTiles = new int[Config.MAP_HEIGHT][Config.MAP_WIDTH];
        int countLine = 0;
        //borrow code
        try {
            reader = new BufferedReader(
                    new InputStreamReader(_context.getAssets().open(_levelMaps.get(_levelNumber)), StandardCharsets.UTF_8));
            while ((_line = reader.readLine()) != null) {
                _values = _line.trim().split(_context.getString(R.string.comma));
                for (int i = 0; i < Config.MAP_WIDTH; i++) {
                    _map.mTiles[countLine][i] = Integer.parseInt(_values[i]);
                }
                countLine++;
            }
            //until there
            _map.updateLevelDimensions();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadMapAssets();
    }

    private void loadMapAssets() {
        _levelHeight = _map.mHeight;
        _levelWidth = _map.mWidth;

        for (int y = 0; y < _levelHeight; y++) {
            final int[] row = _map.getRow(y);
            for (int x = 0; x < row.length; x++) {
                final int tileID = row[x];
                if (tileID == LevelData.NO_TILE) { continue; }
                final String spriteName = _map.getSpriteName(tileID);
                createEntity(spriteName, x, y);
            }
        }
    }

    private void createEntity(final String spriteName, final int xpos, final int ypos) {
        Entity e = null;
        if (spriteName.equalsIgnoreCase(LevelData.PLAYER)) {
            e = new Player(spriteName, xpos, ypos);
            if (_player == null) {
                _player = (Player) e;
            }
        } else if(spriteName.equalsIgnoreCase(LevelData.ENEMY)) {
            e = new Enemy(spriteName, xpos, ypos);
        } else if (spriteName.equalsIgnoreCase(LevelData.COIN)) {
            e = new Coin(spriteName, xpos, ypos);
            _totalLevelCoin++;
        }
        else if (spriteName.equalsIgnoreCase(LevelData.DOOR)) {
            e = new Door(spriteName, xpos, ypos);
        } else if (spriteName.equalsIgnoreCase(LevelData.HEART)) {
            e = new Heart(spriteName, xpos, ypos);
        } else if (spriteName.equalsIgnoreCase(LevelData.SPEAR)) {
            e = new Spear(spriteName, xpos, ypos);
        } else {
            e = new StaticEntity(spriteName, xpos, ypos);
        }
        addEntity(e);
    }

    //end of update state
    private void addAndRemoveEntities() {
        for (Entity e : _entitiesToRemove) {
            _entities.remove(e);
        }
        for (Entity e : _entitiesToAdd) {
            _entities.add(e);
        }
        _entitiesToRemove.clear();
        _entitiesToAdd.clear();
    }

    public void addEntity(final Entity e) {
        if (e != null) {
            _entitiesToAdd.add(e);
        }
    }

    public void removeEntity(final Entity e) {
        if (e != null) {
            _entitiesToRemove.add(e);
        }
    }

    private void cleanup() {
        addAndRemoveEntities();
        for (Entity e : _entities) {
            e.destroy();
        }
        _totalLevelCoin = 0;
        _levelCollectedCoin = 0;
        _entities.clear();
        _player = null;
        _pool.empty();
    }

    public void destroy() {
        cleanup();
    }
}