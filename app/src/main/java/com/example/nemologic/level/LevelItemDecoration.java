package com.example.nemologic.level;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class LevelItemDecoration extends RecyclerView.ItemDecoration {

    private final int marginLength;

    public LevelItemDecoration(int marginLength) {
        this.marginLength = marginLength;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {

        if (parent.getChildAdapterPosition(view) % 2 == 1) {
            outRect.left = marginLength;
        }
        outRect.bottom = marginLength;
    }
}