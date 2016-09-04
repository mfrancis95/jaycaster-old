package com.amf.jaycaster;

public class Animation {
    
    public static final int REPETITIONS_INFINITE = -1;
    
    public Bitmap[] bitmaps;
    
    public int repetitions, ticksPerFrame;
    
    private int index, repetitionCount, tick;
    
    public Animation(Bitmap bitmap) {
        bitmaps = new Bitmap[] {bitmap};
    }
    
    public Animation(int ticksPerFrame) {
        this.ticksPerFrame = ticksPerFrame;
        repetitions = -1;
    }
    
    public Animation(int ticksPerFrame, int repetitions) {
        this.ticksPerFrame = ticksPerFrame;
        this.repetitions = repetitions;
    }
    
    public Bitmap getFrame() {
        return bitmaps[index];
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