package com.tum.customview.percentlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.tum.customview.R;

/**
 * Created by GKF on 2016/7/29.
 */
public class PercentRelativeLayout extends RelativeLayout {
    public PercentRelativeLayout(Context context) {
        this(context,null);
    }

    public PercentRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PercentRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //容器宽高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //默认宽高手机屏幕match_parent
        int widthHint = View.MeasureSpec.getSize(widthMeasureSpec);//父容器宽
        int heightHint = View.MeasureSpec.getSize(heightMeasureSpec);//父容器高

        for(int i=0;i<this.getChildCount();i++){
            //获取子控件
            View child = this.getChildAt(i);
            ViewGroup.LayoutParams params = child.getLayoutParams();
            //默认情况下，子控件采用的是系统layout_width/layout_height
            float widthPercent = 0;
            float heightPercent = 0;
            //获取到自定义宽高
            if(params instanceof PercentRelativeLayout.LayoutParams){
                widthPercent = ((LayoutParams) params).getWidthPercent();
                heightPercent = ((LayoutParams) params).getHeightPercent();
            }

            params.width =(int)( widthPercent * widthHint);
            params.height = (int)(heightPercent * heightHint);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //摆放子控件
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * 当布局内每有一个子控件，就会调用generateLayoutParams这个类
     * @param attrs
     * @return
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(),attrs);
    }

    public static class LayoutParams extends RelativeLayout.LayoutParams{

        private float widthPercent;
        private float heightPercent;

        public float getHeightPercent() {
            return heightPercent;
        }

        public float getWidthPercent() {
            return widthPercent;
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            //解析自定义属性
            TypedArray array = c.obtainStyledAttributes(attrs, R.styleable.percentRelativeLayout);
            widthPercent = array.getFloat(R.styleable.percentRelativeLayout_layout_height_percent,widthPercent);
            heightPercent = array.getFloat(R.styleable.percentRelativeLayout_layout_height_percent,heightPercent);
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }


    }
}
