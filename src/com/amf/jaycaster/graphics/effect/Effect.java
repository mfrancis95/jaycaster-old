package com.amf.jaycaster.graphics.effect;

import com.amf.jaycaster.graphics.Bitmap;

public interface Effect {
    
    Effect IDENTITY = (Bitmap bitmap, int x, int y) -> bitmap.getPixel(x, y);
    
    int affect(Bitmap bitmap, int x, int y);
    
}