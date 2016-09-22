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
    
    private Tile lastTile;
    
    public Entity() {
        position = new Vector();
        direction = new Vector(1, 0);
        scale = new Vector(1, 1);
        velocity = new Vector();
        effect = Effect.IDENTITY;
        opacity = 1;
        viewDistanceSquared = Double.MAX_VALUE;
        visible = true;
    }
    
    public void move(Map map) {
        double deltaX = position.x + velocity.x;
        double deltaY = position.y + velocity.y;
        if (!map.getTile(deltaX + radius, position.y + radius).isImpenetrable() && !map.getTile(deltaX + radius, position.y - radius).isImpenetrable() && !map.getTile(deltaX - radius, position.y + radius).isImpenetrable() && !map.getTile(deltaX - radius, position.y - radius).isImpenetrable()) {
            position.x += velocity.x;
        }
        if (!map.getTile(position.x + radius, deltaY + radius).isImpenetrable() && !map.getTile(position.x - radius, deltaY + radius).isImpenetrable() && !map.getTile(position.x + radius, deltaY - radius).isImpenetrable() && !map.getTile(position.x - radius, deltaY - radius).isImpenetrable()) {
            position.y += velocity.y;
        }
    }
    
    public void update(Game game) {
        currentAnimation.nextFrame();
        move(game.map);
        Tile tile = game.map.getTile(position);
        if (tile != lastTile) {
            if (lastTile != null) {
                lastTile.triggerLeave(game, this);
            }
            tile.triggerEnter(game, this);
            lastTile = tile;
        }
    }
    
}