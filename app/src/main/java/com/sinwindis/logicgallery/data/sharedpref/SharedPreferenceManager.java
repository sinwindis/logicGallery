package com.sinwindis.logicgallery.data.sharedpref;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferenceManager {

    protected final Context ctx;
    protected final SharedPreferences sharedPreferences;

    public SharedPreferenceManager(Context ctx, String prefName) {
        this.ctx = ctx;
        this.sharedPreferences = ctx.getSharedPreferences(prefName, MODE_PRIVATE);
    }
}
