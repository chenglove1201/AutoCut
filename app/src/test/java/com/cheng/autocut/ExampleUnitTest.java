package com.cheng.autocut;

import com.cheng.autocut.entity.ViewPoint;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
//        assertEquals(4, 2 + 2);
        java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");

        // 初始化数据
        int a = 3;
        int b = 4;
        int c = 5;

        // 计算弧度表示的角
        int B = (int) Math.toDegrees(Math.acos((a * a + c * c - b * b) / (2.0 * a * c)));
        // 格式化数据，保留两位小数
//        String temp = df.format(B);
        System.out.println(B);
    }

    @Test
    public void a() {
        ViewPoint[] viewPoints;
        ViewPoint viewPoint = new ViewPoint(0, 0);
        ViewPoint viewPoint1 = new ViewPoint(0.8f, 0.65f);

        viewPoints = new ViewPoint[]{viewPoint1, viewPoint, viewPoint1, viewPoint};
        System.out.println(viewPoints.length);
        for (ViewPoint vp : viewPoints) {
            System.out.println(vp.x + ".." + vp.y);
        }
    }

    @Test
    public void b() {
//        LinkedList<Integer> l = new LinkedList<>();
//        l.offer(1);
//        l.offer(2);
//        l.offer(3);
//        l.offer(4);
//        l.offer(5);
//        l.removeFirst();
//        l.offer(6);
//        for (int i = 0; i < l.size(); i++) {
//            System.out.println(l.get(i));
//        }

        List<int[]> list = new ArrayList<>();
        list.add(new int[]{1,2,3});
        list.add(new int[]{4,5,6});
        list.add(new int[]{7,8,9});
        list.add(new int[]{10,11,12});
        list.add(new int[]{13,14,15});
//        list.remove(0);
//        list.add(new int[]{1,2,3});
//        list.remove(0);
//        list.add(new int[]{1,2,3});
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i)[0]+".."+list.get(i)[1]+".."+list.get(i)[2]);
        }

    }
}