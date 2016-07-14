package com.tum.customview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

/**
 * Created by GKF on 2016/7/11.
 */
public class RandomView extends View {

    private int width,height;//空间在屏幕中宽高

    //图片资源
    Bitmap oneBitmap;//第一张图片
    Bitmap twoBitmap;//第二章图片

    //图片位置
    private Point p1,p2;

    //点击事件——判断点击的图片
    private boolean isOneTouch,isTwoTouch;
    private final static int ONEBITMAP = 0x001;
    private final static int TWOBITMAP = 0x002;

    //两张图片中点坐标
    private int location1,location2;

    //画笔
    private Paint mPaint = new Paint();

    public RandomView(Context context) {
        this(context,null);
    }

    public RandomView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RandomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        setXY();
        p1 = new Point();
        p2 = new Point();

    }

    /************************************获取空间在屏幕中的宽高*************************************/
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;

        initXY();
    }

    /************************************************获取图片***************************************/
    public void setOneBitmap(int resId){
        oneBitmap = BitmapFactory.decodeResource(getResources(),resId);
    }

    public void setTwoBitmap(int resId){
        twoBitmap = BitmapFactory.decodeResource(getResources(),resId);
    }

    /************************************自定义Point类**********************************************/
    class Point{
        public int x;
        public int y;

        public void setX(int x) {
            this.x = x;
        }

        public int getX() {
            return x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getY() {
            return y;
        }
    }

    /****************************************绘图***************************************************/
    @Override
    protected void onDraw(Canvas canvas) {
        if (oneBitmap != null) {
            Log.d("TGA", " TGA onDraw: one");
            if (p1.x + oneBitmap.getWidth() > width) {
                p1.x = width - oneBitmap.getWidth();
            }
            if (p1.y + oneBitmap.getHeight() > height) {
                p1.y = height - oneBitmap.getHeight();
            }

//            scaleBitmap(oneBitmap,p1,canvas);
//            canvas.drawBitmap(resizeImage(oneBitmap,0.5f,0.5f),p1.x,p1.y,mPaint);
        }else{
            return;
        }
        //画第一张图片
        if(!isOneTouch) {
            canvas.drawBitmap(oneBitmap, p1.x, p1.y, mPaint);
        }else{
            canvas.drawBitmap(resizeImage(oneBitmap,0.5f,0.5f),p1.x,p1.y,mPaint);
        }

        //画第二张图片
        if(twoBitmap != null){
            if(p2.x + twoBitmap.getWidth() > width){
                p2.x = width - twoBitmap.getWidth();
            }
            if(p2.y + twoBitmap.getHeight() > height){
                p2.y = height - twoBitmap.getHeight();
            }

        }else{
            return;
        }

        if(!isTwoTouch){
            canvas.drawBitmap(twoBitmap,p2.x,p2.y,mPaint);
        }else{
            canvas.drawBitmap(resizeImage(twoBitmap,0.5f,0.5f),p2.x,p2.y,mPaint);
        }
    }

    /**************************************设置图片的坐标*******************************************/
    public void setXY(){

        initXY();

        Log.d("TGA", "TGA setXY: " + p1.x+"," + p1.y+";p2.x"+p2.x+",p2.y:"+p2.y);
        invalidate();
    }

    private void initXY() {
        Random random = new Random();
        //第一张图片
        p1.x = random.nextInt(width);
        p1.y= random.nextInt(height);
        location1 = width/4 - oneBitmap.getWidth()/2;

        //第二章图片
        p2.x = random.nextInt(width);
        p2.y = random.nextInt(height);
        location2 = width * 3 / 4 - twoBitmap.getWidth() / 2;

        //启动动画
        bitmapAnimation(p1,oneBitmap,location1);
        bitmapAnimation(p2,twoBitmap,location2);
    }

    /***********************************动画效果****************************************************/
    public void bitmapAnimation(final Point p,Bitmap bitmap,int location){
        Log.d("TGA", " TGA bitmapAnimation: 动画启动");
        ObjectAnimator animatorX = ObjectAnimator.ofInt(p,"x",p.x,location );
        ObjectAnimator animatorY = ObjectAnimator.ofInt(p,"y",p.y,height/2-bitmap.getHeight()/2);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animatorX,animatorY);
        set.setDuration(1000);
        set.start();
        set.setStartDelay(1000);

        animatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.d("TGA","TGA Point"+p.x+"\ty:"+p.y);
                invalidate();
            }
        });
    }

    /**********************************点击事件*****************************************************/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.i("TGA","TGA ACTION_DOWN");
                //判断点击的图片
                int picnum = justBitmap(x,y);
                if(picnum == ONEBITMAP){
                    isOneTouch = true;
                    p1.x = location1 + resizeImage(oneBitmap,0.5f,0.5f).getWidth() / 2;
                    p1.y = height / 2 - resizeImage(oneBitmap,0.5f,0.5f).getHeight() / 2;
                }else if(picnum == TWOBITMAP){
                    isTwoTouch = true;
                    p2.x = location2 + twoBitmap.getWidth() / 4;
                    p2.y = height / 2 - twoBitmap.getHeight() / 4;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("TGA","TGA ACTION_MOVE"+x+","+y);
                if(isOneTouch){
                    p1.x = x - oneBitmap.getWidth() / 4;
                    p1.y = y - oneBitmap.getHeight() / 4;
                }else if(isTwoTouch){
                    p2.x = x - twoBitmap.getWidth() / 4;
                    p2.y = y - twoBitmap.getHeight() / 4;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                Log.i("TGA","TGA ACTION_UP");
                if(isOneTouch){
                    p1.x = x - oneBitmap.getWidth() / 2;
                    p1.y = y - oneBitmap.getHeight() / 2;
                    bitmapAnimation(p1,oneBitmap,location1);
                }else if(isTwoTouch){
                    p2.x = x - twoBitmap.getWidth() / 2;
                    p2.y = y - twoBitmap.getHeight() / 2;
                    bitmapAnimation(p2,twoBitmap,location2);
                }
                invalidate();
                isTwoTouch = false;
                isOneTouch = false;
                break;
        }

        return true;
    }

    //判断哪个图片
    private int justBitmap(int x, int y) {
        int oneX = Math.abs(x - p1.x - oneBitmap.getWidth() / 2);
        int oneY = Math.abs(oneBitmap.getHeight() / 2 + p1.y - y);

        int twoX = Math.abs(twoBitmap.getWidth() / 2 + p2.x - x);
        int twoY = Math.abs(twoBitmap.getHeight() / 2 + p2.y - y);

        if(oneX < oneBitmap.getWidth() / 2 && oneY < oneBitmap.getHeight() / 2){
            return ONEBITMAP;
        }else if(twoX < twoBitmap.getWidth() / 2 && twoY < twoBitmap.getHeight() / 2){
            return TWOBITMAP;
        }
        return 0;
    }

    /******************************************缩放的实现*******************************************/
    private void scaleBitmap(Bitmap bitmap,Point point,Canvas canvas){
        //计算缩放的点
        int x = point.x + bitmap.getWidth()/2;
        int y = point.y+bitmap.getHeight()/2;

        Matrix matrix = new Matrix();   // 创建操作图片用的 Matrix 对象
        matrix.setScale(0.5f,0.5f,x,y);
        canvas.drawBitmap(bitmap,matrix,mPaint);
    }

    public Bitmap resizeImage(Bitmap bitmap,float w,float h){
        Matrix matric = new Matrix();
        matric.postScale(w,h);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matric,true);
        return resizedBitmap;
    }

}