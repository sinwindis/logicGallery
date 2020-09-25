package com.example.nemologic.levelcreate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.nemologic.R;
import com.example.nemologic.category.CategoryFragment;
import com.example.nemologic.data.DataManager;
import com.example.nemologic.data.DbOpenHelper;
import com.example.nemologic.data.SqlManager;
import com.example.nemologic.game.GameFragment;
import com.example.nemologic.mainactivity.MainActivity;
import com.example.nemologic.option.OptionDialog;

import java.sql.SQLException;

import static android.content.Context.MODE_PRIVATE;

public class LevelCreateFragment extends Fragment {

    Context ctx;

    public LevelCreateFragment(Context ctx) {
        // Required empty public constructor
        this.ctx = ctx;
    }

    @SuppressLint({"ClickableViewAccessibility"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Context mainActivityCtx = ctx;

        final View fragmentView = inflater.inflate(R.layout.fragment_levelcreate, container, false);

        return fragmentView;
    }
}