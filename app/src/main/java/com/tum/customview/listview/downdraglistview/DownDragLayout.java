package com.tum.customview.listview.downdraglistview;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tum.customview.R;

import java.util.LinkedList;

/**
 * Created by GKF on 2016/7/19.
 */
public class DownDragLayout extends AppCompatActivity {
    private LinkedList<String> data;
    private BaseAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downdraglayout);

        data = new LinkedList<String>();
        for(int i=0;i<10;i++){
            data.add(String.valueOf(i));
        }

        final MyListView listView = (MyListView) findViewById(R.id.listView);
        adapter = new BaseAdapter() {
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView= LayoutInflater.from(getApplicationContext()).inflate(R.layout.item, null);
                TextView textView = (TextView) convertView.findViewById(R.id.textView_item);
                textView.setText(data.get(position));
                return convertView;
            }

            public long getItemId(int position) {
                return position;
            }

            public Object getItem(int position) {
                return data.get(position);
            }

            public int getCount() {
                return data.size();
            }
        };
        listView.setAdapter(adapter);

        listView.setonRefreshListener(new MyListView.OnRefreshListener() {
            public void onRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        data.addFirst("刷新后的内容");
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        adapter.notifyDataSetChanged();
                        listView.onRefreshComplete();
                    }

                }.execute();
            }
        });
    }
}
