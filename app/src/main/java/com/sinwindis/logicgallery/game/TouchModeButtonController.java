package com.sinwindis.logicgallery.game;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class TouchModeButtonController {

    private final ImageView ivButtonO;
    private final ImageView ivButtonX;
    private final ImageView ivButtonLock;
    private final ImageView ivButtonHint;

    private TouchMode touchMode;

    public TouchModeButtonController(ImageView ivButtonO, ImageView ivButtonX, ImageView ivButtonLock, ImageView ivButtonHint) {
        this.ivButtonO = ivButtonO;
        this.ivButtonX = ivButtonX;
        this.ivButtonLock = ivButtonLock;
        this.ivButtonHint = ivButtonHint;
    }

    public void setTouchMode(TouchMode touchMode) {
        this.touchMode = touchMode;
    }

    public void setup(Drawable background) {

        ivButtonO.setOnClickListener(view -> {
            if (touchMode.getTouchMode() != TouchMode.TOUCH_CHECK) {

                int paddingStart = view.getPaddingStart();
                int paddingEnd = view.getPaddingEnd();
                int paddingTop = view.getPaddingTop();
                int paddingBottom = view.getPaddingBottom();

                ivButtonO.setBackground(background);
                ivButtonO.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom);
                ivButtonX.setBackground(null);
                ivButtonLock.setBackground(null);
                ivButtonHint.setBackground(null);

                touchMode.setTouchMode(TouchMode.TOUCH_CHECK);
            }
        });

        ivButtonX.setOnClickListener(view -> {
            if (touchMode.getTouchMode() != TouchMode.TOUCH_X) {

                int paddingStart = view.getPaddingStart();
                int paddingEnd = view.getPaddingEnd();
                int paddingTop = view.getPaddingTop();
                int paddingBottom = view.getPaddingBottom();

                ivButtonO.setBackground(null);
                ivButtonX.setBackground(background);
                ivButtonX.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom);
                ivButtonLock.setBackground(null);
                ivButtonHint.setBackground(null);

                touchMode.setTouchMode(TouchMode.TOUCH_X);
            }
        });

        ivButtonLock.setOnClickListener(view -> {
            if (touchMode.getTouchMode() != TouchMode.TOUCH_LOCK) {

                int paddingStart = view.getPaddingStart();
                int paddingEnd = view.getPaddingEnd();
                int paddingTop = view.getPaddingTop();
                int paddingBottom = view.getPaddingBottom();

                ivButtonO.setBackground(null);
                ivButtonX.setBackground(null);
                ivButtonLock.setBackground(background);
                ivButtonLock.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom);
                ivButtonHint.setBackground(null);

                touchMode.setTouchMode(TouchMode.TOUCH_LOCK);
            }
        });

        ivButtonHint.setOnClickListener(view -> {
            if (touchMode.getTouchMode() != TouchMode.TOUCH_HINT) {

                int paddingStart = view.getPaddingStart();
                int paddingEnd = view.getPaddingEnd();
                int paddingTop = view.getPaddingTop();
                int paddingBottom = view.getPaddingBottom();

                ivButtonO.setBackground(null);
                ivButtonX.setBackground(null);
                ivButtonLock.setBackground(null);
                ivButtonHint.setBackground(background);
                ivButtonHint.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom);

                touchMode.setTouchMode(TouchMode.TOUCH_HINT);
            }
        });
    }


}
