package com.sinwindis.logicgallery.data;

import android.content.Context;

import com.sinwindis.logicgallery.data.sharedpref.PropertyPreference;

public class HintManager {
    private int hintCount;
    private final PropertyPreference propertyPreference;

    public HintManager(Context ctx) {
        propertyPreference = new PropertyPreference(ctx);
        hintCount = propertyPreference.getHintCount();
    }

    public int getHintCount() {
        return this.hintCount;
    }

    public boolean useHint(int hintCount) {
        if (this.hintCount < hintCount) {
            return false;
        }
        this.hintCount -= hintCount;
        return true;
    }

    public void setHintCount(int hintCount) {
        this.hintCount = hintCount;
    }

    public void apply() {
        propertyPreference.setHintCount(hintCount);
    }
}