package com.cheng.autocut.entity;

/**
 * Created by Administrator on 2018/1/20.
 */

public class ViewPoint {
    public float x;
    public float y;

    public ViewPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "ViewPoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
