package com.example.grant.bluetooth_elicited_brain_stimulation;


import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.emotiv.insight.IEdk;
import com.emotiv.insight.IEdkErrorCode;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by alex m on 3/9/16
 * <p/>
 * Views: content_graph.xml and activity_graph.xml
 */
public class GraphActivity extends AppCompatActivity {

    private boolean[] channelList;

    private ArrayList<LineDataSet> mDataSets = new ArrayList<>();
    private LineChart mChart;
    private LineData mData;
    private ImageButton mPlayStopButton;
    private boolean mIsRecording = false;
    private boolean mUseSampleData = false;

    private final EEGDataHandler handler = new EEGDataHandler();
    private static final int REQUEST_ENABLE_BT = 1;
    private boolean isEnableGetData = false;
    private boolean isEnableWriteFile = false;
    private BufferedWriter motion_writer;

    private double[][] eegData = new double[14][5];
    private int[] channelIndex;
    int number;
    int counter = 0;

    private static final int REQUEST_WRITE_STORAGE = 112;

    private int[] channelColors = new int[]{Color.parseColor("#F44336"), Color.parseColor("#9C27B0"),
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
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Graph Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.grant.bluetooth_elicited_brain_stimulation/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Graph Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.grant.bluetooth_elicited_brain_stimulation/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private class EEGDataHandler extends Handler {

        public EEGDataHandler() {
        }

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

        private void checkEnableGetData() {
            int state = IEdk.IEE_EngineGetNextEvent();
//            Log.e("IEdk Error", "Error event with IEdk");
            if (state == IEdkErrorCode.EDK_OK.ToInt()) {
                int eventType = IEdk.IEE_EmoEngineEventGetType();
                int userId = IEdk.IEE_EmoEngineEventGetUserId();
                if (eventType == IEdk.IEE_Event_t.IEE_UserAdded.ToInt()) {
                    Log.e("SDK", "User added");
                    IEdk.IEE_FFTSetWindowingType(userId, IEdk.IEE_WindowsType_t.IEE_BLACKMAN);
                    isEnableGetData = true;
                } else if (eventType == IEdk.IEE_Event_t.IEE_UserRemoved.ToInt()) {
                    Log.e("SDK", "User removed");
                    isEnableGetData = false;
                }
            }
        }

        private void checkEpocPlusConnection() {
            /*Connect device with Epoc Plus headset*/
            number = IEdk.IEE_GetEpocPlusDeviceCount();
            if (number != 0) {
                IEdk.IEE_ConnectEpocPlusDevice(0, false);
            }
//            else {
//                Log.e("EPOC Device Count", "Cannot connect to Epoc Plus Headset");
//            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        channelList = i.getBooleanArrayExtra("channelList");

        //check if using sample data was selected
        // i.getBooleanExtra("sampleData",mUseSampleData);
        mUseSampleData = true;
        setContentView(R.layout.activity_graph);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainLayout = (LinearLayout) findViewById(R.id.graphLayout);

        //create line chart
        mChart = new LineChart(this);
        //add to mainLayout
        mChart.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        mainLayout.addView(mChart);

        mPlayStopButton = (ImageButton) findViewById(R.id.start_recording_button);

        mPlayStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validPermission()) {
                    if (mIsRecording) {
                        // Stop recording
                        mPlayStopButton.setImageResource(R.mipmap.start_recording_button);
                        Log.e("Graph Recording", "Stop Write File");
                        StopWriteFile();
                        isEnableWriteFile = false;
                    } else {
                        if (isEnableGetData) {
                            // Start recording
                            mPlayStopButton.setImageResource(R.mipmap.stop_recording_button);
                            setDataFile();
                            isEnableWriteFile = true;
                            Log.e("Graph Recording", "Start Write File");
                        } else if (mUseSampleData) {
                            mPlayStopButton.setImageResource(R.mipmap.stop_recording_button);
                            setDataFile();
                            isEnableWriteFile = true;
                            Log.e("Graph Sample Recording", "Start Write File");
                        } else {
                            Toast.makeText(GraphActivity.this, "Error: Cannot write to file!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    mIsRecording = !mIsRecording;
                    Log.e("mIsRecording", "misrecording = " + mIsRecording);
                } else {
                    getWritePermission();
                }
            }
        });

        //initialize mDataSets from channels selected
        initializeDataChannels();

        //initialize mChart
        configureChart();

        //alternative background color
        // mChart.setBackgroundColor(Color.LTGRAY);


        //data.setValueTextColor(Color.WHITE);
        //if not a sample data recording
        if (!mUseSampleData) {
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            IEdk.IEE_EngineConnect(this, "");
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //adds all selected channels to the ArrayList of dataSets so they can be displayed
    private void initializeDataChannels() {
        counter = 0;
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
                dataSet.setDrawCubic(true);
                dataSet.setCubicIntensity(0.2f);
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
        Paint paint = mChart.getPaint(Chart.PAINT_INFO);
        paint.setTextSize(40f);
        paint.setColor(Color.parseColor("#66BB6A"));
        mChart.setNoDataText("Hit play button to begin graphing channels");
        mChart.setDrawGridBackground(false);
        mChart.getAxisRight().setEnabled(false);

        mChart.setTouchEnabled(true);
        mChart.setScaleEnabled(false);
        mChart.setPinchZoom(true);
        mChart.getXAxis().setDrawAxisLine(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getXAxis().setAvoidFirstLastClipping(true);
        mChart.getXAxis().setTextColor(Color.parseColor("#66BB6A"));
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getAxisLeft().setTextColor(Color.GREEN);
        mChart.getAxisLeft().setAxisMinValue(0);
        mChart.getAxisLeft().setAxisMaxValue(counter * 3);


        //adds all dataSets to mData to be displayed on graph
        mData = new LineData();
        for (LineDataSet mSet : mDataSets) {
            //Log.e("stuff","mset = "+mSet+" mDatasets = "+mDataSets);
            mData.addDataSet(mSet);
        }
        mChart.setData(mData);

        //get legend object and customize
        Legend legend = mChart.getLegend();
        legend.setFormSize(10f);
        legend.setForm(Legend.LegendForm.LINE);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

        legend.setTextColor(Color.BLUE);
        legend.setEnabled(true);

        //highlights selected graph points and displays units
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
        for (int x = 0; x < channelIndex.length; x++) {
            eegData[x] = IEdk.IEE_GetAverageBandPowers(Channel_list[x]);
        }
        //populate each channel's dataset with eegdata
        for (int i = 0; i < mData.getDataSetCount(); i++) {
            mData.addXValue("");
            //offset by 3*dataset index so graphs spaced out
            mData.addEntry(new Entry((float) eegData[channelIndex[index]][0]+3*i, mData.getDataSetByIndex(i).getEntryCount()), i);
            index++;
        }
        //notify chart data have changed
        mChart.notifyDataSetChanged();
        //limit number of visible entries
        mChart.setVisibleXRange(10, 10);
        //scroll viewport
        mChart.moveViewToX(mData.getXValCount() / mData.getDataSetCount() - 10);
        mChart.getRootView().invalidate();
        mChart.invalidate();

        //write each line to file
        if (isEnableWriteFile && mIsRecording && motion_writer != null) {
            for (int i = 0; i < channelIndex.length; i++) {
                double[] data = eegData[i];
                try {
                    motion_writer.write(Name_Channel[i] + ",");
                    for (double j : data)
                        addData(j);
                    motion_writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addSampleEntry() {

        int index = 0;
        // Get readings for chosen channels
        for (int x = 0; x < channelIndex.length; x++) {
            eegData[x][0] = (Math.random() * 2 + x * 3);
        }
        //populate each channel's dataset with eegdata
        for (int i = 0; i < mData.getDataSetCount(); i++) {
            mData.addXValue("");
            mData.addEntry(new Entry((float) eegData[i][0], mData.getDataSetByIndex(i).getEntryCount()), i);
            index++;
            //Log.e("stuff","mDataByIndex = "+mData.getDataSetByIndex(i)+" mDatasets = "+mDataSets +" eegData = "+eegData[channelIndex[index]][0]);
            //notify chart data have changed
            mChart.notifyDataSetChanged();
            //limit number of visible entries
            mChart.setVisibleXRange(10, 10);
            //scroll to last entry
            mChart.moveViewToX(mData.getXValCount() / mData.getDataSetCount() - 10);
            //refresh graph data
            mChart.getRootView().invalidate();
            mChart.invalidate();
        }
        //write each eeg line to file
        if (isEnableWriteFile && mIsRecording && motion_writer != null) {
            for (int i = 0; i < channelIndex.length; i++) {
                double[] data = eegData[i];
                try {
                    motion_writer.write(Name_Channel[i] + ",");
                    for (double j : data)
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!mUseSampleData) {
                                handler.sendEmptyMessage(0);
                                handler.sendEmptyMessage(1);
                                if (isEnableGetData && eegData != null)
                                    handler.sendEmptyMessage(2);
                            } else {
                                if (mIsRecording)
                                    addSampleEntry();
                            }
                        }
                    });

                    try {
                        Thread.sleep(150);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private boolean validPermission() {
        return (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
    }

    private void getWritePermission() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
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

            showDialog();

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

    public void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Send email")
                .setMessage("Would you like to send the recorded EEG data?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String filename = "bandpowerValue.csv";
                        File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/EEGSample/", filename);
                        Uri path = Uri.fromFile(filelocation);
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        // set the type to 'email'
                        emailIntent.setType("vnd.android.cursor.dir/email");
                        // the attachment
                        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
                        // the mail subject
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Raw EEG Data File");
                        try {
                            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
                        } catch (ActivityNotFoundException ex) {
                            Toast.makeText(GraphActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
