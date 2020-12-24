package com.sinwindis.logicgallery.game;

import android.graphics.Point;

public class DragManager {

    private TouchRangeManager touchRangeManager;
    private boolean oneLineDrag = true;

    public DragManager() {

    }

    public void setTouchRangeManager(TouchRangeManager touchRangeManager) {
        this.touchRangeManager = touchRangeManager;
    }

    public Point getStartPoint() {
        Point touchStartPoint = touchRangeManager.getStartPoint();
        Point touchEndPoint = touchRangeManager.getEndPoint();

        int x;
        int y;

        if (oneLineDrag) {
            int width = Math.abs(touchEndPoint.x - touchStartPoint.x);
            int height = Math.abs(touchEndPoint.y - touchStartPoint.y);
            if (width > height) {
                x = Math.min(touchStartPoint.x, touchEndPoint.x);
                y = touchStartPoint.y;
            } else {
                x = touchStartPoint.x;
                y = Math.min(touchStartPoint.y, touchEndPoint.y);
            }
        } else {
            x = Math.min(touchStartPoint.x, touchEndPoint.x);
            y = Math.min(touchStartPoint.y, touchEndPoint.y);
        }

        return new Point(x, y);
    }

    public void setOneLineDrag(boolean oneLineDrag) {
        this.oneLineDrag = oneLineDrag;
    }

    public Point getEndPoint() {
        Point touchStartPoint = touchRangeManager.getStartPoint();
        Point touchEndPoint = touchRangeManager.getEndPoint();

        int x;
        int y;

        if (oneLineDrag) {
            int width = Math.abs(touchEndPoint.x - touchStartPoint.x);
            int height = Math.abs(touchEndPoint.y - touchStartPoint.y);
            if (width > height) {
                x = Math.max(touchStartPoint.x, touchEndPoint.x);
                y = touchStartPoint.y;
            } else {
                x = touchStartPoint.x;
                y = Math.max(touchStartPoint.y, touchEndPoint.y);
            }
        } else {
            x = Math.max(touchStartPoint.x, touchEndPoint.x);
            y = Math.max(touchStartPoint.y, touchEndPoint.y);
        }


        return new Point(x, y);
    }

    public int getDragCount() {
        Point endPoint = getEndPoint();
        Point startPoint = getStartPoint();
        return (endPoint.x - startPoint.x + 1) * (endPoint.y - startPoint.y + 1);
    }
}
