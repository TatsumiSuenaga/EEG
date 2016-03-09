package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

/**
 * Created by Tatsumi on 3/5/16
 */

public class RecordReadingActivity extends AppCompatActivity {

    private RelativeLayout mainLayout;

    private TextView mIdText;

    private LineChart mChart;


    private static final String EXTRA_RECORD_READING =
            "com.example.grant.bluetooth_elicited_brain_stimulation.record_reading";

    public static Intent newIntent (Context packageContext, Recording r){
        Intent i = new Intent(packageContext, RecordReadingActivity.class);
        i.putExtra(EXTRA_RECORD_READING, r);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_reading);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainLayout = (RelativeLayout)findViewById(R.id.recordReadingLayout);

        //create line chart
        mChart = new LineChart(this);
        //add to mainLayout
        mainLayout.addView(mChart, new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));
        //mainLayout.addView(mChart);

        //customize line chart
        mChart.setDescription("this is a graph");

        mChart.setNoDataTextDescription("test data");

        //enable value highlighting
        mChart.setHighlightPerTapEnabled(true);

        //enable touch gestures
        mChart.setTouchEnabled(true);

        //enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        //enable pinch zoom to avoid scaling x and y axes separately
        mChart.setPinchZoom(true);

        //alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);

        //work with data
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        //add data to chart, will need to have function
        // to dynamically update with bluetooth data
        mChart.setData(data);

        //get legend object and customize
        Legend legend = mChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(Color.WHITE);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);

        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setTextColor(Color.WHITE);
        yAxis.setAxisMaxValue(100f);
        yAxis.setDrawGridLines(true);

        YAxis yAxis2 = mChart.getAxisRight();
        yAxis2.setEnabled(false);

        Intent i = getIntent();
        Recording r = i.getParcelableExtra(EXTRA_RECORD_READING);
        mIdText = (TextView) findViewById(R.id.idTitle);
        mIdText.setText("Patient Name: " + r.getPatient() + "\nPatient ID: " + r.getID() + "\nRecording Date: " + r.getDate()
                + "\nMuscle: " + r.getMuscle());

    }
    private void addEntry() {
        LineData data = mChart.getData();

        if (data !=null) {
            LineDataSet set = data.getDataSetByIndex(0);

            if(set==null){
                //create if null
                set = createSet();
                data.addDataSet(set);
            }
            data.addXValue("");
            data.addEntry(
                    new Entry((float)(Math.random()*75)+20f,set
                    .getEntryCount()), 0);

            //notify chart data have changed
            mChart.notifyDataSetChanged();
            //limit number of visible entries
            mChart.setVisibleXRange(10,10);
            //scroll to last entry
            mChart.moveViewToX(data.getXValCount()-7);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //for simulating real time data addition

        new Thread(new Runnable() {
            @Override
            public void run() {
                //add 100 test entries
                for(int i = 0; i<100; i++) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry(); //chart is notified of update via addEntry method
                        }
                    });
                    //pause between adds
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        //not sure what to do with errors
                    }
                }
            }
        }).start();

    }
    //method for making dataset
    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Tatsumi Likes Balls");
        set.setDrawCubic(true);
        set.setCubicIntensity(0.2f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2f);
        set.setCircleSize(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.WHITE);
        set.setValueTextSize(10f);

        return set;
    }

}
