package com.amf.jaycaster.graphics;

public class Fog extends Lighting {
    
    public double distance, distanceSquared;
    
    private double[] fog, fogSquared;
    
    public Fog(Fog fog) {
        this(fog.color, fog.alpha, fog.distance);
    }
    
    public Fog(Lighting lighting, double distance) {
        this(lighting.color, lighting.alpha, distance);
    }
    
    public Fog(int color, double alpha, double distance) {
        super(color, alpha);
        this.distance = distance;
        distanceSquared = distance * distance;
    }
    
    public int blend(int color, double distance) {
        if (fog == null) {
            return Color.blend(color, this.color, distance >= this.distance ? alpha : alpha * distance / this.distance);
        }
        else {
            return Color.blend(color, this.color, distance >= this.distance ? alpha : fog[(int) distance]);
        }
    }
    
    public int blendSquared(int color, double distanceSquared) {
        if (fogSquared == null) {
            return Color.blend(color, this.color, distanceSquared >= this.distanceSquared ? alpha : alpha * distanceSquared / this.distanceSquared);
        }
        else {
            return Color.blend(color, this.color, distanceSquared >= this.distanceSquared ? alpha : fogSquared[(int) distanceSquared]);
        }
    }
    
    public boolean isOptimised() {
        return fog != null;
    }
    
    public void setDistance(double distance) {
        this.distance = distance;
        distanceSquared = distance * distance;
    }
    
    public void setOptimised(boolean optimised) {
        if (optimised) {
            fog = new double[(int) Math.ceil(distance)];
            for (int i = 0; i < fog.length; i++) {
                fog[i] = alpha * i / distance;
            }
            fogSquared = new double[(int) Math.ceil(distanceSquared)];
            for (int i = 0; i < fogSquared.length; i++) {
                fogSquared[i] = alpha * i / distanceSquared;
            }
        }
        else {
            fog = fogSquared = null;
        }
    }
    
}