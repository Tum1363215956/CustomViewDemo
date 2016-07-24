package com.tum.customview.draggridview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.tum.customview.R;

import java.util.ArrayList;
import java.util.List;

public class DragActivity extends AppCompatActivity {

    private ViewPager vp;
    private List<Fragment> fragArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);

        vp = (ViewPager)findViewById(R.id.vp);

        fragArray.add(new FragmentOne());
        fragArray.add(new FragmentTwo());

        vp.setAdapter(new FragAdapter(getSupportFragmentManager()));
    }

    class FragAdapter extends FragmentPagerAdapter{

        public FragAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragArray.get(position);
        }

        @Override
        public int getCount() {
            return fragArray.size();
        }
    }
}
