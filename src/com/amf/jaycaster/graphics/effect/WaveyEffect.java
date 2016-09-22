package com.amf.jaycaster.graphics.effect;

import com.amf.jaycaster.graphics.Bitmap;
import com.amf.jaycaster.core.FastMath;
import com.amf.jaycaster.core.Game;
import com.amf.jaycaster.core.Vector;

public class WaveyEffect implements Effect {
    
    public Vector amplitude, frequency, waveFactor;
    
    public boolean animated, xWaveFactorX, yWaveFactorY;
    
    public double phase;
    
    public WaveyEffect(Vector amplitude, Vector frequency, Vector waveFactor) {
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.waveFactor = waveFactor;
        animated = true;
    }
    
    public WaveyEffect(Vector amplitude, Vector frequency, Vector waveFactor, double phase) {
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.waveFactor = waveFactor;
        this.phase = phase;
    }
    
    public int affect(Bitmap bitmap, int x, int y) {
        int newX = (int) (amplitude.x * FastMath.sin(Math.toDegrees(frequency.x * (animated ? Game.tick : phase) + (xWaveFactorX ? x : y) * waveFactor.x)) + x) % bitmap.width;
        if (newX < 0) {
            newX += bitmap.width;
        }
        int newY = (int) (amplitude.y * FastMath.sin(Math.toDegrees(frequency.y * (animated ? Game.tick : phase) + (yWaveFactorY ? y : x) * waveFactor.y)) + y) % bitmap.height;
        if (newY < 0) {
            newY += bitmap.height;
        }
        return bitmap.getPixel(newX, newY);
    }
    
}