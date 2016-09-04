package com.amf.jaycaster;

public interface Effect {
    
    Effect IDENTITY = (Bitmap bitmap, int x, int y) -> bitmap.getPixel(x, y);
    
    int apply(Bitmap bitmap, int x, int y);
    
}