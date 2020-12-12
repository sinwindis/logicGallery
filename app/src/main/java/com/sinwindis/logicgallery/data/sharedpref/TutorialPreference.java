package com.sinwindis.logicgallery.data.sharedpref;

import android.content.Context;
import android.content.SharedPreferences;

public class TutorialPreference extends SharedPreferenceManager {

    private final String NAME_TUTORIAL = "tutorial";

    public TutorialPreference(Context ctx) {
        super(ctx, "TUTORIAL");
    }

    public boolean isTutorialExperienced() {
        return sharedPreferences.getBoolean(NAME_TUTORIAL, false);
    }

    public void setTutorialExperienced() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NAME_TUTORIAL, true);
        editor.apply();
    }
}
