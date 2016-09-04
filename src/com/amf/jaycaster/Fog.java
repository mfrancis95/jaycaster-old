package com.amf.jaycaster;

public class Fog {
    
    public double alpha, distance, distanceSquared;
    
    public int color;
    
    private double[] fog, fogSquared;
    
    public Fog(int color, double alpha, double distance) {
        this.color = color;
        this.alpha = alpha;
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