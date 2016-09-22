package com.amf.jaycaster.graphics.effect;

import com.amf.jaycaster.graphics.Bitmap;
import com.amf.jaycaster.graphics.Color;

public class ColorEffect implements Effect {
    
    public double alpha;
    
    public int color;
    
    public ColorEffect(int color, double alpha) {
        this.color = color;
        this.alpha = alpha;
    }
    
    public int affect(Bitmap bitmap, int x, int y) {
        return Color.blend(bitmap.getPixel(x, y), this.color, alpha);
    }
    
}