package com.example.nemologic.animation;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.nemologic.R;

public class ButtonAnimation {

    public ButtonAnimation()
    {

    }

    @SuppressLint("ClickableViewAccessibility")
    public static void setButtonAnimationShadow(ImageView iv)
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
                        view.setBackground(view.getResources().getDrawable(R.drawable.background_btn_press));
                        break;

                    case MotionEvent.ACTION_UP:
                        view.setBackground(view.getResources().getDrawable(R.drawable.background_btn_normal));
                        break;
                }

                view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void setButtonAnimationNormal(ImageView iv)
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
                        view.setBackground(view.getResources().getDrawable(R.drawable.background_btn_shadow));
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

}

