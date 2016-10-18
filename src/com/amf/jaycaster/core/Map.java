package com.amf.jaycaster.core;

import com.amf.jaycaster.graphics.Bitmap;
import com.amf.jaycaster.graphics.Fog;
import com.amf.jaycaster.graphics.Lighting;
import com.amf.jaycaster.tile.Tile;

public class Map {
    
    public static enum LightDirection {

        NORTH, SOUTH, EAST, WEST,
        NORTHEAST, NORTHWEST,
        SOUTHEAST, SOUTHWEST

    }
    
    public static final int NEIGHBOR_NORTH = 0;
    public static final int NEIGHBOR_SOUTH = 1;
    public static final int NEIGHBOR_EAST = 2;
    public static final int NEIGHBOR_WEST = 3;
    public static final int NEIGHBOR_NORTHEAST = 4;
    public static final int NEIGHBOR_NORTHWEST = 5;
    public static final int NEIGHBOR_SOUTHEAST = 6;
    public static final int NEIGHBOR_SOUTHWEST = 7;
    
    public final int columns, rows;
    
    public Bitmap background;
    
    public boolean experimentalEffect;
    
    public int experimentalHeight;
    
    public Fog fog;
    
    public LightDirection lightDirection;
    
    private final Tile[] neighbors, tiles;

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
        neighbors = new Tile[8];
    }
    
    public Tile[] getNeighbors(int x, int y) {
        neighbors[NEIGHBOR_NORTH] = getTile(x, y - 1);
        neighbors[NEIGHBOR_SOUTH] = getTile(x, y + 1);
        neighbors[NEIGHBOR_EAST] = getTile(x + 1, y);
        neighbors[NEIGHBOR_WEST] = getTile(x - 1, y);
        neighbors[NEIGHBOR_NORTHEAST] = getTile(x + 1, y - 1);
        neighbors[NEIGHBOR_NORTHWEST] = getTile(x - 1, y - 1);
        neighbors[NEIGHBOR_SOUTHEAST] = getTile(x + 1, y + 1);
        neighbors[NEIGHBOR_SOUTHWEST] = getTile(x - 1, y + 1);     
        return neighbors;
    }
    
    public Tile getTile(Vector vector) {
        return getTile((int) vector.x, (int) vector.y);
    }
    
    public Tile getTile(double x, double y) {
        return getTile((int) x, (int) y);
    }
    
    public Tile getTile(int x, int y) {
        try {
            return tiles[x + y * rows];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return null;
        }
    }
    
    public void light(int x, int y, Lighting lighting, boolean lightWalls) {
        getTile(x, y).lighting = lighting;
        Lighting dimmer = new Lighting(lighting);
        dimmer.alpha += dimmer.alpha;
        Lighting evenDimmer = new Lighting(lighting);
        evenDimmer.alpha += evenDimmer.alpha + evenDimmer.alpha;
        getNeighbors(x, y);
        for (int i = 0; i < 4; i++) {
            Tile tile = neighbors[i];
            if (tile != null && (lightWalls || !tile.isRaised())) {
                tile.lighting = dimmer;
            }
        }
        for (int i = 4; i < 8; i++) {
            Tile tile = neighbors[i];
            if (tile != null && (lightWalls || !tile.isRaised())) {
                tile.lighting = evenDimmer;
            }
        }
    }
    
}