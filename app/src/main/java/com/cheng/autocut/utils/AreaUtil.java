package com.cheng.autocut.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;

import com.cheng.autocut.entity.ViewPoint;

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
    private static final int RATIO = 20;

    /**
     * 四条边和对角线边长
     */
    private static int length0, length1, length2, length3, length4;

    /**
     * bitmap宽高
     */
    private static int bitmapWidth, bitmapHeight;

    /**
     * view宽高
     */
    private static int viewWidth, viewHeight;

    /**
     * view上对应的point
     */
    private static ViewPoint[] viewPoints;

    public static ViewPoint[] filterPoint(Point[] points, Context context, int viewWidth, int viewHeight, int bitmapWidth, int bitmapHeight) {
        if (points != null && points[0] != null && points.length == 4) {
            AreaUtil.viewWidth = viewWidth;
            AreaUtil.viewHeight = viewHeight;
            AreaUtil.bitmapWidth = bitmapWidth;
            AreaUtil.bitmapHeight = bitmapHeight;
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            int widthPixels = dm.widthPixels;
            int heightPixels = dm.heightPixels;
            minEffectiveSquare = widthPixels * heightPixels / RATIO;
            transferViewPoints(points);
            calcLength(points);
            if (filterDegrees()) {
                return viewPoints;
            }
        }
        resetViewPoint();
        return viewPoints;
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
        int degrees0 = (int) Math.toDegrees(Math.acos((length0 * length0 + length3 * length3 - length4 * length4) / (2.0 * length0 * length3)));
        int degrees1 = (int) Math.toDegrees(Math.acos((length0 * length0 + length4 * length4 - length3 * length3) / (2.0 * length0 * length4)))
                + (int) Math.toDegrees(Math.acos((length1 * length1 + length4 * length4 - length2 * length2) / (2.0 * length1 * length4)));
        int degrees2 = (int) Math.toDegrees(Math.acos((length1 * length1 + length2 * length2 - length4 * length4) / (2.0 * length1 * length2)));
        int degrees3 = (int) Math.toDegrees(Math.acos((length3 * length3 + length4 * length4 - length0 * length0) / (2.0 * length3 * length4)))
                + (int) Math.toDegrees(Math.acos((length2 * length4 + length4 * length4 - length1 * length1) / (2.0 * length2 * length4)));
        return degrees0 > 60 && degrees1 > 60 && degrees2 > 60 && degrees3 > 60;
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
        float transferX0 = bitmap2ViewPointX(points[0]);
        float transferY0 = bitmap2ViewPointY(points[0]);
        float transferX1 = bitmap2ViewPointX(points[1]);
        float transferY1 = bitmap2ViewPointY(points[1]);
        float transferX2 = bitmap2ViewPointX(points[2]);
        float transferY2 = bitmap2ViewPointY(points[2]);
        float transferX3 = bitmap2ViewPointX(points[3]);
        float transferY3 = bitmap2ViewPointY(points[3]);
        ViewPoint viewPoint0 = new ViewPoint(transferX0, transferY0);
        ViewPoint viewPoint1 = new ViewPoint(transferX1, transferY1);
        ViewPoint viewPoint2 = new ViewPoint(transferX2, transferY2);
        ViewPoint viewPoint3 = new ViewPoint(transferX3, transferY3);
        viewPoints = new ViewPoint[]{viewPoint0, viewPoint1, viewPoint2, viewPoint3};
        points[0].x = (int) transferX0;
        points[0].y = (int) transferY0;
        points[1].x = (int) transferX1;
        points[1].y = (int) transferY1;
        points[2].x = (int) transferX2;
        points[2].y = (int) transferY2;
        points[3].x = (int) transferX3;
        points[3].y = (int) transferY3;
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

    private static float bitmap2ViewPointX(Point point) {
        return (float) point.x / bitmapWidth * viewWidth;
    }

    private static float bitmap2ViewPointY(Point point) {
        return (float) point.y / bitmapHeight * viewHeight;
    }

//    /**
//     * 判断两条线是否有交点
//     *
//     * @param points
//     * @return
//     */
//    private static boolean isAcross(Point[] points) {
//        float a1, b1, a2, b2, acrossX, acrossY;
//        if (points[0].x != points[2].x && points[1].x != points[3].x) {
//            a1 = (float) (points[0].y - points[2].y) / (points[0].x - points[2].x);
//            b1 = (float) points[0].y - points[0].x * a1;
//            a2 = (float) (points[1].y - points[3].y) / (points[1].x - points[3].x);
//            b2 = (float) points[1].y - points[1].x * a2;
//            if (a1 != a2) {
//                acrossX = (b2 - b1) / (a1 - a2);
//                acrossY = acrossX * a1 + b1;
//                if (acrossX != points[0].x && acrossX != points[1].x && acrossX != points[2].x && acrossX != points[3].x
//                        && acrossY != points[0].y && acrossY != points[1].y && acrossY != points[2].y && acrossY != points[3].y) {
//                    double[] positionX1 = new double[]{points[0].x, acrossX, points[2].x};
//                    double[] positionX2 = new double[]{points[1].x, acrossX, points[3].x};
//                    double[] positionY1 = new double[]{points[0].y, acrossY, points[2].y};
//                    double[] positionY2 = new double[]{points[1].y, acrossY, points[3].y};
//                    sortPosition(positionX1);
//                    sortPosition(positionX2);
//                    sortPosition(positionY1);
//                    sortPosition(positionY2);
//                    if (acrossX == positionX1[1] && acrossX == positionX2[1] && acrossY == positionY1[1] && acrossY == positionY2[1]) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 排序
//     *
//     * @param positions
//     */
//    private static void sortPosition(double[] positions) {
//        for (int i = 0; i < positions.length - 1; i++) {
//            for (int j = i + 1; j < positions.length; j++) {
//                if (positions[i] > positions[j]) {
//                    double swap = positions[i];
//                    positions[i] = positions[j];
//                    positions[j] = swap;
//                }
//            }
//        }
//    }
}
