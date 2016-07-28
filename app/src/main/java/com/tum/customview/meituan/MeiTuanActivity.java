package com.tum.customview.meituan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.tum.customview.R;

/**
 * 实现美团购买商品布局
 */
public class MeiTuanActivity extends AppCompatActivity implements MeiTuanScrollView.OnScrollListener{

    /**
     * 自定义的ScrollView
     */
    private MeiTuanScrollView myScrollView;
    /**
     * 在MyScrollView里面的购买布局
     */
    private LinearLayout mBuyLayout;
    /**
     * 位于顶部的购买布局
     */
    private LinearLayout mTopBuyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mei_tuan);

        myScrollView = (MeiTuanScrollView) findViewById(R.id.scrollView);
        mBuyLayout = (LinearLayout) findViewById(R.id.buy);
        mTopBuyLayout = (LinearLayout) findViewById(R.id.top_buy_layout);

        myScrollView.setOnScrollListener(this);

        //当布局的状态或者控件的可见性发生改变回调的接口
        findViewById(R.id.parent_layout).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                //这一步很重要，使得上面的购买布局和下面的购买布局重合
                onScroll(myScrollView.getScrollY());

                System.out.println(myScrollView.getScrollY());
                Log.i("TGA","TGA parent_layout:"+myScrollView.getScrollY());
            }
        });
    }

    @Override
    public void onScroll(int scrollY) {
        int mBuyLayout2ParentTop = Math.max(scrollY, mBuyLayout.getTop());
        mTopBuyLayout.layout(0, mBuyLayout2ParentTop, mTopBuyLayout.getWidth(), mBuyLayout2ParentTop + mTopBuyLayout.getHeight());
    }
}
