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
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.emotiv.insight.IEdk;
import com.emotiv.insight.IEdkErrorCode;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by alex m on 3/9/16
 *
 * Views: content_graph.xml and activity_graph.xml
 */
public class GraphActivity extends AppCompatActivity {

    private boolean[] channelList;

    private ArrayList<LineDataSet> mDataSets = new ArrayList<>();
    private LineChart mChart;
    private LineData mData;
    private ImageButton mPlayStopButton;
    private boolean mIsRecording = false;

    private final EEGDataHandler handler = new EEGDataHandler();
    private static final int REQUEST_ENABLE_BT = 1;
    private boolean isEnableGetData = false;
    private boolean isEnableWriteFile = false;
    private BufferedWriter motion_writer;

    private double[][] eegData = new double[14][5];
    private int[] channelIndex;
    int number;

    private int[] channelColors = new int[] {Color.parseColor("#F44336"),Color.parseColor("#9C27B0"),
            Color.parseColor("#2196F3"), Color.parseColor("#03A9F4"), Color.parseColor("#009688"),
            Color.parseColor("#4CAF50"), Color.parseColor("#CDDC39"), Color.parseColor("#FFEB3B"),
            Color.parseColor("#FF9800"), Color.parseColor("#FF5722"), Color.parseColor("#607D8B"),
            Color.parseColor("#795548"), Color.parseColor("#69F0AE"), Color.parseColor("#E91E63")
    };

    IEdk.IEE_DataChannel_t[] Channel_list = {
            IEdk.IEE_DataChannel_t.IED_AF3, IEdk.IEE_DataChannel_t.IED_T7, IEdk.IEE_DataChannel_t.IED_T8,
            IEdk.IEE_DataChannel_t.IED_AF4, IEdk.IEE_DataChannel_t.IED_F3, IEdk.IEE_DataChannel_t.IED_F4,
            IEdk.IEE_DataChannel_t.IED_F7, IEdk.IEE_DataChannel_t.IED_F8, IEdk.IEE_DataChannel_t.IED_FC5,
            IEdk.IEE_DataChannel_t.IED_FC6, IEdk.IEE_DataChannel_t.IED_P7, IEdk.IEE_DataChannel_t.IED_P8,
            IEdk.IEE_DataChannel_t.IED_O1, IEdk.IEE_DataChannel_t.IED_O2};

    String[] Name_Channel = {"AF3", "T7", "T8", "AF4", "F3", "F4", "F7", "F8", "FC5", "FC6", "P7", "P8", "O1", "O2"};

    LinearLayout mainLayout;

    BluetoothAdapter mBluetoothAdapter;

    private class EEGDataHandler extends Handler {

