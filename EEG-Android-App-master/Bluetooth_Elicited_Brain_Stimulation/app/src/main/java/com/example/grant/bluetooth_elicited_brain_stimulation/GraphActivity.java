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
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by alex m on 3/9/16
 *
 * Views: content_graph.xml and activity_graph.xml
 */
public class GraphActivity extends AppCompatActivity {

    private RelativeLayout mainLayout;

    private boolean[] channelList;

    private LineChart mChart;

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

    private double[] eegData;


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

                    eegData = IEdk.IEE_GetAverageBandPowers(Channel_list[0]);
                    if(eegData != null && eegData.length == 5){
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
                    new Entry((float) eegData[1], set
                            .getEntryCount()), 0);

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
//                        if(isEnablGetData && isEnableWriteFile)handler.sendEmptyMessage(2);
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
