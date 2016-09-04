package com.amf.jaycaster;

public class Tile {
    
    public static enum Type {
        FLOOR, SOLID, WALL
    }
    
    public boolean backgroundCeiling, backgroundFloor, backgroundWall;
    
    public Bitmap ceilingBitmap, floorBitmap, wallBitmap;
    
    public Effect effect = Effect.IDENTITY;
    
    public Type type;
    
    public Tile() {
        type = Type.FLOOR;
    }
    
    public boolean isImpenetrable() {
        return type == Type.SOLID || type == Type.WALL;
    }
    
}