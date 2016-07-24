package com.tum.customview.draggridview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import com.tum.customview.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/7/23.
 */
public class FragmentOne extends Fragment {
    private DragGridView mDragGridView;
    private List<HashMap<String, Object>> dataSourceList = new ArrayList<HashMap<String, Object>>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dragfragitem1,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mDragGridView = (DragGridView)view.findViewById(R.id.dragGridView);

        for (int i = 0; i < 30; i++) {
            HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
            itemHashMap.put("item_image",R.drawable.com_tencent_open_notice_msg_icon_big);
            itemHashMap.put("item_text", "拖拽 " + Integer.toString(i));
            dataSourceList.add(itemHashMap);
        }


        final SimpleAdapter mSimpleAdapter = new SimpleAdapter(getContext(), dataSourceList,
                R.layout.grid_item, new String[] { "item_image", "item_text" },
                new int[] { R.id.item_image, R.id.item_text });

        mDragGridView.setAdapter(mSimpleAdapter);

        /*mDragGridView.setOnChangeListener(new DragGridView.OnChanageListener() {

            @Override
            public void onChange(int from, int to) {
                HashMap<String, Object> temp = dataSourceList.get(from);
                //直接交互item
                //				dataSourceList.set(from, dataSourceList.get(to));
                //				dataSourceList.set(to, temp);


                //这里的处理需要注意下
                if (from < to) {
                    for (int i = from; i < to; i++) {
                        Collections.swap(dataSourceList, i, i + 1);
                    }
                } else if (from > to) {
                    for (int i = from; i > to; i--) {
                        Collections.swap(dataSourceList, i, i - 1);
                    }
                }

                dataSourceList.set(to, temp);

                mSimpleAdapter.notifyDataSetChanged();


            }
        });*/
    }
}
