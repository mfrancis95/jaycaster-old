package com.amf.jaycaster;

public class Vector {
    
    public double x, y;
    
    public Vector() {
        this(0, 0);
    }
    
    public Vector(double degrees) {
        this(FastMath.cos(degrees), FastMath.sin(degrees));
    }
    
    public Vector(Vector vector) {
        this(vector.x, vector.y);
    }
    
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public void add(Vector vector) {
        x += vector.x;
        y += vector.y;
    }
    
    public double length() {
        return Math.sqrt(x * x + y * y);
    }
    
    public double lengthSquared() {
        return x * x + y * y;
    }
    
    public void normalise() {
        double length = length();
        if (length > 0) {
            scale(1 / length);
        }
    }
    
    public void rotate(double degrees) {
        double cos = FastMath.cos(degrees), sin = FastMath.sin(degrees);
        this.x = this.x * cos - this.y * sin;
        this.y = this.x * sin + this.y * cos;
    }
    
    public void scale(double scale) {
        x *= scale;
        y *= scale;
    }
    
    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public void set(Vector vector) {
        x = vector.x;
        y = vector.y;
    }
    
    public void subtract(Vector vector) {
        x -= vector.x;
        y -= vector.y;
    }
    
}