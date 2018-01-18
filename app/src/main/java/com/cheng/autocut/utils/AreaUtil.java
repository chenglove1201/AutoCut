package com.cheng.autocut.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;

/**
 * Created by Administrator on 2018/1/12 0012.
 */

public class AreaUtil {
    /**
     * 最小有效面积
     */
    private static int minEffectiveSquare;
    /**
     * 最小面积比
     */
    private static int ratio = 20;

    public static boolean filterPoint(Point[] points, Context context) {
        if (points[0] != null) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            int widthPixels = dm.widthPixels;
            int heightPixels = dm.heightPixels;
            minEffectiveSquare = widthPixels * heightPixels / ratio;
            return calcSquare(points);
        }
        return false;
    }

    /**
     * 计算面积
     */
    private static boolean calcSquare(Point[] points) {
        //四条边和对角线边长
        int length0, length1, length2, length3, length4;
        //四边形面积
        int square;

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
        int perimeterHalf1 = (length0 + length3 + length4) / 2;
        int perimeterHalf2 = (length1 + length2 + length4) / 2;
        int square1 = (int) Math.sqrt(perimeterHalf1 * (perimeterHalf1 - length0) * (perimeterHalf1 - length3) * (perimeterHalf1 - length4));
        int square2 = (int) Math.sqrt(perimeterHalf2 * (perimeterHalf2 - length1) * (perimeterHalf2 - length2) * (perimeterHalf2 - length4));
        square = square1 + square2;
        return square > minEffectiveSquare;
    }

    /**
     * 判断两条线是否有交点
     *
     * @param points
     * @return
     */
    private static boolean isAcross(Point[] points) {
        float a1, b1, a2, b2, acrossX, acrossY;
        if (points[0].x != points[2].x && points[1].x != points[3].x) {
            a1 = (float) (points[0].y - points[2].y) / (points[0].x - points[2].x);
            b1 = (float) points[0].y - points[0].x * a1;
            a2 = (float) (points[1].y - points[3].y) / (points[1].x - points[3].x);
            b2 = (float) points[1].y - points[1].x * a2;
            if (a1 != a2) {
                acrossX = (b2 - b1) / (a1 - a2);
                acrossY = acrossX * a1 + b1;
                if (acrossX != points[0].x && acrossX != points[1].x && acrossX != points[2].x && acrossX != points[3].x
                        && acrossY != points[0].y && acrossY != points[1].y && acrossY != points[2].y && acrossY != points[3].y) {
                    double[] positionX1 = new double[]{points[0].x, acrossX, points[2].x};
                    double[] positionX2 = new double[]{points[1].x, acrossX, points[3].x};
                    double[] positionY1 = new double[]{points[0].y, acrossY, points[2].y};
                    double[] positionY2 = new double[]{points[1].y, acrossY, points[3].y};
                    sortPosition(positionX1);
                    sortPosition(positionX2);
                    sortPosition(positionY1);
                    sortPosition(positionY2);
                    if (acrossX == positionX1[1] && acrossX == positionX2[1] && acrossY == positionY1[1] && acrossY == positionY2[1]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 排序
     *
     * @param positions
     */
    private static void sortPosition(double[] positions) {
        for (int i = 0; i < positions.length - 1; i++) {
            for (int j = i + 1; j < positions.length; j++) {
                if (positions[i] > positions[j]) {
                    double swap = positions[i];
                    positions[i] = positions[j];
                    positions[j] = swap;
                }
            }
        }
    }
}
