package com.amf.jaycaster;

public class FastMath {
    
    private static final double[] cos, sin, tan;
    
    static {
        sin = new double[360];
        cos = new double[360];
        tan = new double[360];
        for (int i = 0; i < 360; i++) {
            double value = i * Math.PI / 180.0;
            sin[i] = Math.sin(value);
            cos[i] = Math.cos(value);
            tan[i] = Math.tan(value);
        }
    }
    
    public static double cos(double degrees) {
        return cos[degreesToIndex(degrees)];
    }
    
    private static int degreesToIndex(double degrees) {
        if (degrees < 0) {
            degrees = Math.abs(degrees + 360);
        }
        return (int) degrees % 360;
    }
    
    public static double sin(double degrees) {
        return sin[degreesToIndex(degrees)];
    }
    
    public static double tan(double degrees) {
        return tan[degreesToIndex(degrees)];
    }
    
}