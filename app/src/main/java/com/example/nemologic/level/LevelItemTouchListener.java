package com.example.nemologic.level;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;


abstract public class LevelItemTouchListener implements RecyclerView.OnItemTouchListener {
    private static final int MAX_CLICK_DURATION = 200;

    private String tagTouchableView;    // View catches touch event in each item of recycler view.

    protected int pos; // Position of item to start touching.


    public LevelItemTouchListener(String tagTouchableView){
        this.tagTouchableView = tagTouchableView;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

        if(motionEvent.getPointerCount() > 1){

            // Touch with many fingers, don't handle.
            return false;
        }

        int action = motionEvent.getAction();
        if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL){

            onClickUp(pos);

            return false;
        }
        else if(action == MotionEvent.ACTION_DOWN){

            View v = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

            if(v != null) {

                View vTouchable = v.findViewWithTag(tagTouchableView);

                if(vTouchable != null) {

                    RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(v);
                    pos = holder.getAdapterPosition();
                }
            }

            onDownTouchableView(pos);
        }



        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) { }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) { }

    abstract public void onDownTouchableView(int pos);
    public void onClickUp(int pos){}
}