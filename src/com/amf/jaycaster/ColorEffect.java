package com.amf.jaycaster;

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