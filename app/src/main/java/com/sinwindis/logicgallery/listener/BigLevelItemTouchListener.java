package com.sinwindis.logicgallery.listener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinwindis.logicgallery.R;


public class BigLevelItemTouchListener implements RecyclerView.OnItemTouchListener {
    Drawable press;
    Drawable up;

    View v;

    @SuppressLint("UseCompatLoadingForDrawables")
    public BigLevelItemTouchListener(Context ctx){

        press = ctx.getResources().getDrawable(R.drawable.background_item_big_level_press);
        up = ctx.getResources().getDrawable(R.drawable.background_item_big_level_normal);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

        if(motionEvent.getPointerCount() > 1){

            return false;
        }

        int action = motionEvent.getAction();

        if(action == MotionEvent.ACTION_DOWN){

            v = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

            if(v != null)
            {
                v.setBackground(press);
            }


        }
        else if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL){

            if(v != null)
            {
                v.setBackground(up);
            }

            return false;
        }



        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) { }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) { }
}