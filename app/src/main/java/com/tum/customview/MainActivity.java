package com.tum.customview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.tum.customview.view.RandomView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RandomView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();
}

    private void initView() {
        rv = (RandomView) findViewById(R.id.rv);

        rv.setOneBitmap(R.drawable.one);
        rv.setTwoBitmap(R.drawable.two);
    }

    public void onClick(View v){
        rv.setXY();
    }
}
