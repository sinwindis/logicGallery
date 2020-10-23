package com.example.nemologic.biglevel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class BigLevelBorder extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private Drawable normalBorder;
    private int width;

    /**
     * Default divider will be used
     */
    public BigLevelBorder(Context context) {
        final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
        normalBorder = styledAttributes.getDrawable(0);
        styledAttributes.recycle();
    }

    /**
     * Custom divider will be used
     */
    public BigLevelBorder(Context context, int normalBorder, int width) {
        this.normalBorder = ContextCompat.getDrawable(context, normalBorder);
        this.width = width;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {

            View child = parent.getChildAt(i);
            int positionY = i/width;
            int positionX = i%width;

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            if(i < childCount - width)
            {
                int left = child.getLeft();
                int right = child.getRight();
                int top = child.getBottom() - normalBorder.getIntrinsicHeight();
                int bottom = child.getBottom();

                normalBorder.setBounds(left, top, right, bottom);
                normalBorder.draw(c);
            }


            if(positionX < width - 1)
            {
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