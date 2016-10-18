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
    
    public boolean destroyed, visible;
    
    public Vector direction, position, scale, velocity;
    
    public Effect effect;
    
    public double opacity, radius, viewDistanceSquared, yOffset;
    
    private Tile tile;
    
    public Entity() {
        visible = true;
        position = new Vector();
        direction = new Vector(1, 0);
        scale = new Vector(1, 1);
        velocity = new Vector();
        effect = Effect.IDENTITY;
        opacity = 1;
        viewDistanceSquared = Double.MAX_VALUE;
    }
    
    public void move(Map map) {
        double deltaX = position.x + velocity.x, deltaY = position.y + velocity.y;
        boolean addX = false;
        if (!map.getTile(deltaX + radius, position.y + radius).isImpenetrable() && !map.getTile(deltaX + radius, position.y - radius).isImpenetrable() && !map.getTile(deltaX - radius, position.y + radius).isImpenetrable() && !map.getTile(deltaX - radius, position.y - radius).isImpenetrable()) {
            addX = true;
        }
        if (!map.getTile(position.x + radius, deltaY + radius).isImpenetrable() && !map.getTile(position.x - radius, deltaY + radius).isImpenetrable() && !map.getTile(position.x + radius, deltaY - radius).isImpenetrable() && !map.getTile(position.x - radius, deltaY - radius).isImpenetrable()) {
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