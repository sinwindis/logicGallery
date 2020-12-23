package com.sinwindis.logicgallery.game;

public class TouchMode {

    public static final int TOUCH_CHECK = 0;
    public static final int TOUCH_X = 1;
    public static final int TOUCH_LOCK = 2;
    public static final int TOUCH_HINT = 3;

    private int touchMode = TOUCH_CHECK;

    public TouchMode() {

    }

    public int getTouchMode() {
        return this.touchMode;
    }

    public void setTouchMode(int touchMode) {
        this.touchMode = touchMode;
    }

}
