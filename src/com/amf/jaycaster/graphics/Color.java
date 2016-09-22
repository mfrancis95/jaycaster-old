package com.amf.jaycaster.graphics;

public class Color {
    
    public static int blend(int oldColor, int newColor, double alpha) {
        if (alpha <= 0) {
            return oldColor;
        }
        if (alpha >= 1) {
            return newColor;
        }
        int red = (int) (alpha * extractRed(newColor) + (1 - alpha) * extractRed(oldColor));
        int green = (int) (alpha * extractGreen(newColor) + (1 - alpha) * extractGreen(oldColor));
        int blue = (int) (alpha * extractBlue(newColor) + (1 - alpha) * extractBlue(oldColor));
        return fromRGB(red, green, blue);
    }
    
    public static int extractBlue(int color) {
        return color & 0xFF;
    }
    
    public static int extractGreen(int color) {
        return (color >> 8) & 0xFF;
    }
    
    public static int extractRed(int color) {
        return (color >> 16) & 0xFF;
    }
    
    public static int fromRGB(int red, int green, int blue) {
        if (red < 0) {
            red = 0;
        }
        else if (red > 255) {
            red = 255;
        }
        if (green < 0) {
            green = 0;
        }
        else if (green > 255) {
            green = 255;
        }
        if (blue < 0) {
            blue = 0;
        }
        else if (blue > 255) {
            blue = 255;
        }
        red = (red << 8) + green;
        return (red << 8) + blue;
    }
    
}