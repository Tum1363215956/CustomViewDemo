package com.tum.customview.draggridview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by GKF on 2016/7/22.
 */
public class DragGridView extends GridView {

    //按下的X坐标
    private int mDownX;
    //按下的Y坐标
    private int mDownY;
    //WindowManager对象
    private WindowManager mWindowManager;
    //WindowManger.LayoutParams的布局参数
    private WindowManager.LayoutParams lp;
    //Context对象
    private Context context;
    //长按的item对应镜像View
    private ImageView mirrorView;

    public DragGridView(Context context) {
        this(context,null);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch(ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownX = (int)ev.getX();
                mDownY = (int)ev.getY();

                setDownClickListener(ev);
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    //需要进行拖拽的item的位置
    private int dragPostion;
    //当前拖拽的item对应的View
    private View dragView;
    //点击坐标点和dragView左顶点的坐标之间的差值
    private int differenceX;//X轴上的
    private int differenceY;//Y轴上的

    //这里用于判断位置是否是点击的位置
    private void setDownClickListener(final MotionEvent ev) {
        dragPostion = pointToPosition(mDownX,mDownY);
        //当需要拖拽的位置部位空时，处理拖拽的结果
        if(dragPostion != AdapterView.INVALID_POSITION){
            setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    dragPostion = position;

                    //获取点击的View
                    dragView = getChildAt(dragPostion - getFirstVisiblePosition());

                    dragView.destroyDrawingCache();
                    dragView.setDrawingCacheEnabled(true);
                    Bitmap dragBitmap = Bitmap.createBitmap(dragView.getDrawingCache());


                    //确定长按之后获取坐标之间的差值
                    differenceX = mDownX - dragView.getLeft();
                    differenceY = mDownY - dragView.getTop();

                    //创建窗口镜像
                    createWindowMirror(dragBitmap,(int)ev.getRawY(),(int)ev.getRawY());


                    return true;
                }
            });
        }
    }

    //创建长按Item的镜像
    private void createWindowMirror(Bitmap dragBitmap, int downX, int downY) {
        mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.CENTER;//居中显示

        lp.x = downX - differenceX;
        lp.y = downY -differenceY;

        this.lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        this.lp.format = PixelFormat.TRANSLUCENT;

        lp.alpha = 0.5f;

        //创建镜像View
        mirrorView = new ImageView(context);
        mirrorView.setImageBitmap(dragBitmap);

        //添加到Window布局中
        mWindowManager.addView(mirrorView,lp);
    }
}
