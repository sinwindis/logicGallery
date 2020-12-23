package com.sinwindis.logicgallery.game;

public class MacroMode {

    public static final int CHECK = 0;
    public static final int UNCHECK = 1;
    public static final int X = 2;
    public static final int UNX = 3;
    public static final int LOCK = 4;
    public static final int UNLOCK = 5;
    public static final int HINT = 6;

    private int macroMode = CHECK;

    public void setMacroMode(int macroMode) {
        this.macroMode = macroMode;
    }

    public int getMacroMode() {
        return this.macroMode;
    }
}
