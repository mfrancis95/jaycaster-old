package com.amf.jaycaster.core;

public class FastMath {
    
    private static final double[] cos, sin, tan;
    
    static {
        sin = new double[360];
        cos = new double[360];
        tan = new double[360];
        for (int i = 0; i < 360; i++) {
            double radians = Math.toRadians(i);
            sin[i] = Math.sin(radians);
            cos[i] = Math.cos(radians);
            tan[i] = Math.tan(radians);
        }
    }
    
    public static double cos(double degrees) {
        return cos[degreesToIndex(degrees)];
    }
    
    private static int degreesToIndex(double degrees) {
        int index = (int) degrees % 360;
        return index < 0 ? index + 360 : index;
    }
    
    public static double sin(double degrees) {
        return sin[degreesToIndex(degrees)];
    }
    
    public static double tan(double degrees) {
        return tan[degreesToIndex(degrees)];
    }
    
}