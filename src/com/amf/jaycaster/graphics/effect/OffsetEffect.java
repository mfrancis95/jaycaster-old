package com.amf.jaycaster.graphics.effect;

import com.amf.jaycaster.graphics.Bitmap;
import com.amf.jaycaster.core.Game;
import com.amf.jaycaster.core.Vector;

public class OffsetEffect implements Effect {
    
    public boolean animated;
    
    public Vector offset;
    
    public OffsetEffect(Vector offset) {
        this.offset = offset;
    }
    
    public OffsetEffect(Vector offset, boolean animated) {
        this.offset = offset;
        this.animated = animated;
    }
    
    public int affect(Bitmap bitmap, int x, int y) {
        int newX = (int) ((animated ? (offset.x * Game.tick) : offset.x) + x) % bitmap.width;
        if (newX < 0) {
            newX += bitmap.width;
        }
        int newY = (int) ((animated ? (offset.y * Game.tick) : offset.y) + y) % bitmap.height;
        if (newY < 0) {
            newY += bitmap.height;
        }
        return bitmap.getPixel(newX, newY);
    }
    
}