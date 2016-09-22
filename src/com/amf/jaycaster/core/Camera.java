package com.amf.jaycaster.core;

public class Camera {
    
    public Vector direction, position;
    
    public double halfFov, nearClippingSquared;
    
    public Camera(double fov) {
        halfFov = fov / 2;
        position = new Vector();
        direction = new Vector(1, 0);
    }
    
    public Camera(double fov, double nearClipping, Vector position, Vector direction) {
        halfFov = fov / 2;
        nearClippingSquared = nearClipping * nearClipping;
        this.position = position;
        this.direction = direction;
    }
    
}