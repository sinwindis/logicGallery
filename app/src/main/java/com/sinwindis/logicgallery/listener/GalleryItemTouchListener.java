package com.sinwindis.logicgallery.listener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.sinwindis.logicgallery.R;


public class GalleryItemTouchListener implements ImageView.OnTouchListener {
    Drawable press;
    Drawable up;

    @SuppressLint("UseCompatLoadingForDrawables")
    public GalleryItemTouchListener(Context ctx){

        press = ctx.getResources().getDrawable(R.drawable.background_frame_press);
        up = ctx.getResources().getDrawable(R.drawable.background_frame);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            if(view != null)
            {
                view.setBackground(press);
            }


        }
        else if(motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_POINTER_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL){

            if(view != null)
            {
                view.setBackground(up);
            }
        }

        return false;
    }
}