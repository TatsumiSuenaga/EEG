package com.example.grant.bluetooth_elicited_brain_stimulation;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.emotiv.insight.IEdk;
import com.emotiv.insight.IEdkErrorCode;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by alex m on 3/9/16
 *
 * Views: content_graph.xml and activity_graph.xml
 */
public class GraphActivity extends AppCompatActivity {

    private RelativeLayout mainLayout;

    private boolean[] channelList;

    private ArrayList<LineDataSet> mDataSets;
    private LineChart mChart;
    private LineData mData;

    private final MyHandler handler = new MyHandler(this);
    private static final String TAG = NewRecordingActivity.class.getSimpleName();
    private Button mButton;
    BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private boolean lock = false;
    private boolean isEnablGetData = false;
    private boolean isEnableWriteFile = false;
    int userId;
    private BufferedWriter motion_writer;


    IEdk.IEE_DataChannel_t[] Channel_list = {
            IEdk.IEE_DataChannel_t.IED_AF3, IEdk.IEE_DataChannel_t.IED_T7, IEdk.IEE_DataChannel_t.IED_T8,
            IEdk.IEE_DataChannel_t.IED_AF4, IEdk.IEE_DataChannel_t.IED_F3, IEdk.IEE_DataChannel_t.IED_F4,
            IEdk.IEE_DataChannel_t.IED_F7, IEdk.IEE_DataChannel_t.IED_F8, IEdk.IEE_DataChannel_t.IED_FC5,
            IEdk.IEE_DataChannel_t.IED_FC6, IEdk.IEE_DataChannel_t.IED_P7, IEdk.IEE_DataChannel_t.IED_P8,
            IEdk.IEE_DataChannel_t.IED_O1, IEdk.IEE_DataChannel_t.IED_O2};

    String[] Name_Channel = {"AF3","T7","T8","AF4", "F3", "F4", "F7", "F8", "FC5", "FC6", "P7", "P8", "O1", "O2"};

    private double[][] eegData;
    private int[] channelIndex;


    /**private static final String EXTRA_RECORD_READING =
            "com.example.grant.bluetooth_elicited_brain_stimulation.record_reading";**/

    /** Not sure if doing puttingExtra with this activity
    public static Intent newIntent (Context packageContext, Recording r){
        Intent i = new Intent(packageContext, GraphActivity.class);
        i.putExtra(EXTRA_RECORD_READING, r);
        return i;
    }**/

    private class MyHandler extends Handler {

        private final WeakReference<GraphActivity> mActivity;

        public MyHandler(GraphActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 0:
                    int state = IEdk.IEE_EngineGetNextEvent();
                    if (state == IEdkErrorCode.EDK_OK.ToInt()) {
                        int eventType = IEdk.IEE_EmoEngineEventGetType();
                        userId = IEdk.IEE_EmoEngineEventGetUserId();
                        if(eventType == IEdk.IEE_Event_t.IEE_UserAdded.ToInt()){
                            Log.e("SDK", "User added");
                            IEdk.IEE_FFTSetWindowingType(userId, IEdk.IEE_WindowsType_t.IEE_BLACKMAN);
                            isEnablGetData = true;
                        }
                        if(eventType == IEdk.IEE_Event_t.IEE_UserRemoved.ToInt()){
                            Log.e("SDK","User removed");
                            isEnablGetData = false;
                        }
                    }

                    break;
                case 1:
                    /*Connect device with Epoc Plus headset*/
                    int number = IEdk.IEE_GetEpocPlusDeviceCount();
                    if(number != 0) {
                        if(!lock){
                            lock = true;
                            IEdk.IEE_ConnectEpocPlusDevice(0,false);
                        }
                    }
                    /**************************************/
                    else lock = false;
                    break;
                case 2:
//                    for(int i=0; i < Channel_list.length; i++)
//                    {
//                        data = IEdk.IEE_GetAverageBandPowers(Channel_list[i]);
//                        if(data != null && data.length == 5){
//                            try {
//                                motion_writer.write(Name_Channel[i] + ",");
//                                for(int j=0; j < data.length;j++)
//                                    addData(data[j]);
//                                motion_writer.newLine();
//                            } catch (IOException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
//                        }
//                    }


                    eegData[0] = IEdk.IEE_GetAverageBandPowers(Channel_list[0]);
                    eegData[1] = IEdk.IEE_GetAverageBandPowers(Channel_list[1]);
                    eegData[2] = IEdk.IEE_GetAverageBandPowers(Channel_list[2]);
                    eegData[3] = IEdk.IEE_GetAverageBandPowers(Channel_list[3]);
                    eegData[4] = IEdk.IEE_GetAverageBandPowers(Channel_list[4]);
                    eegData[5] = IEdk.IEE_GetAverageBandPowers(Channel_list[5]);
                    eegData[6] = IEdk.IEE_GetAverageBandPowers(Channel_list[6]);
                    eegData[7] = IEdk.IEE_GetAverageBandPowers(Channel_list[7]);
                    eegData[8] = IEdk.IEE_GetAverageBandPowers(Channel_list[8]);
                    eegData[9] = IEdk.IEE_GetAverageBandPowers(Channel_list[9]);
                    eegData[10] = IEdk.IEE_GetAverageBandPowers(Channel_list[10]);
                    eegData[11] = IEdk.IEE_GetAverageBandPowers(Channel_list[11]);
                    eegData[12] = IEdk.IEE_GetAverageBandPowers(Channel_list[12]);
                    eegData[13] = IEdk.IEE_GetAverageBandPowers(Channel_list[13]);

                    if(eegData != null){
                        addEntry();
                    }

                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        channelList = i.getBooleanArrayExtra("channelList");
        setContentView(R.layout.activity_graph);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainLayout = (RelativeLayout)findViewById(R.id.graphLayout);

        //create line chart
        mChart = new LineChart(this);
        //add to mainLayout
        mainLayout.addView(mChart, new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));
        //mainLayout.addView(mChart);

