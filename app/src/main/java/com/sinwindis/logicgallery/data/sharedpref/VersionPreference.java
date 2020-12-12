package com.sinwindis.logicgallery.data.sharedpref;

import android.content.Context;
import android.content.SharedPreferences;

public class VersionPreference extends SharedPreferenceManager {

    private final String NAME_VERSION_LEVEL = "levelVersion";

    public VersionPreference(Context ctx) {
        super(ctx, "VERSION");
    }

    public String getVersion() {
        return sharedPreferences.getString(NAME_VERSION_LEVEL, "0");
    }

    public void setVersion(String currentVersion) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME_VERSION_LEVEL, currentVersion);
        editor.apply();
    }
}
