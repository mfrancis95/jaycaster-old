package com.amf.jaycaster.core;

import com.amf.jaycaster.graphics.Fog;
import com.amf.jaycaster.graphics.Lighting;
import com.amf.jaycaster.tile.Tile;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;

public class World {
    
    public static final int DIRECTION_NORTH = 0;
    public static final int DIRECTION_SOUTH = 1;
    public static final int DIRECTION_EAST = 2;
    public static final int DIRECTION_WEST = 3;
    public static final int DIRECTION_NORTHEAST = 4;
    public static final int DIRECTION_NORTHWEST = 5;
    public static final int DIRECTION_SOUTHEAST = 6;
    public static final int DIRECTION_SOUTHWEST = 7;
    
    public final int columns, rows;
    
    public String backgroundBitmap;
    
    public boolean experimentalEffect;
    
    public int experimentalHeight = 1;
    
    public Fog fog = new Fog(0, 0, 0);
    
    public int lightDirection = DIRECTION_NORTHEAST;
    
    private final Tile[] neighbors = new Tile[8], tiles;
    
    public World(URL url) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String[] split = reader.readLine().split(",");
            rows = Integer.parseInt(split[0]);
            columns = Integer.parseInt(split[1]);
            tiles = new Tile[rows * columns];
            for (int i = 0; i < tiles.length; i++) {
                tiles[i] = new Tile();
            }
            while (reader.ready()) {
                split = reader.readLine().split(",");
                Tile tile = getTile(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                tile.type = Tile.Type.valueOf(split[2]);
                tile.ceilingBitmap = split[3];
                tile.floorBitmap = split[4];
                tile.wallBitmap = split[5];
                tile.backgroundCeiling = Boolean.parseBoolean(split[6]);
                tile.backgroundFloor = Boolean.parseBoolean(split[7]);
                tile.backgroundWall = Boolean.parseBoolean(split[8]);
            }
        }
    }

    public World(int rows, int columns) {
        tiles = new Tile[rows * columns];
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = new Tile();
        }
        this.rows = rows;
        this.columns = columns;
    }
    
    public Tile[] getNeighbors(double x, double y) {
        return getNeighbors((int) x, (int) y);
    }
    
    public Tile[] getNeighbors(int x, int y) {
        neighbors[DIRECTION_NORTH] = getTile(x, y - 1);
        neighbors[DIRECTION_SOUTH] = getTile(x, y + 1);
        neighbors[DIRECTION_EAST] = getTile(x + 1, y);
        neighbors[DIRECTION_WEST] = getTile(x - 1, y);
        neighbors[DIRECTION_NORTHEAST] = getTile(x + 1, y - 1);
        neighbors[DIRECTION_NORTHWEST] = getTile(x - 1, y - 1);
        neighbors[DIRECTION_SOUTHEAST] = getTile(x + 1, y + 1);
        neighbors[DIRECTION_SOUTHWEST] = getTile(x - 1, y + 1);     
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
    
    public void light(int x, int y, Lighting lighting, boolean lightRaised) {
        getTile(x, y).lighting = lighting;
        Lighting dimmer = new Lighting(lighting);
        dimmer.alpha += dimmer.alpha;
        Lighting evenDimmer = new Lighting(lighting);
        evenDimmer.alpha += evenDimmer.alpha + evenDimmer.alpha;
        getNeighbors(x, y);
        for (int i = DIRECTION_NORTH; i <= DIRECTION_WEST; i++) {
            Tile tile = neighbors[i];
            if (tile != null && (lightRaised || !tile.isRaised())) {
                tile.lighting = dimmer;
            }
        }
        for (int i = DIRECTION_NORTHEAST; i <= DIRECTION_SOUTHWEST; i++) {
            Tile tile = neighbors[i];
            if (tile != null && (lightRaised || !tile.isRaised())) {
                tile.lighting = evenDimmer;
            }
        }
    }
    
    public void save(String file) throws FileNotFoundException {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.printf("%d,%d\n", rows, columns);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    Tile tile = getTile(i, j);
                    writer.print(tile.type.toString());
                    writer.printf(",%s,%s,%s", tile.ceilingBitmap, tile.floorBitmap, tile.wallBitmap);
                    writer.printf(",%b,%b,%b\n", tile.backgroundCeiling, tile.backgroundFloor, tile.backgroundWall);
                }
            }
        }
    }
    
}