package com.amf.jaycaster;

public class Entity {
    
    public static enum Direction {
        FORWARD, BACKWARD, LEFT, RIGHT
    }
    
    public Animation currentAnimation;
    
    public boolean destroyed, visible;
    
    public Vector direction, position, scale, velocity;
    
    public Effect effect = Effect.IDENTITY;
    
    public double opacity, radius, viewDistanceSquared, yOffset;
    
    public Entity() {
        position = new Vector();
        direction = new Vector(1, 0);
        scale = new Vector(1, 1);
        velocity = new Vector();
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
    }
    
}