        public EEGDataHandler() {}

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    checkEnableGetData();
                    break;
                case 1:
                    checkEpocPlusConnection();
                    break;
                case 2:
                    addEntry();
                    break;
            }
        }

        private void checkEnableGetData()
        {
            int state = IEdk.IEE_EngineGetNextEvent();
            Log.e("IEdk Error", "Error event with IEdk");
            if (state == IEdkErrorCode.EDK_OK.ToInt()) {
                int eventType = IEdk.IEE_EmoEngineEventGetType();
                int userId = IEdk.IEE_EmoEngineEventGetUserId();
                if (eventType == IEdk.IEE_Event_t.IEE_UserAdded.ToInt()) {
                    Log.e("SDK", "User added");
                    IEdk.IEE_FFTSetWindowingType(userId, IEdk.IEE_WindowsType_t.IEE_BLACKMAN);
                    isEnableGetData = true;
                }
                else if (eventType == IEdk.IEE_Event_t.IEE_UserRemoved.ToInt()) {
                    Log.e("SDK", "User removed");
                    isEnableGetData = false;
                }
            }
        }

        private void checkEpocPlusConnection()
        {
            /*Connect device with Epoc Plus headset*/
            number = IEdk.IEE_GetEpocPlusDeviceCount();
            if (number != 0) {
                IEdk.IEE_ConnectEpocPlusDevice(0, false);
            }
            else {
                Log.e("EPOC Device Count", "Cannot connect to Epoc Plus Headset");
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

        mainLayout = (LinearLayout) findViewById(R.id.graphLayout);

        //create line chart
        mChart = new LineChart(this);
        //add to mainLayout
        mChart.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        mainLayout.addView(mChart);

        mPlayStopButton = (ImageButton)findViewById(R.id.start_recording_button);

        mPlayStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsRecording)
                {
                    // Stop recording
                    mPlayStopButton.setImageResource(R.mipmap.start_recording_button);
                    Log.e("Graph Recording", "Stop Write File");
                    StopWriteFile();
                    isEnableWriteFile = false;
                }
                else
                {
                    if(isEnableGetData)
                    {
                        // Start recording
                        mPlayStopButton.setImageResource(R.mipmap.stop_recording_button);
                        Log.e("Graph Recording", "Start Write File");
                        setDataFile();
                        isEnableWriteFile = true;
                    }
                    else {
                        Toast.makeText(GraphActivity.this, "Error: Cannot write to file!", Toast.LENGTH_SHORT).show();
                    }
                }
                mIsRecording = !mIsRecording;
            }
        });

        //initialize mDataSets from channels selected
        initializeDataChannels();

        //initialize mChart
        configureChart();

        //alternative background color
        // mChart.setBackgroundColor(Color.LTGRAY);


        //data.setValueTextColor(Color.WHITE);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        IEdk.IEE_EngineConnect(this, "");
    }

    //adds all selected channels to the ArrayList of dataSets so they can be displayed
    private void initializeDataChannels() {
        int counter = 0;
        channelIndex = new int[channelList.length];
        for (int i = 0; i < channelList.length; i++) {
            //if the channel is true, it was selected for data display
            if (channelList[i]) {
                LineDataSet dataSet = new LineDataSet(null, Name_Channel[i]);
                dataSet.setLineWidth(2.5f);
                dataSet.setDrawCircles(false);
                dataSet.setHighLightColor(Color.rgb(190, 190, 190));
                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                dataSet.setValueTextSize(10f);

                //set dataSet color based on channel
                dataSet.setColor(channelColors[i]);

                mDataSets.add(dataSet);
                channelIndex[counter] = i;
                counter++;
            }
        }
    }

    private void configureChart() {
        mChart.setDescription("");
        mChart.setDrawGridBackground(false);
        mChart.getAxisRight().setEnabled(false);
        mChart.setTouchEnabled(true);
        mChart.setScaleEnabled(false);
        mChart.setPinchZoom(true);
        mChart.getXAxis().setDrawAxisLine(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getXAxis().setAvoidFirstLastClipping(true);
        mChart.getXAxis().setTextColor(Color.GREEN);
        mChart.getAxisLeft().setDrawGridLines(true);
        mChart.getAxisLeft().setTextColor(Color.GREEN);
        mChart.getAxisLeft().setAxisMinValue(0);
        mChart.getAxisLeft().setAxisMaxValue(20);

        //adds all dataSets to mData to be displayed on graph
        mData = new LineData();
        for (LineDataSet mSet : mDataSets) {
            mData.addDataSet(mSet);
        }
        mChart.setData(mData);

        //get legend object and customize
        Legend legend = mChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(Color.BLUE);

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

        int index = 0;
        // Get readings for chosen channels
        for(int x = 0; x < channelIndex.length; x++)
        {
            eegData[x] = IEdk.IEE_GetAverageBandPowers(Channel_list[x]);
        }

        for (int i = 0; i < mData.getDataSetCount(); i++) {
            mData.addXValue("");
            mData.addEntry(new Entry((float) eegData[channelIndex[index]][0], mData.getDataSetByIndex(i).getEntryCount()), 0);
            index++;
            //notify chart data have changed
            mChart.notifyDataSetChanged();
            //limit number of visible entries
            mChart.setVisibleXRange(10, 10);
            //scroll to last entry
            mChart.moveViewToX(mData.getXValCount());
            mChart.getRootView().invalidate();
            mChart.invalidate();
        }

        if(isEnableWriteFile && mIsRecording)
        {
            for(int i = 0; i < channelIndex.length; i++)
            {
                double[] data = eegData[i];
                try {
                    motion_writer.write(Name_Channel[i] + ",");
                    for(double j:data)
                        addData(j);
                    motion_writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        handler.sendEmptyMessage(0);
                        handler.sendEmptyMessage(1);
                        if (isEnableGetData && eegData != null) handler.sendEmptyMessage(2);
                        Thread.sleep(200);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void setDataFile() {
        try {
            String eeg_header = "Channel , Theta ,Alpha ,Low beta ,High beta , Gamma ";
            File root = Environment.getExternalStorageDirectory();
            String file_path = root.getAbsolutePath() + "/EEGSample/";
            File folder = new File(file_path);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            motion_writer = new BufferedWriter(new FileWriter(file_path + "bandpowerValue.csv"));
            motion_writer.write(eeg_header);
            motion_writer.newLine();
        } catch (Exception e) {
            Log.e("", "Exception" + e.getMessage());
        }
    }

    private void StopWriteFile() {
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
     * @param data - double array of eeg data
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
