package com.amf.jaycaster;

public class WaveyEffect implements Effect {
    
    public Vector amplitude, frequency, waveFactor;
    
    public boolean xWaveFactorX, yWaveFactorY;
    
    public WaveyEffect(Vector amplitude, Vector frequency, Vector waveFactor) {
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.waveFactor = waveFactor;
    }
    
    public int apply(Bitmap bitmap, int x, int y) {
        int newX = (int) (amplitude.x * FastMath.sin(FastMath.toDegrees(frequency.x * Game.tick + (xWaveFactorX ? x : y) * waveFactor.x)) + x) % bitmap.width;
        if (newX < 0) {
            newX += bitmap.width;
        }
        int newY = (int) (amplitude.y * FastMath.sin(FastMath.toDegrees(frequency.y * Game.tick + (yWaveFactorY ? y : x) * waveFactor.y)) + y) % bitmap.height;
        if (newY < 0) {
            newY += bitmap.height;
        }
        return bitmap.getPixel(newX, newY);
    }
    
}