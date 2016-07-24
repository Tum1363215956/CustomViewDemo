package com.tum.customview.draggridview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
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

    //是否可拖拽
    private boolean isDrag = false;
    private final int mStatusHeight;
    //按下的X坐标
    private int mDownX;
    //按下的Y坐标
    private int mDownY;
    //移动的坐标
    private int moveX;
    private int moveY;

    //点击需要拖拽的位置
    private int mDragPosition;
    //拖拽的item
    private View mDragItemView;
    //WindowManager对象
    private WindowManager mWindowManager;
    //WindowManger.LayoutParams的布局参数
    private WindowManager.LayoutParams mWindowLayoutParams;
    //Context对象
    private Context context;
    //长按的item对应镜像View
    private ImageView mirrorView;

    private int mPoint2ItemTop;
    private int mPoint2ItemLeft;
    private int mOffset2Top;
    private int mOffset2Left;
    private Bitmap mDragBitmap;
    //切换位置的接口
    //    private OnChangeListener onChangeListener;

    public DragGridView(Context context) {
        this(context, null);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);

        //获取状态栏的高度
        mStatusHeight = getStatusHeight(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mDownX = (int) ev.getX();
            mDownY = (int) ev.getY();

            setOnLongClickResponse(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(isDrag && mirrorView != null){
            switch (ev.getAction()){
                case MotionEvent.ACTION_MOVE:
                    int moveX = (int)getX();
                    int moveY = (int)getY();
                    Log.i("TGA","TGA moveX:"+moveX+",moveY"+moveY);
                    //拖拽item
                    onDragItem(moveX,moveY);
                    break;
                case MotionEvent.ACTION_UP:
                    onStopDrag();
                    isDrag = false;
//                    requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }*/

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(isDrag && mirrorView != null){
            switch(ev.getAction()){
                case MotionEvent.ACTION_MOVE:
                    moveX = (int) ev.getX();
                    moveY = (int) ev.getY();
                    Log.i("TGA","TGA moveX:"+moveX+",moveY"+moveY);
                    //拖动item
                    onDragItem(moveX, moveY);
                    break;
                case MotionEvent.ACTION_UP:
                    onStopDrag();
                    isDrag = false;
//                    mHandler.removeCallbacks(mScrollRunnable);
                    requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 实现WindowManagerView的移动
     * @param moveX
     * @param moveY
     */
    private void onDragItem(int moveX, int moveY) {
        mWindowLayoutParams.x = moveX - mPoint2ItemLeft + mOffset2Left;
        mWindowLayoutParams.y = moveY - mPoint2ItemTop + mOffset2Top - mStatusHeight;


        Log.i("TGA","TGA window x"+mWindowLayoutParams.x+",mWindowLayoutParams.y"+mWindowLayoutParams.y);
        mWindowManager.updateViewLayout(mirrorView,mWindowLayoutParams);//更新镜像的位置

    }

    /**
     * 停止拖拽我们将之前隐藏的item显示出来，并将镜像移除
     */
    private void onStopDrag() {
        View view = getChildAt(mDragPosition - getFirstVisiblePosition());
        if(view != null){
            view.setVisibility(View.VISIBLE);
        }
        removeDragImage();
    }

    /**
     * 从界面上移除拖拽的镜像
     */
    private void removeDragImage() {
        if(mirrorView != null){
            mWindowManager.removeView(mirrorView);
            mirrorView = null;
        }
    }

    /**
     * 长按监听
     * @param ev
     */
    private void setOnLongClickResponse(final MotionEvent ev) {
        final int dPosition = pointToPosition(mDownX, mDownY);


        setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (dPosition != AdapterView.INVALID_POSITION) {
                    isDrag = true;//是否可拖拽

                    mDragPosition = dPosition;
                    mDragItemView = getChildAt(mDragPosition - getFirstVisiblePosition());//获取需要拖拽的View


                    //6个距离
                    mPoint2ItemTop = mDownY - mDragItemView.getTop();
                    mPoint2ItemLeft = mDownX - mDragItemView.getLeft();

                    mOffset2Top = (int) (ev.getRawY() - mDownY);
                    mOffset2Left = (int) (ev.getRawX() - mDownX);
                    //开启mDragItemView绘图缓存
                    mDragItemView.setDrawingCacheEnabled(true);
                    //获取mDragItemView在缓存中的Bitmap对象
                    mDragBitmap = Bitmap.createBitmap(mDragItemView.getDrawingCache());
                    //这一步很关键，释放绘图缓存，避免出现重复的镜像
                    mDragItemView.destroyDrawingCache();

                    createWindowView(mDragBitmap, mDownX, mDownY);

                    mDragItemView.setVisibility(View.INVISIBLE);//设置为不可见

                    requestDisallowInterceptTouchEvent(true);

                    return true;
                }

                return false;
            }
        });

    }

    private void createWindowView(Bitmap mDragBitmap, int mDownX, int mDownY) {
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowLayoutParams.x = mDownX - mPoint2ItemLeft + mOffset2Left;
        mWindowLayoutParams.y = mDownY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
        mWindowLayoutParams.alpha = 0.55f; //透明度
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        mirrorView = new ImageView(getContext());
        mirrorView.setImageBitmap(mDragBitmap);
        mWindowManager.addView(mirrorView, mWindowLayoutParams);
    }

    /**
     * 获取状态栏的高度
     *
     * @param context
     * @return
     */
    private static int getStatusHeight(Context context) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }
}
