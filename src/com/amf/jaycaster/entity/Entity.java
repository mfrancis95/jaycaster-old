package com.amf.jaycaster.entity;

import com.amf.jaycaster.graphics.Animation;
import com.amf.jaycaster.core.Game;
import com.amf.jaycaster.core.World;
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
    
    private boolean intersectsEntity(World level, double x, double y) {
        for (Entity entity : level.getTile(x, y).entities) {
            if (entity != this && entity.collidable && aabbIntersection(x, y, entity.position.x, entity.position.y, radius * 2, entity.radius * 2)) {
                return true;
            }
        }
        for (Tile tile : level.getNeighbors(x, y)) {
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
    
    private boolean intersectsTile(World world, double x, double y) {
        return world.getTile(x + radius, y + radius).isImpenetrable() || world.getTile(x + radius, y - radius).isImpenetrable() || world.getTile(x - radius, y + radius).isImpenetrable() || world.getTile(x - radius, y - radius).isImpenetrable();
    }
    
    public void move(World world) {
        double deltaX = position.x + velocity.x, deltaY = position.y + velocity.y;
        boolean addX = false;
        if (!intersectsTile(world, deltaX, position.y) && !intersectsEntity(world, deltaX, position.y)) {
            addX = true;
        }
        if (!intersectsTile(world, position.x, deltaY) && !intersectsEntity(world, position.x, deltaY)) {
            position.y = deltaY;
        }
        if (addX) {
            position.x = deltaX;
        }
    }
    
    public void update(Game game) {
        currentAnimation.nextFrame();
        move(game.world);      
        Tile tile = game.world.getTile(position);
        if (tile != this.tile) {
            if (this.tile != null) {
                this.tile.triggerLeave(game, this);
            }
            tile.triggerEnter(game, this);
            this.tile = tile;
        }
    }
    
}