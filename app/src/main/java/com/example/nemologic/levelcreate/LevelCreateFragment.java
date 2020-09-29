package com.example.nemologic.levelcreate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.nemologic.R;

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