package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class RecordsActivity extends AppCompatActivity {

    private ArrayList<Recording> recordingsList;
    private final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        ListView recordingsLV = (ListView) findViewById(R.id.listViewRecords);

        RecordingDAO recordingDAO = new RecordingDAO(this);

        recordingsList = recordingDAO.getRecordings();

        ArrayAdapter<Recording> recordingAdapter = new ArrayAdapter<Recording>(RecordsActivity.this, android.R.layout.simple_list_item_1, recordingsList);
        recordingsLV.setAdapter(recordingAdapter);

        recordingsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                Recording r = recordingsList.get(position);
                Intent i = RecordReadingActivity.newIntent(RecordsActivity.this, r);
                startActivity(i);

            }
        });


    }
}
