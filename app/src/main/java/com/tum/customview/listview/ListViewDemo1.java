package com.tum.customview.listview;

import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by GKF on 2016/7/19.
 */
public class ListViewDemo1 extends ListActivity implements AbsListView.OnScrollListener{

    /**
     * 设置布局显示属性
     */
    private LinearLayout.LayoutParams mLayoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
    /**
     * 设置布局显示目标最大化属性
     */
    private LinearLayout.LayoutParams FFlayoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);

    private ProgressBar progressBar;
    private LinearLayout loadingLayout;
    private ListView listView;

    private ListAdapter adapter = new ListAdapter();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //线性布局
        LinearLayout layout = new LinearLayout(this);
        //设置布局 水平方向
        layout.setOrientation(LinearLayout.HORIZONTAL);
        //进度条
        progressBar = new ProgressBar(this);
        //进度条显示位置
        progressBar.setPadding(0, 0, 15, 0);
        //把进度条加入到layout中
        layout.addView(progressBar, mLayoutParams);
        //文本内容
        TextView textView = new TextView(this);
        textView.setText("加载中。。。");
        textView.setGravity(Gravity.CENTER_VERTICAL);
        //把文本加入到layout中
        layout.addView(textView, FFlayoutParams);
        //设置layout的重力方向，即对齐方式是
        layout.setGravity(Gravity.CENTER);

        ///设置ListView的页脚layout
        loadingLayout = new LinearLayout(this);
        loadingLayout.addView(layout, mLayoutParams);
        loadingLayout.setGravity(Gravity.CENTER);

        //得到一个ListView用来显示条目
        listView = getListView();
        //添加到页脚显示
        listView.addFooterView(loadingLayout);
        //给ListView添加适配器
        setListAdapter(adapter);
        //给ListView注册滚动监听
        listView.setOnScrollListener(this);
    }

    int lastItem;

    @Override
    public void onScroll(AbsListView v, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
        Log.v("@@@@@@", "Scroll>>>first: " + firstVisibleItem + ", visible: " + visibleItemCount + ", total: " + totalItemCount);
        lastItem = firstVisibleItem + visibleItemCount - 1;
        Log.i("@@@@@@" , "Scroll>>>lastItem:" + lastItem);
        //显示50条ListItem，即0-49，因为onScroll是在“滑动”执行过之后才触发，所以用adapter.count<=41作条件
        if (adapter.count<=41) {
            if (firstVisibleItem+visibleItemCount==totalItemCount) {
                adapter.count += 10;
                adapter.notifyDataSetChanged();
                listView.setSelection(lastItem);
                Log.v("@@@@@@","onScroll  "+adapter.count);
                int currentPage=adapter.count/10;
                Toast.makeText(getApplicationContext(), "第"+currentPage+"页", Toast.LENGTH_LONG).show();
            }
        }else {
            listView.removeFooterView(loadingLayout);
        }
    }
    @Override
    public void onScrollStateChanged(AbsListView v, int state) {
        if (lastItem == adapter.count && state == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            Log.v("@@@@@@", "ScrollStateChanged>>>state:"+state+"lastItem:" + lastItem);
            if (adapter.count<=41) {
                adapter.count += 10;
                adapter.notifyDataSetChanged();
                Log.v("@@@@@@","onScrollStateChanged  "+adapter.count);
            }
        }
    }

    public class ListAdapter extends BaseAdapter {
        int count = 10;

        public int getCount() {
            return count;
        }

        public Object getItem(int pos) {
            return pos;
        }

        public long getItemId(int pos) {
            return pos;
        }

        public View getView(int pos, View v, ViewGroup p) {
            TextView view;
            if (v == null) {
                view = new TextView(ListViewDemo1.this);
            } else {
                view = (TextView) v;
            }
            view.setText("ListItem " + pos);
            view.setTextSize(20f);
            view.setGravity(Gravity.CENTER);
            view.setHeight(60);
            return view;
        }
    }

}
