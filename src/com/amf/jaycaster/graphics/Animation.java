package com.amf.jaycaster.graphics;

public class Animation {
    
    public static final int REPETITIONS_INFINITE = -1;
    
    public String[] bitmaps;
    
    public int repetitions, ticksPerFrame;
    
    private int index, repetitionCount, tick;
    
    public Animation(String bitmap) {
        bitmaps = new String[] {bitmap};
    }
    
    public Animation(int ticksPerFrame, String... bitmaps) {
        this(ticksPerFrame, REPETITIONS_INFINITE, bitmaps);
    }
    
    public Animation(int ticksPerFrame, int repetitions, String... bitmaps) {
        this.ticksPerFrame = ticksPerFrame;
        this.repetitions = repetitions;
        this.bitmaps = bitmaps;
    }
    
    public String getFrame() {
        return bitmaps[index];
    }
    
    public boolean isFinished() {
        return repetitions >= 0 && repetitionCount > repetitions;
    }
    
    public void nextFrame() {
        if (repetitions < 0) {
            if (++tick > ticksPerFrame) {
                if (++index >= bitmaps.length) {
                    index = 0;
                }
                tick = 0;
            }
        }
        else if (repetitionCount <= repetitions) {
            if (++tick > ticksPerFrame) {
                if (index + 1 >= bitmaps.length) {
                    if (++repetitionCount <= repetitions) {
                        index = 0;
                    }
                }
                else {
                    index++;
                }
                tick = 0;
            }
        }
    }
    
    public void reset() {
        index = repetitionCount = tick = 0;
    }
    
}