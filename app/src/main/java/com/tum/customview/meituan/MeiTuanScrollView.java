package com.tum.customview.meituan;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

/**
 * Created by GKF on 2016/7/28.
 */
public class MeiTuanScrollView extends ScrollView {

    private OnScrollListener onScrollListener;

    public MeiTuanScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MeiTuanScrollView(Context context) {
        this(context,null);
    }

    public MeiTuanScrollView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }


    /**
     * 设置滚动接口
     * @param onScrollListener
     */
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(onScrollListener != null){
            Log.i("TGA","TGA MeiTuanScrollView:"+t);
            onScrollListener.onScroll(t);
        }
    }

    /**
     *
     * 滚动的回调接口
     *
     * @author xiaanming
     *
     */
    public interface OnScrollListener{
        /**
         * 回调方法， 返回MyScrollView滑动的Y方向距离
         * @param scrollY
         * 				、
         */
        public void onScroll(int scrollY);
    }

}
