package com.amf.jaycaster.entity;

import com.amf.jaycaster.graphics.Animation;
import com.amf.jaycaster.core.Game;
import com.amf.jaycaster.core.Map;
import com.amf.jaycaster.core.Vector;
import com.amf.jaycaster.tile.Tile;
import com.amf.jaycaster.graphics.effect.Effect;

public class Entity {
    
    public static enum Direction {
        FORWARD, BACKWARD, LEFT, RIGHT
    }
    
    public Animation currentAnimation;
    
    public boolean collidable = true, removed, visible = true;
    
    public Vector direction = new Vector(1, 0), position = new Vector(), scale = new Vector(1, 1), velocity = new Vector();
    
    public Effect effect = Effect.IDENTITY;
    
    public double opacity = 1, radius, viewDistanceSquared = Double.MAX_VALUE, yOffset;
    
    private Tile tile;
    
    private boolean aabbIntersection(double x1, double y1, double x2, double y2, double r1, double r2) {
        return x1 < x2 + r2 && x1 + r1 > x2 && y1 < y2 + r2 && y1 + r1 > y2;
    }
    
    private boolean intersectsEntity(Map map, double x, double y) {
        for (Entity entity : map.getTile(x, y).entities) {
            if (entity != this && entity.collidable && aabbIntersection(x, y, entity.position.x, entity.position.y, radius * 2, entity.radius * 2)) {
                return true;
            }
        }
        for (Tile tile : map.getNeighbors(x, y)) {
            if (tile != null) {
                for (Entity entity : tile.entities) {
                    if (entity != this && entity.collidable && aabbIntersection(x, y, entity.position.x, entity.position.y, radius * 2, entity.radius * 2)) {
                       return true;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean intersectsTile(Map map, double x, double y) {
        return map.getTile(x + radius, y + radius).isImpenetrable() || map.getTile(x + radius, y - radius).isImpenetrable() || map.getTile(x - radius, y + radius).isImpenetrable() || map.getTile(x - radius, y - radius).isImpenetrable();
    }
    
    public void move(Map map) {
        double deltaX = position.x + velocity.x, deltaY = position.y + velocity.y;
        boolean addX = false;
        if (!intersectsTile(map, deltaX, position.y) && !intersectsEntity(map, deltaX, position.y)) {
            addX = true;
        }
        if (!intersectsTile(map, position.x, deltaY) && !intersectsEntity(map, position.x, deltaY)) {
            position.y = deltaY;
        }
        if (addX) {
            position.x = deltaX;
        }
    }
    
    public void update(Game game) {
        currentAnimation.nextFrame();
        move(game.map);      
        Tile tile = game.map.getTile(position);
        if (tile != this.tile) {
            if (this.tile != null) {
                this.tile.triggerLeave(game, this);
            }
            tile.triggerEnter(game, this);
            this.tile = tile;
        }
    }
    
}