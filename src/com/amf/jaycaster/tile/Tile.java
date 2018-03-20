package com.amf.jaycaster.tile;

import com.amf.jaycaster.entity.Entity;
import com.amf.jaycaster.core.Game;
import com.amf.jaycaster.graphics.Lighting;
import com.amf.jaycaster.graphics.effect.Effect;
import java.util.LinkedList;
import java.util.List;

public class Tile {
    
    public static enum Type {
        FLOOR, SOLID, WALL, FAKE
    }
    
    public boolean backgroundCeiling, backgroundFloor, backgroundWall;
    
    public String ceilingBitmap, floorBitmap, wallBitmap;
    
    public Effect effect;
    
    public List<Entity> entities;
    
    public Lighting lighting;
    
    public Type type;
    
    private final List<TileEnterListener> enterListeners;
    
    private final List<TileLeaveListener> leaveListeners;
    
    public Tile() {
        effect = Effect.IDENTITY;
        entities = new LinkedList<>();
        lighting = Lighting.DEFAULT;
        type = Type.FLOOR;
        enterListeners = new LinkedList<>();
        leaveListeners = new LinkedList<>();
    }
    
    public void addEnterListener(TileEnterListener listener) {
        enterListeners.add(listener);
    }
    
    public void addLeaveListener(TileLeaveListener listener) {
        leaveListeners.add(listener);
    }
    
    public boolean isImpenetrable() {
        return type == Type.SOLID || type == Type.WALL;
    }
    
    public boolean isRaised() {
        return type == Type.WALL || type == Type.FAKE;
    }
    
    public void removeEnterListener(TileEnterListener listener) {
        enterListeners.remove(listener);
    }
    
    public void removeLeaveListener(TileLeaveListener listener) {
        leaveListeners.remove(listener);
    }
    
    public void setBitmaps(String bitmap) {
        ceilingBitmap = floorBitmap = wallBitmap = bitmap;
    }    
    
    public void triggerEnter(Game game, Entity entity) {
        entities.add(entity);
        for (TileEnterListener listener : enterListeners) {
            listener.onEnter(game, this, entity);
        }
    }
    
    public void triggerLeave(Game game, Entity entity) {
        entities.remove(entity);
        for (TileLeaveListener listener : leaveListeners) {
            listener.onLeave(game, this, entity);
        }
    }
    
}