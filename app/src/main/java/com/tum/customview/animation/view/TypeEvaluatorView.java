package com.tum.customview.animation.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.tum.customview.animation.evaluator.ColorEvaluator;
import com.tum.customview.animation.evaluator.PointEvaluator;

/**
 * Created by GKF on 2016/7/12.
 */
public class TypeEvaluatorView extends View {

    private Paint mPaint;//设置画笔

    private final static int RADIUS = 50;//半径

    private Point curPoint;//当前位置

    public TypeEvaluatorView(Context context) {
        this(context,null);
    }

    public TypeEvaluatorView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TypeEvaluatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    //初始化画笔
    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
    }
    /***********************************************绘图********************************************/
    @Override
    protected void onDraw(Canvas canvas) {
        if(curPoint == null){
            curPoint = new Point(RADIUS,RADIUS);
            drawCircle(canvas);
            startAnimation();
        }else{
            drawCircle(canvas);
        }
    }

    private void drawCircle(Canvas canvas) {
        canvas.drawCircle((float)curPoint.x,(float)curPoint.y,RADIUS,mPaint);
    }

    /**********************************************动画*********************************************/
    private void startAnimation() {
        //初始点
        Point startPoint = new Point(RADIUS,RADIUS);
        Point endPoint = new Point(getWidth()-RADIUS,getHeight()-RADIUS);
        ValueAnimator animator = ValueAnimator.ofObject(new PointEvaluator(),startPoint,endPoint);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                curPoint = (Point) animation.getAnimatedValue();
                invalidate();
            }
        });
//        animator.setDuration(5000);
//        animator.start();

        //颜色变化
        ObjectAnimator anim = ObjectAnimator.ofObject(this,"color",new ColorEvaluator(),"#0000FF", "#FF0000");

        //动画集
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator,anim);
        set.setDuration(5000);
        set.start();
    }

    /*********************************************颜色**********************************************/
    private String color;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        mPaint.setColor(Color.parseColor(color));
        invalidate();
    }


}
