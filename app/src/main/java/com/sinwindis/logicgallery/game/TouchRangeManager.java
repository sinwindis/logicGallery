package com.sinwindis.logicgallery.game;

import android.graphics.Point;

public class TouchRangeManager {

    private Point startPoint;
    private Point endPoint;

    public TouchRangeManager() {

        startPoint = new Point(0, 0);
        endPoint = new Point(0, 0);
    }

    public void setStartPoint(Point point) {
        this.startPoint = point;
    }

    public void setEndPoint(Point point) {
        this.endPoint = point;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }
}
