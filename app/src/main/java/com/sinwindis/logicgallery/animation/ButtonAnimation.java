package com.sinwindis.logicgallery.animation;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.sinwindis.logicgallery.R;

public class ButtonAnimation {

    public ButtonAnimation()
    {

    }

    @SuppressLint("ClickableViewAccessibility")
    public static void setOvalButtonAnimationShadow(ImageView iv)
    {

        iv.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int paddingLeft = view.getPaddingLeft();
                int paddingRight = view.getPaddingRight();
                int paddingTop = view.getPaddingTop();
                int paddingBottom = view.getPaddingBottom();

                switch (motionEvent.getActionMasked())
                {
                    case MotionEvent.ACTION_DOWN:
                        view.setBackground(view.getResources().getDrawable(R.drawable.background_btn_oval_press));
                        break;

                    case MotionEvent.ACTION_UP:
                        view.setBackground(view.getResources().getDrawable(R.drawable.background_btn_oval_normal));
                        break;
                }

                view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void setOvalButtonAnimationBlack(ImageView iv)
    {

        @SuppressLint("UseCompatLoadingForDrawables") final Drawable press = iv.getResources().getDrawable(R.drawable.background_btn_oval_shadow);

        iv.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int paddingLeft = view.getPaddingLeft();
                int paddingRight = view.getPaddingRight();
                int paddingTop = view.getPaddingTop();
                int paddingBottom = view.getPaddingBottom();

                switch (motionEvent.getActionMasked())
                {
                    case MotionEvent.ACTION_DOWN:
                        view.setBackground(press);
                        break;

                    case MotionEvent.ACTION_UP:
                        view.setBackground(null);
                        break;
                }

                view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void setRoundButtonAnimationShadow(ImageView iv)
    {
        @SuppressLint("UseCompatLoadingForDrawables") final Drawable press = iv.getResources().getDrawable(R.drawable.background_btn_round_press);
        @SuppressLint("UseCompatLoadingForDrawables") final Drawable up = iv.getResources().getDrawable(R.drawable.background_btn_round_normal);

        iv.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int paddingLeft = view.getPaddingLeft();
                int paddingRight = view.getPaddingRight();
                int paddingTop = view.getPaddingTop();
                int paddingBottom = view.getPaddingBottom();

                switch (motionEvent.getActionMasked())
                {
                    case MotionEvent.ACTION_DOWN:
                        view.setBackground(press);
                        break;

                    case MotionEvent.ACTION_UP:
                        view.setBackground(up);
                        break;
                }

                view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

                return false;
            }
        });
    }

}

