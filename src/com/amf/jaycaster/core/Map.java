package com.amf.jaycaster.core;

import com.amf.jaycaster.graphics.Bitmap;
import com.amf.jaycaster.graphics.Fog;
import com.amf.jaycaster.tile.Tile;

public class Map {
    
    public static enum LightDirection {

        NORTH, SOUTH, EAST, WEST,
        NORTHEAST, NORTHWEST,
        SOUTHEAST, SOUTHWEST

    }
    
    public final int columns, rows;
    
    public int ambientColor, experimentalHeight;
    
    public double ambientAlpha;
    
    public Bitmap background;
    
    public boolean experimentalEffect;
    
    public Fog fog;
    
    public LightDirection lightDirection;
    
    private final Tile[] tiles;

    public Map(int rows, int columns) {
        this.tiles = new Tile[rows * columns];
        for (int i = 0; i < tiles.length; i++) {
            this.tiles[i] = new Tile();
        }
        this.rows = rows;
        this.columns = columns;
        experimentalHeight = 1;
        fog = new Fog(0, 0, 0);
        lightDirection = LightDirection.NORTHEAST;
    }
    
    public Tile getTile(Vector vector) {
        return getTile((int) vector.x, (int) vector.y);
    }
    
    public Tile getTile(double x, double y) {
        return getTile((int) x, (int) y);
    }
    
    public Tile getTile(int x, int y) {
        return tiles[x + y * rows];
    }    
    
}