package com.amf.jaycaster;

public class Tile {
    
    public static enum Type {
        FLOOR, SOLID, WALL, FAKE
    }
    
    public boolean backgroundCeiling, backgroundFloor, backgroundWall;
    
    public Bitmap ceilingBitmap, floorBitmap, wallBitmap;
    
    public Effect effect;
    
    public Type type;
    
    public Tile() {
        effect = Effect.IDENTITY;
        type = Type.FLOOR;
    }
    
    public boolean isImpenetrable() {
        return type == Type.SOLID || type == Type.WALL;
    }
    
    public boolean isRaised() {
        return type == Type.WALL || type == Type.FAKE;
    }
    
    public void setBitmaps(Bitmap bitmap) {
        ceilingBitmap = floorBitmap = wallBitmap = bitmap;
    }
    
}