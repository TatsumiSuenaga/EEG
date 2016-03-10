package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class RecordsActivity extends AppCompatActivity {

    private ArrayList<Recording> recordingsList;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar.setTitle("Records");
            setSupportActionBar(mToolbar);
        }

        ListView recordingsLV = (ListView)findViewById(R.id.listViewRecords);

        //Test with 1 patient, 2 records
        PatientDAO patientDAO = new PatientDAO(this);
        RecordingDAO recordingDAO = new RecordingDAO(this);

        Patient patient = new Patient("David", "Kerns","12223", "osu.edu");
        patientDAO.createPatient(patient);

        Patient test = patientDAO.getPatient(1);
        final Recording recording = new Recording(System.currentTimeMillis(),"Funny bone",test);
        final Recording recording2 = new Recording(System.currentTimeMillis()+1500000,"Not so funny bone",test);

        recordingDAO.createRecording(recording);
        recordingDAO.createRecording(recording2);

        recordingsList = recordingDAO.getRecordings();

        ArrayAdapter<Recording> recordingAdapter = new ArrayAdapter<Recording>(RecordsActivity.this,android.R.layout.simple_list_item_1, recordingsList);
        recordingsLV.setAdapter(recordingAdapter);

        recordingsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                Recording r = recordingsList.get(position);
                Intent i = RecordReadingActivity.newIntent(RecordsActivity.this, r);
                startActivity(i);

            }
        });

        patientDAO.close();

    }

}
