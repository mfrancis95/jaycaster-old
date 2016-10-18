package com.amf.jaycaster.graphics;

public class Lighting {
    
    public static final Lighting DEFAULT = new Lighting();
    
    public double alpha;
    
    public int color;
    
    public Lighting() {
        this(0, 0);
    }
    
    public Lighting(Lighting lighting) {
        this(lighting.color, lighting.alpha);
    }
    
    public Lighting(int color, double alpha) {
        this.color = color;
        this.alpha = alpha;
    }
    
}