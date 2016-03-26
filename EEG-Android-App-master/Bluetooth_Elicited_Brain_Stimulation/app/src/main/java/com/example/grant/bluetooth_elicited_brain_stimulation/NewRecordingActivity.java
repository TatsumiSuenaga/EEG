package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.emotiv.insight.IEdk;
import com.emotiv.insight.IEdkErrorCode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class NewRecordingActivity extends AppCompatActivity {

    private static final String TAG = NewRecordingActivity.class.getSimpleName();
    private Button mButton;
    private ListView mChannelList;private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean lock = false;
    private boolean isEnablGetData = false;
    private boolean isEnableWriteFile = false;
    int userId;
    private BufferedWriter motion_writer;
    IEdk.IEE_DataChannel_t[] Channel_list = {IEdk.IEE_DataChannel_t.IED_AF3, IEdk.IEE_DataChannel_t.IED_T7, IEdk.IEE_DataChannel_t.IED_Pz,
            IEdk.IEE_DataChannel_t.IED_T8, IEdk.IEE_DataChannel_t.IED_AF4};
    String[] Name_Channel = {"AF3","T7","Pz","T8","AF4"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recording);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //list view will go here

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        mButton = (Button) findViewById(R.id.tempButt);
        mButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d(TAG, "Confirm button OnClickListener");
                Intent i = new Intent(NewRecordingActivity.this, GraphActivity.class);
                startActivity(i);
                Log.e("FFTSample","Start Write File");
                setDataFile();
                isEnableWriteFile = true;

            }
        });

        mChannelList = (ListView) findViewById(R.id.channelList);
        String[] channels = new String[]{"Channel 1", "Channel 2", "Channel 3",
                "Channel 4", "Channel 5", "Channel 6", "Channel 7", "Channel 8", "Channel 9",
                "Channel 10", "Channel 11", "Channel 12", "Channel 13", "Channel 14"};

        /**First Param: Context
         * Second Param: Layout for the Row
         * Third Param: TextView ID
         * Fourth Param: Array of Data
         */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewRecordingActivity.this,
                android.R.layout.simple_list_item_1, android.R.id.text1, channels );

        //Temporary adapter just for UI, this will be later made into different fragment probably
        mChannelList.setAdapter(adapter);
        mChannelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ch = (String) mChannelList.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        ch + " is turned on", Toast.LENGTH_SHORT).show();
            }
        });


        IEdk.IEE_EngineConnect(this, "");
        Thread processingThread=new Thread()
        {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                while(true)
                {
                    try
                    {
                        handler.sendEmptyMessage(0);
                        handler.sendEmptyMessage(1);
                        if(isEnablGetData && isEnableWriteFile)handler.sendEmptyMessage(2);
                        Thread.sleep(5);
                    }

                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        };
        processingThread.start();



    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 0:
                    int state = IEdk.IEE_EngineGetNextEvent();
                    if (state == IEdkErrorCode.EDK_OK.ToInt()) {
                        int eventType = IEdk.IEE_EmoEngineEventGetType();
                        userId = IEdk.IEE_EmoEngineEventGetUserId();
                        if(eventType == IEdk.IEE_Event_t.IEE_UserAdded.ToInt()){
                            Log.e("SDK","User added");
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
				/*Connect device with Insight headset*/
//				int number = IEdk.IEE_GetInsightDeviceCount();
//				if(number != 0) {
//					if(!lock){
//						lock = true;
//						IEdk.IEE_ConnectInsightDevice(0);
//					}
//				}
                    /**************************************/
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
                    for(int i=0; i < Channel_list.length; i++)
                    {
                        double[] data = IEdk.IEE_GetAverageBandPowers(Channel_list[i]);
                        if(data.length == 5){
                            try {
                                motion_writer.write(Name_Channel[i] + ",");
                                for(int j=0; j < data.length;j++)
                                    addData(data[j]);
                                motion_writer.newLine();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    break;
            }

        }

    };

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