        //initialize mDataSets from channels selected
        initializeDataChannels();

        //initialize mChart
        configureChart();

        //alternative background color
       // mChart.setBackgroundColor(Color.LTGRAY);


        //data.setValueTextColor(Color.WHITE);


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

        // NEW CODE

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

//        mButton = (Button) findViewById(R.id.record_btn);
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "Confirm button OnClickListener");
//                if (mButton.getText().equals("Start")) {
//                    Log.e("FFTSample", "Start Write File");
//                    setDataFile();
//                    isEnableWriteFile = true;
//                    mButton.setText("Stop");
//                } else {
//                    Log.e("FFTSample", "Stop Write File");
//                    StopWriteFile();
//                    isEnableWriteFile = false;
//                    mButton.setText("Start");
//                }
//            }
//        });

        IEdk.IEE_EngineConnect(this, "");
//        Thread processingThread = new Thread()
//        {
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                super.run();
//                while(true)
//                {
//                    try
//                    {
//                        handler.sendEmptyMessage(0);
//                        handler.sendEmptyMessage(1);
////                        if(isEnablGetData && isEnableWriteFile)handler.sendEmptyMessage(2);
//                        if(isEnablGetData)handler.sendEmptyMessage(2);
//                        Thread.sleep(50);
//                    }
//                    catch (Exception ex)
//                    {
//                        ex.printStackTrace();
//                    }
//                }
//            }
//        };
//        processingThread.start();

    }

    //adds all selected channels to the ArrayList of dataSets so they can be displayed
    private void initializeDataChannels() {
        int counter = 0;
        for(int i = 0; i<channelList.length;i++) {
            //if the channel is true, it was selected for data display
            if(channelList[i]) {
                LineDataSet dataSet = new LineDataSet(null, Name_Channel[i]);
                dataSet.setLineWidth(2.5f);
                dataSet.setDrawCircles(false);
                dataSet.setHighLightColor(Color.rgb(190, 190, 190));
                //set dataSet color based on channel
                if(i==0) {
                    dataSet.setColor(Color.parseColor("#F44336"));
                }
                else if(i==1) {
                    dataSet.setColor(Color.parseColor("#9C27B0"));
                }
                else if(i==2) {
                    dataSet.setColor(Color.parseColor("#2196F3"));
                }
                else if(i==3) {
                    dataSet.setColor(Color.parseColor("#03A9F4"));
                }
                else if(i==4) {
                    dataSet.setColor(Color.parseColor("#009688"));
                }
                else if(i==5) {
                    dataSet.setColor(Color.parseColor("#4CAF50"));
                }
                else if(i==6) {
                    dataSet.setColor(Color.parseColor("#CDDC39"));
                }
                else if(i==7) {
                    dataSet.setColor(Color.parseColor("#FFEB3B"));
                }
                else if(i==8) {
                    dataSet.setColor(Color.parseColor("#FF9800"));
                }
                else if(i==9) {
                    dataSet.setColor(Color.parseColor("#FF5722"));
                }
                else if(i==10) {
                    dataSet.setColor(Color.parseColor("#607D8B"));
                }
                else if(i==11) {
                    dataSet.setColor(Color.parseColor("#795548"));
                }
                else if(i==12) {
                    dataSet.setColor(Color.parseColor("#69F0AE"));
                }
                else if(i==13) {
                    dataSet.setColor(Color.parseColor("#E91E63"));
                }



                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                dataSet.setValueTextSize(10f);

                mDataSets.add(dataSet);
                channelIndex[counter] = i;
                counter++;
            }
        }
    }

    private void configureChart() {
        mChart.setDescription("");
        mChart.setDrawGridBackground(false);
        mChart.setData(new LineData());
        mChart.getAxisRight().setEnabled(false);
        mChart.setTouchEnabled(true);
        mChart.setScaleEnabled(false);
        mChart.setPinchZoom(true);
        mChart.getXAxis().setDrawAxisLine(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getAxisLeft().setDrawGridLines(false);

        mChart.getAxisLeft().setAxisMinValue(0);
        mChart.getAxisLeft().setAxisMaxValue(10);


        //adds all dataSets to mData to be displayed on graph
        mData= new LineData();
        for(LineDataSet mSet : mDataSets) {
            mData.addDataSet(mSet);
        }

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                mChart.setDescription(Float.toString(e.getVal()) + " mV");
            }

            @Override
            public void onNothingSelected() {
                mChart.setDescription("");
            }
        });

    }
    private void addEntry() {
        LineData data = mChart.getData();
        int index = 0;
        for(LineDataSet dataSet : mDataSets) {
            mData.addXValue("");
            mData.addEntry(
                    new Entry((float)eegData[channelIndex[index]][0], dataSet.getEntryCount()),0);
            index++;
            //notify chart data have changed
            mChart.notifyDataSetChanged();
            //limit number of visible entries
            mChart.setVisibleXRange(10, 10);
            //scroll to last entry
            mChart.moveViewToX(data.getXValCount());
            mChart.getRootView().invalidate();
            mChart.invalidate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //for simulating real time data addition

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true)
                {
                    try
                    {
                        handler.sendEmptyMessage(0);
                        handler.sendEmptyMessage(1);
//                        if(isEnableGetData && isEnableWriteFile)handler.sendEmptyMessage(2);
                        if(isEnablGetData)handler.sendEmptyMessage(2);
                        Thread.sleep(200);
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
                //add 100 test entries
//                for(int i = 0; i<120; i++) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            addEntry(); //chart is notified of update via addEntry method
//                        }
//                    });
//                    //pause between adds
//                    try {
//                        Thread.sleep(50);
//                    } catch (InterruptedException e) {
//                        //not sure what to do with errors
//                    }
//                }

            }
        }).start();

    }
    //method for making dataset
    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Beta Waves");
        set.setDrawCircles(false);
        set.setCubicIntensity(0.2f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.rgb(102, 187, 106));
        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2f);
        set.setCircleSize(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.BLACK);
        set.setValueTextSize(10f);

        return set;
    }

    private void setDataFile() {
        try {
            String eeg_header = "Channel , Theta ,Alpha ,Low beta ,High beta , Gamma ";
            File root = Environment.getExternalStorageDirectory();
            String file_path = root.getAbsolutePath()+ "/FFTSample/";
            File folder=new File(file_path);
            if(!folder.exists())
            {
                folder.mkdirs();
            }
            motion_writer = new BufferedWriter(new FileWriter(file_path+"bandpowerValue.csv"));
            motion_writer.write(eeg_header);
            motion_writer.newLine();
        } catch (Exception e) {
            Log.e("","Exception"+ e.getMessage());
        }
    }
    private void StopWriteFile(){
        try {
            motion_writer.flush();
            motion_writer.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    /**
     * public void addEEGData(Double[][] eegs) Add EEG Data for write int the
     * EEG File
     *
     * @param data
     *            - double array of eeg data
     */
    public void addData(double data) {

        if (motion_writer == null) {
            return;
        }

        String input = "";
        input += (String.valueOf(data) + ",");
        try {
            motion_writer.write(input);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
