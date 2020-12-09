package com.sinwindis.logicgallery.tutorial;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class GameBoardBorder extends RecyclerView.ItemDecoration {

    private final Drawable normalBorder;
    private final int width;

    /**
     * Custom divider will be used
     */
    public GameBoardBorder(Context context, int normalBorder, int width) {
        this.normalBorder = ContextCompat.getDrawable(context, normalBorder);
        this.width = width;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, RecyclerView parent, @NonNull RecyclerView.State state) {

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {

            View child = parent.getChildAt(i);
            int positionX = i % width;

            if (i < childCount - width) {
                int left = child.getLeft();
                int right = child.getRight();
                int top = child.getBottom() - normalBorder.getIntrinsicHeight();
                int bottom = child.getBottom();

                normalBorder.setBounds(left, top, right, bottom);
                normalBorder.draw(c);
            }


            if (positionX < width - 1) {
                //그 외에는 일반 줄을 그린다.
                int left = child.getRight() - normalBorder.getIntrinsicWidth();
                int right = child.getRight();
                int top = child.getTop();
                int bottom = child.getBottom();

                normalBorder.setBounds(left, top, right, bottom);
                normalBorder.draw(c);
            }


        }
    }
}