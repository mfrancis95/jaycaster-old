package com.amf.jaycaster;

public class ScrollingEffect implements Effect {
    
    public Vector velocity;
    
    public ScrollingEffect(Vector velocity) {
        this.velocity = velocity;
    }
    
    public int apply(Bitmap bitmap, int x, int y) {
        int newX = (int) (velocity.x * Game.tick + x) % bitmap.width;
        if (newX < 0) {
            newX += bitmap.width;
        }
        int newY = (int) (velocity.y * Game.tick + y) % bitmap.height;
        if (newY < 0) {
            newY += bitmap.height;
        }
        return bitmap.getPixel(newX, newY);
    }
    
}