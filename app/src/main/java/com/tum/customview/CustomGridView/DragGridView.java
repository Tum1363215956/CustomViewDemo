package com.tum.customview.CustomGridView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/7/16.
 */
public class DragGridView extends GridView {
    //DragGridView的item长按响应的时间，默认毫秒是1000，也可以自行设置
    private long dragResponseMS = 1000;

    //是否可以拖拽，默认不可以
    private boolean isDrag = false;

    private int mDownX;
    private int mDownY;
    private int moveX;
    private int moveY;

    //正在拖拽的position
    private int mDragPosition;

    //刚开始拖拽的item对应的View
    private View mStartDragItemView = null;

    //用于拖拽的镜像，这里直接用一个ImageView
    private ImageView mDragImageView;

    //振动器
    private Vibrator mVibrator;
    private WindowManager mWindowManager;

    //item镜像的布局参数
    private WindowManager.LayoutParams mWindowLayoutParams;

    //我们拖拽的item对应的Bitmap
    private Bitmap mDragBitmap;

    //按下的点到所在item的上边缘的距离
    private int mPoint2ItemTop;

    //按下的点到所在item的左边缘的距离
    private int mPoint2ItemLeft;

    //DragGridView距离屏幕顶部的偏移量
    private int mOffset2Top;

    //DragGridView距离屏幕左边的的偏移量
    private int mOffset2Left;

    //状态兰的高度
    private int mStatusHeight;

    //DragGridView自动向下滚动的边界值
    private int mDownScrollBorder;

    //DragGridView自动向下滚动的边界值
    private int mUpScrollBorder;

    //DragGridView自动滚动的速度
    private static final int speed = 20;

    //item发生变化时=回调的接口
    private OnChangeListener onChangeListener;

    public DragGridView(Context context) {
        this(context, null);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mVibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);

