package com.nanochap.customringchart_master;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    //环形图
    private CircularGraphView mCircularGraphView;
    //柱状图
    private HistogramView barChart;
    List<Double> datasList=new ArrayList<>();
    List<String> description=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mCircularGraphView = findViewById(R.id.circularView);
        barChart = findViewById(R.id.barChart);
        //设置环形图值
        mCircularGraphView.setAnnularData((float) (2.0 / 6.0 * 90), (float)(3.0 / 6.0 * 90),
                (float)(2.0 / 6.0 * 90), (float)(4.0 / 6.0 * 90)
        );
        mCircularGraphView.setOnClickListener(new CircularGraphView.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(int region) {
                Log.e("TAGS","区域"+region);
            }
        });
        //柱状图
        datasList.add(1.0);
        datasList.add(5.0);
        datasList.add(3.0);
        datasList.add(2.0);

        description.add(getString(R.string.time_interval_one));
        description.add(getString(R.string.time_interval_two));
        description.add(getString(R.string.time_interval_three));
        description.add(getString(R.string.time_interval_four));
        
        barChart.setDatas(datasList, description, true);
        barChart.setOnItemClick(new BaseChart.setOnRangeBarItemClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onItemClick(int position) {
                Log.e("TAGS",position+"");
            }
        });
    }
}