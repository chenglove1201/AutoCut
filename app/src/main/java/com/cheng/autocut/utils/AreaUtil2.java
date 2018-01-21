package com.cheng.autocut.utils;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;

import com.cheng.autocut.Camera2Activity;
import com.cheng.autocut.entity.ViewPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/12 0012.
 */

public class AreaUtil2 {
    private static Context context;

    /**
     * 最小有效面积
     */
    private static int minEffectiveSquare;

    /**
     * 最小面积比
     */
    private static final int RATIO = 20;

    /**
     * 四条边和对角线边长
     */
    private static int length0, length1, length2, length3, length4;

    /**
     * view上对应的point
     */
    private static ViewPoint[] viewPoints;

    /**
     * 存储过滤值
     */
    private static List<Point[]> values = new ArrayList<>();

    /**
     * 屏幕宽高
     */
    private static int widthPixels, heightPixels;

    public static ViewPoint[] filterPoint(Point[] points, Context context) {
        if (points != null && points[0] != null && points.length == 4) {
            AreaUtil2.context = context;
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            widthPixels = dm.widthPixels;
            heightPixels = dm.heightPixels;
            minEffectiveSquare = widthPixels * heightPixels / RATIO;
            calcLength(points);
            if (filterDegrees()) {
                transferViewPoints(points);
                addValues(points);
                return viewPoints;
            }
        }
        resetViewPoint();
        return viewPoints;
    }

    /**
     * 添加结果队列
     */
    private static void addValues(Point[] points) {
        values.add(points);
        if (values.size() == 2) {
            isShutter(values);
            values.remove(0);
        }
    }

    /**
     * 判断是否按下快门拍照
     *
     * @param values
     */
    private static void isShutter(List<Point[]> values) {
        Point[] points0 = values.get(0);
        Point[] points1 = values.get(1);
        int absX0 = Math.abs(points0[0].x - points1[0].x);
        int absY0 = Math.abs(points0[0].y - points1[0].y);
        int distance0 = (int) Math.sqrt(absX0 * absX0 + absY0 * absY0);
        int absX1 = Math.abs(points0[1].x - points1[1].x);
        int absY1 = Math.abs(points0[1].y - points1[1].y);
        int distance1 = (int) Math.sqrt(absX1 * absX1 + absY1 * absY1);
        int absX2 = Math.abs(points0[2].x - points1[2].x);
        int absY2 = Math.abs(points0[2].y - points1[2].y);
        int distance2 = (int) Math.sqrt(absX2 * absX2 + absY2 * absY2);
        int absX3 = Math.abs(points0[3].x - points1[3].x);
        int absY3 = Math.abs(points0[3].y - points1[3].y);
        int distance3 = (int) Math.sqrt(absX3 * absX3 + absY3 * absY3);
        if (distance0 <= 30 && distance1 <= 30 && distance2 <= 30 && distance3 <= 30) {
            getRect(values.get(1));
        }
    }

    /**
     * 获取聚焦矩形
     */
    private static void getRect(Point[] points) {
        int left = Math.min(points[0].x, points[3].x);
        int top = Math.min(points[0].y, points[1].y);
        int right = Math.max(points[1].x, points[2].x);
        int bottom = Math.max(points[2].y, points[3].y);

        left = (int) ((float) left / widthPixels * 2000 - 1000);
        top = (int) ((float) top / heightPixels * 2000 - 1000);
        right = (int) ((float) right / widthPixels * 2000 - 1000);
        bottom = (int) ((float) bottom / heightPixels * 2000 - 1000);

        Rect rect = new Rect(left, top, right, bottom);
        ((Camera2Activity) context).shutter(rect);
    }

