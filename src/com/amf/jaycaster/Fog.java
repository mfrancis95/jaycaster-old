package com.amf.jaycaster;

public class Fog {
    
    public int color;
    
    public double alpha, distance, distanceSquared;
    
    public Fog(int color, double alpha, double distance) {
        this.color = color;
        this.alpha = alpha;
        this.distance = distance;
        distanceSquared = distance * distance;
    }
    
    public int blend(int color, double distance) {
        if (alpha >= 1) {
            return distance >= this.distance ? this.color : Color.blend(color, this.color, distance / this.distance);
        }
        return Color.blend(color, this.color, distance >= this.distance ? alpha : alpha * distance / this.distance);
    }
    
    public int blendSquared(int color, double distanceSquared) {
        if (alpha >= 1) {
            return distanceSquared >= this.distanceSquared ? this.color : Color.blend(color, this.color, distanceSquared / this.distanceSquared);
        }
        return Color.blend(color, this.color, distanceSquared >= this.distanceSquared ? alpha : alpha * distanceSquared / this.distanceSquared);
    }
    
}