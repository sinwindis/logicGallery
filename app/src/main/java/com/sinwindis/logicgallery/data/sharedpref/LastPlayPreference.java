package com.sinwindis.logicgallery.data.sharedpref;

import android.content.Context;
import android.content.SharedPreferences;

public class LastPlayPreference extends SharedPreferenceManager {

    private final String NAME_CUSTOM = "custom";
    private final String NAME_ID = "id";

    public LastPlayPreference(Context ctx) {
        super(ctx, "LASTPLAY");
    }

    public boolean isCustom() {
        return sharedPreferences.getBoolean(NAME_CUSTOM, false);
    }

    public int getLastPlayId() {
        return sharedPreferences.getInt(NAME_ID, -1);
    }

    public void setCustom(boolean isCustom) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NAME_CUSTOM, isCustom);
        editor.apply();
    }

    public void setId(int id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(NAME_ID, id);
        editor.apply();
    }
}
