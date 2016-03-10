package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class NewRecordingActivity extends AppCompatActivity {

    private static final String TAG = NewRecordingActivity.class.getSimpleName();
    private Button mButton;
    private ListView mChannelList;

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



    }

}