    /**
     * 过滤面积
     */
    private static boolean filterSquare() {
        int perimeterHalf1 = (length0 + length3 + length4) / 2;
        int perimeterHalf2 = (length1 + length2 + length4) / 2;
        int square1 = (int) Math.sqrt(perimeterHalf1 * (perimeterHalf1 - length0) * (perimeterHalf1 - length3) * (perimeterHalf1 - length4));
        int square2 = (int) Math.sqrt(perimeterHalf2 * (perimeterHalf2 - length1) * (perimeterHalf2 - length2) * (perimeterHalf2 - length4));
        return square1 + square2 > minEffectiveSquare;
    }

    /**
     * 过滤角度
     */
    private static boolean filterDegrees() {
        double degrees0 = Math.toDegrees(Math.acos((length0 * length0 + length3 * length3 - length4 * length4) / (2.0 * length0 * length3)));
        double degrees1 = Math.toDegrees(Math.acos((length0 * length0 + length4 * length4 - length3 * length3) / (2.0 * length0 * length4)))
                + Math.toDegrees(Math.acos((length1 * length1 + length4 * length4 - length2 * length2) / (2.0 * length1 * length4)));
        double degrees2 = Math.toDegrees(Math.acos((length1 * length1 + length2 * length2 - length4 * length4) / (2.0 * length1 * length2)));
        double degrees3 = Math.toDegrees(Math.acos((length3 * length3 + length4 * length4 - length0 * length0) / (2.0 * length3 * length4)))
                + Math.toDegrees(Math.acos((length2 * length4 + length4 * length4 - length1 * length1) / (2.0 * length2 * length4)));
        return degrees0 > 70.0d && degrees1 > 70.0d && degrees2 > 70.0d && degrees3 > 70.0d;
    }

    /**
     * 计算边长
     */
    private static void calcLength(Point[] points) {
        int absX0 = Math.abs(points[0].x - points[1].x);
        int absY0 = Math.abs(points[0].y - points[1].y);
        length0 = (int) Math.sqrt(absX0 * absX0 + absY0 * absY0);
        int absX1 = Math.abs(points[1].x - points[2].x);
        int absY1 = Math.abs(points[1].y - points[2].y);
        length1 = (int) Math.sqrt(absX1 * absX1 + absY1 * absY1);
        int absX2 = Math.abs(points[2].x - points[3].x);
        int absY2 = Math.abs(points[2].y - points[3].y);
        length2 = (int) Math.sqrt(absX2 * absX2 + absY2 * absY2);
        int absX3 = Math.abs(points[3].x - points[0].x);
        int absY3 = Math.abs(points[3].y - points[0].y);
        length3 = (int) Math.sqrt(absX3 * absX3 + absY3 * absY3);
        int absX4 = Math.abs(points[1].x - points[3].x);
        int absY4 = Math.abs(points[1].y - points[3].y);
        length4 = (int) Math.sqrt(absX4 * absX4 + absY4 * absY4);
    }

    /**
     * 将bitmap中的point转为view中对应的point
     */
    private static void transferViewPoints(Point[] points) {
        ViewPoint viewPoint0 = new ViewPoint(points[0].x, points[0].y);
        ViewPoint viewPoint1 = new ViewPoint(points[1].x, points[1].y);
        ViewPoint viewPoint2 = new ViewPoint(points[2].x, points[2].y);
        ViewPoint viewPoint3 = new ViewPoint(points[3].x, points[3].y);
        viewPoints = new ViewPoint[]{viewPoint0, viewPoint1, viewPoint2, viewPoint3};
    }

    /**
     * 重置ViewPoint
     */
    private static void resetViewPoint() {
        if (viewPoints == null) {
            ViewPoint viewPoint = new ViewPoint(0, 0);
            viewPoints = new ViewPoint[]{viewPoint, viewPoint, viewPoint, viewPoint};
        } else {
            viewPoints[0].x = 0;
            viewPoints[0].y = 0;
            viewPoints[1].x = 0;
            viewPoints[1].y = 0;
            viewPoints[2].x = 0;
            viewPoints[2].y = 0;
            viewPoints[3].x = 0;
            viewPoints[3].y = 0;
        }
    }
}
