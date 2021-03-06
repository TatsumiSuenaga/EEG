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
    private ListView mChannelList;
    boolean[] channelList = new boolean[14];
    boolean sampleData = true;
    Button mButton;

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
                if(checkChannelSelected())
                {
                    Intent i = new Intent(NewRecordingActivity.this, GraphActivity.class);
                    i.putExtra("channelList", channelList);
                    i.putExtra("sampleData",sampleData);
                    startActivity(i);
                }
                else {
                    Toast.makeText(NewRecordingActivity.this, "Please select at least one channel", Toast.LENGTH_SHORT).show();
                }
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(NewRecordingActivity.this,
                android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, channels );

        //Temporary adapter just for UI, this will be later made into different fragment probably
        mChannelList.setAdapter(adapter);
        mChannelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ch = (String) mChannelList.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        ch + " is turned on", Toast.LENGTH_SHORT).show();
                if (channelList[position]) {
                    channelList[position] = false;
                    //mChannelList.getChildAt(position).setBackgroundColor(Color.WHITE);
                } else {
                    channelList[position] = true;
                    //mChannelList.getChildAt(position).setBackgroundColor(Color.parseColor("#DEDEDE"));
                }
            }
        });
    }

    private boolean checkChannelSelected()
    {
        for(boolean i:channelList)
        {
            if(i) return true;
        }
        return false;
    }


}
