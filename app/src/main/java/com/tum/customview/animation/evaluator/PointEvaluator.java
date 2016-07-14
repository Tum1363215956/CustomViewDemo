package com.tum.customview.animation.evaluator;

import android.animation.TypeEvaluator;
import android.graphics.Point;

/**
 * Created by GKF on 2016/7/12.
 */
public class PointEvaluator implements TypeEvaluator {
    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        Point startPoint = (Point)startValue;
        Point endPoint = (Point)endValue;

        //计算当前的坐标
        int  x = (int)( startPoint.x + fraction * (endPoint.x - startPoint.x));
        int y = (int)( startPoint.y + fraction * (endPoint.y - startPoint.y));

        Point point = new Point(x, y);

        return point;
    }
}
