package com.sinwindis.logicgallery.data.sharedpref;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class PropertyPreference extends SharedPreferenceManager {

    private final String NAME_HINT = "hint";
    private final String NAME_DATE = "date";

    public PropertyPreference(Context ctx) {
        super(ctx, "PROPERTY");
    }

    public int increaseHintCount() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int hintCount = getHintCount();
        editor.putInt("hint", hintCount + 1);
        editor.apply();

        return getHintCount();
    }

    public int setHintCount(int hintCount) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("hint", hintCount);
        editor.apply();

        return getHintCount();
    }

    public int getHintCount() {
        return sharedPreferences.getInt(NAME_HINT, 4);
    }

    public String getDate() {
        return sharedPreferences.getString(NAME_DATE, "00-00-0000");
    }

    public void setDate(String formattedDate) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME_DATE, formattedDate);
        editor.apply();
    }
}
