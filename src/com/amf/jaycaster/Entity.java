package com.amf.jaycaster;

public class Entity {
    
    public static enum Direction {
        FORWARD, BACKWARD, LEFT, RIGHT
    }
    
    public Animation currentAnimation;
    
    public boolean destroyed, visible;
    
    public Vector direction, position, scale;
    
    public double opacity, radius, viewDistanceSquared, yOffset;
    
    public Entity() {
        position = new Vector();
        direction = new Vector(1, 0);
        scale = new Vector(1, 1);
        opacity = 1;
        viewDistanceSquared = Double.MAX_VALUE;
        visible = true;
    }
    
    public void move(Map map, Direction direction, double speed) {
        Vector vector;
        switch (direction) {
            case FORWARD:
                vector = new Vector(this.direction);
                break;
            case BACKWARD:
                vector = new Vector(-this.direction.x, -this.direction.y);
                break;
            case LEFT:
                vector = new Vector(this.direction.y, -this.direction.x);
                break;
            default:
                vector = new Vector(-this.direction.y, this.direction.x);
        }
        vector.scale(speed);
        double deltaX = position.x + vector.x;
        double deltaY = position.y + vector.y;
        if (!map.getTile(deltaX + radius, position.y + radius).isImpenetrable() && !map.getTile(deltaX + radius, position.y - radius).isImpenetrable() && !map.getTile(deltaX - radius, position.y + radius).isImpenetrable() && !map.getTile(deltaX - radius, position.y - radius).isImpenetrable()) {
            position.x += vector.x;
        }
        if (!map.getTile(position.x + radius, deltaY + radius).isImpenetrable() && !map.getTile(position.x - radius, deltaY + radius).isImpenetrable() && !map.getTile(position.x + radius, deltaY - radius).isImpenetrable() && !map.getTile(position.x - radius, deltaY - radius).isImpenetrable()) {
            position.y += vector.y;
        }
    }
    
    public void update(Game game) {
        currentAnimation.nextFrame();
    }
    
}