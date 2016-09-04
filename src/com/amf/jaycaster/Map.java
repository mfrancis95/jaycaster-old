package com.amf.jaycaster;

public class Map {
    
    public static enum LightDirection {

        NORTH, SOUTH, EAST, WEST,
        NORTHEAST, NORTHWEST,
        SOUTHEAST, SOUTHWEST

    }
    
    public final int columns, rows;
    
    public int ambientColor, experimentalHeight;
    
    public double ambientFactor;
    
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
        lightDirection = LightDirection.NORTHEAST;
    }
    
    public Tile getTile(int x, int y) {
        return tiles[x + y * rows];
    }
    
    public Tile getTile(double x, double y) {
        return tiles[(int) x + (int) y * rows];
    }
    
}