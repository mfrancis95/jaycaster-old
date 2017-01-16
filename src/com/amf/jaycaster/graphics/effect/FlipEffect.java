package com.amf.jaycaster.graphics.effect;

import com.amf.jaycaster.graphics.Bitmap;

public class FlipEffect implements Effect {
    
    public boolean horizontal, vertical;
    
    public FlipEffect(boolean horizontal, boolean vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public int affect(Bitmap bitmap, int x, int y) {
        if (horizontal) {
            x = bitmap.width - x - 1;
        }
        if (vertical) {
            y = bitmap.height - y - 1;
        }
        return bitmap.getPixel(x, y);
    }
    
}