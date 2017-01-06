package com.amf.jaycaster.core;

public class Camera {
    
    public Vector direction, position;
    
    public double halfFov, height = 0.5, nearClippingSquared, pitch = 0.5;
    
    public Camera(double fov) {
        this(fov, 0, new Vector(), new Vector(1, 0));
    }
    
    public Camera(double fov, double nearClipping, Vector position, Vector direction) {
        halfFov = fov / 2;
        nearClippingSquared = nearClipping * nearClipping;
        this.position = position;
        this.direction = direction;
    }
    
}