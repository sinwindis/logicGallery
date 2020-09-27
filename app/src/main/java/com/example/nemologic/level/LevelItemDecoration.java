package com.example.nemologic.level;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class LevelItemDecoration extends RecyclerView.ItemDecoration {

    private int marginSize;
    private int rowItemNum;

    public LevelItemDecoration(int marginSize, int rowItemNum) {
        this.marginSize = marginSize;
        this.rowItemNum = rowItemNum;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {

        if ((parent.getChildAdapterPosition(view) % rowItemNum) < (rowItemNum - 1)) {
            outRect.right = marginSize;
        }
        else
        {
            Log.d("LevelItemDecoration", "is there any case?");
            outRect.right = 0;
        }
        outRect.bottom = marginSize;
    }
}