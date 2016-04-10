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

//import com.emotiv.insight.IEdk;
//import com.emotiv.insight.IEdkErrorCode;

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
    /**IEdk.IEE_DataChannel_t[] Channel_list = {IEdk.IEE_DataChannel_t.IED_AF3, IEdk.IEE_DataChannel_t.IED_T7, IEdk.IEE_DataChannel_t.IED_Pz,
     IEdk.IEE_DataChannel_t.IED_T8, IEdk.IEE_DataChannel_t.IED_AF4};
     String[] Name_Channel = {"AF3","T7","Pz","T8","AF4"};
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recording);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //list view will go here


        mButton = (Button) findViewById(R.id.tempButt);
        mButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d(TAG, "Confirm button OnClickListener");
                Intent i = new Intent(NewRecordingActivity.this, GraphActivity.class);
                startActivity(i);
                /**Log.e("FFTSample","Start Write File");
                setDataFile();
                isEnableWriteFile = true;**/

            }
        });

        mChannelList = (ListView) findViewById(R.id.channelList);
        String[] channels = new String[]{"AF3", "F7", "F3",
                "FC5", "T7", "P7", "O1", "O2", "P8",
                "T8", "FC6", "F4", "F8", "AF4"};

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

    }


}