        //获取状态栏的高度
        mStatusHeight = getStatusHeight(context);
    }

    /**
     * 设置回调接口
     * @param onChangeListener
     */
    public void setOnChangeListener(OnChangeListener onChangeListener){
        this.onChangeListener = onChangeListener;
    }

    /**
     * 设置响应拖拽的毫秒数，默认是1000毫秒
     * @param dragResponseMS
     */
    public void setDragResponseMS(long dragResponseMS){
        this.dragResponseMS = dragResponseMS;
    }

    private Handler mHandler = new Handler();

    //用来处理是否为长按的Runnable
    private Runnable mLongClickRunnable = new Runnable() {
        @Override
        public void run() {
            isDrag = true;//设置可以拖拽
            mVibrator.vibrate(50);//震动一下
            mStartDragItemView.setVisibility(View.INVISIBLE);

            //根据我们按下的点显示item的镜像
            createDragImage(mDragBitmap,mDownX,mDownY);
        }
    };

    /**
     * 创建拖动的镜像
     * @param bitmap
     * @param downX
     * 			按下的点相对父控件的X坐标
     * @param downY
     * 			按下的点相对父控件的X坐标
     */
    private void createDragImage(Bitmap bitmap, int downX, int downY) {
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowLayoutParams.x = downX - mPoint2ItemLeft + mOffset2Left;
        mWindowLayoutParams.y = downY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
        mWindowLayoutParams.alpha = 0.55f; //透明度
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE ;

        mDragImageView = new ImageView(getContext());
        mDragImageView.setImageBitmap(bitmap);
        mWindowManager.addView(mDragImageView, mWindowLayoutParams);
    }

    //事件分发
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch(ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownX = (int)ev.getX();
                mDownY = (int)ev.getY();

                //根据按下的X、Y坐标获取所点击item的position
                mDragPosition = pointToPosition(mDownX,mDownY);//AbsListView里面确定点的位置

                //根据位置来判断是否分发点击事件
                if(mDragPosition == AdapterView.INVALID_POSITION){
                    return super.dispatchTouchEvent(ev);
                }

                //使用Handler延时dragResponseMS执行mLongClickRunnable
                mHandler.postDelayed(mLongClickRunnable,dragResponseMS);

                //根据position获取该item所对应的的View
                mStartDragItemView = getChildAt(mDragPosition - getFirstVisiblePosition());

                //6个距离
                mPoint2ItemTop = mDownY - mStartDragItemView.getTop();
                mPoint2ItemLeft = mDownX - mStartDragItemView.getLeft();

                mOffset2Top = (int)(ev.getRawY() - mDownY);
                mOffset2Left = (int)(ev.getRawX() - mDownX);


                //获取DragGridView自动向上滚动的偏移量，小于这个值，DragGridView向下滚动
                mDownScrollBorder = getHeight() / 4;
                //获取DragGridView自动向下滚动的偏移量，大于这个值，DragGridView向上滚动
                mUpScrollBorder = getHeight() * 3/4;


                //开启mDragItemView绘图缓存
                mStartDragItemView.setDrawingCacheEnabled(true);
                //获取mDragItemView在缓存中的Bitmap对象
                mDragBitmap = Bitmap.createBitmap(mStartDragItemView.getDrawingCache());
                //这一步很关键，释放绘图缓存，避免出现重复的镜像
                mStartDragItemView.destroyDrawingCache();

                break;
            case MotionEvent.ACTION_MOVE:

                int moveX = (int)ev.getX();
                int moveY = (int)ev.getY();

                //如果我们在按下的item上面移动，只要不超过item的边界我们就不移除mRunnable
                if(!isTouchInItem(mStartDragItemView,moveX,moveY)){
                    mHandler.removeCallbacks(mLongClickRunnable);
                }

                break;
            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacks(mLongClickRunnable);
                mHandler.removeCallbacks(mScrollRunnable);
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(isDrag && mDragImageView != null){
            switch (ev.getAction()){
                case MotionEvent.ACTION_MOVE:
                    moveX = (int)getX();
                    moveY = (int)getY();

                    //拖拽item
                    onDragItem(moveX,moveY);
                    break;
                case MotionEvent.ACTION_UP:
                    onStopDrag();
                    isDrag = false;
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }


    /**
     * 拖动item，在里面实现了item镜像的位置更新，item的相互交换以及GridView的自行滚动
     * @param moveX
     * @param moveY
     */
    private void onDragItem(int moveX, int moveY) {
        mWindowLayoutParams.x = moveX - mPoint2ItemLeft + mOffset2Left;
        mWindowLayoutParams.y = moveY - mPoint2ItemTop + mOffset2Top;

        mWindowManager.updateViewLayout(mDragImageView,mWindowLayoutParams);//更新镜像的位置

        onSwapItem(moveX,moveY);

        mHandler.post(mScrollRunnable);
    }

    private Runnable mScrollRunnable = new Runnable() {
        @Override
        public void run() {
            int scrollY;
            if(moveY > mUpScrollBorder){
                scrollY = -speed;
                mHandler.postDelayed(mScrollRunnable,25);
            }else if(moveY < mDownScrollBorder){
                scrollY = speed;
                mHandler.postDelayed(mScrollRunnable,25);
            }else{
                scrollY = 0;
                mHandler.removeCallbacks(mScrollRunnable);
            }

            //当我们的手指到达GridView向上或者向下滚动的偏移量的时候，可能我们手指没有移动，但是DragGridView在自动的滚动
            //所以我们在这里调用下onSwapItem()方法来交换item
            onSwapItem(moveX, moveY);
            View view = getChildAt(mDragPosition - getFirstVisiblePosition());
            //实现GridView的自动滚动
            smoothScrollToPositionFromTop(mDragPosition, view.getTop() + scrollY);
        }
    };

    /**
     * 停止拖拽我们将之前隐藏的item显示出来，并将镜像移除
     */
    private void onStopDrag() {
        View view = getChildAt(mDragPosition - getFirstVisiblePosition());
        if(view != null){
            view.setVisibility(View.VISIBLE);
        }
//        ((DragAdapter)this.getAdapter()).setItemHide(-1);
        removeDragImage();
    }

    /**
     * 从界面上移除拖拽的镜像
     */
    private void removeDragImage() {
        if(mDragImageView != null){
            mWindowManager.removeView(mDragImageView);
            mDragImageView = null;
        }
    }

    /**
     * 交换item，并且控制item之间的显示与隐藏效果
     * @param moveX
     * @param moveY
     */
    private void onSwapItem(int moveX, int moveY) {
        //获取我们手指移动到的那个item的postion
        int tempPosition = pointToPosition(moveX,moveY);

        //假如tempPosition改变了并且tempPosition不等于-1，则进行交换
        if(tempPosition != mDragPosition && tempPosition != AdapterView.INVALID_POSITION){
            if(onChangeListener != null){
                onChangeListener.onChange(mDragPosition,tempPosition);
            }
            getChildAt(tempPosition - getFirstVisiblePosition()).setVisibility(View.INVISIBLE);//拖拽到新的item，新的item隐藏掉
            getChildAt(mDragPosition - getFirstVisiblePosition()).setVisibility(View.VISIBLE);//之前的item显示出来

            mDragPosition = tempPosition;
        }
    }

    /**
     * 是否点击在GridView的Item上面
     * @param dragView
     * @param x
     * @param y
     * @return
     */
    private boolean isTouchInItem(View dragView, int x, int y) {
        if(dragView == null){
            return false;
        }

        int leftOffset = dragView.getLeft();
        int topOffset = dragView.getTop();

        if(x < leftOffset || x > leftOffset + dragView.getWidth()){
            return false;
        }

        if(y < topOffset || y > topOffset + dragView.getHeight()){
            return false;
        }

        return true;
    }

    /**
     * 获取状态栏的高度
     * @param context
     * @return
     */
    private static int getStatusHeight(Context context){
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight){
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

    public interface OnChangeListener{

        /**
         * 当item交换位置的时候回调的方法，我们只需要在该方法中实现数据的交换即可
         * @param form
         * @param to
         */
        public void onChange(int form,int to);
    }
}