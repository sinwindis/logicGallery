package com.sinwindis.logicgallery.data.sharedpref;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class OptionPreference extends SharedPreferenceManager {

    private final String NAME_SMARTDRAG = "smartDrag";
    private final String NAME_ONELINEDRAG = "oneLineDrag";
    private final String NAME_AUTOX = "autoX";

    public OptionPreference(Context ctx) {
        super(ctx, "OPTION");
    }

    public void setSmartDrag(boolean isSmartDrag) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NAME_SMARTDRAG, isSmartDrag);
        editor.apply();
    }

    public void setOneLineDrag(boolean isOneLineDrag) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NAME_ONELINEDRAG, isOneLineDrag);
        editor.apply();
    }

    public void setAutoX(boolean isAutoX) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NAME_AUTOX, isAutoX);
        editor.apply();
    }

    public boolean isSmartDrag() {
        return sharedPreferences.getBoolean(NAME_SMARTDRAG, true);
    }

    public boolean isOneLineDrag() {
        return sharedPreferences.getBoolean(NAME_ONELINEDRAG, true);
    }

    public boolean isAutoX() {
        return sharedPreferences.getBoolean(NAME_AUTOX, false);
    }
}
