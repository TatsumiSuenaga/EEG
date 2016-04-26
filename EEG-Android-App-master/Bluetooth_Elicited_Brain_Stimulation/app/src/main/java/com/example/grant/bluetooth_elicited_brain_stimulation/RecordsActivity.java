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
import java.util.Calendar;
import java.util.Date;

public class RecordsActivity extends AppCompatActivity {

    private ArrayList<Recording> recordingsList;
    private final String SAMP_PREFS_NAME = "SampPrefsFile";
    private final String SAMP_REC = "recording";
    private final String PREFS_NAME = "MyPrefsFile";
    private final static String OPT_EMAIL="email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        ListView recordingsLV = (ListView) findViewById(R.id.listViewRecords);

        RecordingDAO recordingDAO = new RecordingDAO(this);

        recordingsList = recordingDAO.getRecordings();

        //this is just sample stuff to make demoing easy. You can delete this.d
        SharedPreferences samples = getSharedPreferences(SAMP_PREFS_NAME, 0);
        boolean create = samples.getBoolean(SAMP_REC, false);

        if(!create){
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String email = settings.getString(OPT_EMAIL, "email");
            ClinicianDAO clinicianDAO = new ClinicianDAO(getApplicationContext());
            Clinician clinician = clinicianDAO.getClinician(email);
            PatientDAO patientDAO = new PatientDAO(getApplicationContext());

            Calendar c = Calendar.getInstance();
            int seconds = c.get(Calendar.SECOND);

            Recording record = new Recording();
            record.setID(1);
            record.setDate(seconds);
            Patient sample = new Patient();
            sample.setClinician(clinician.getID());
            sample.setAddress("141 E. Canada Ave.");
            sample.setEthnicity("Caucasian");
            sample.setFirstName("Joey");
            sample.setLastName("Votto");
            sample.setEmail("vottomatic@YourCincinnatiReds.com");
            sample.setID(patientDAO.maxID() + 1);
            patientDAO.createPatient(sample, clinician.getID());
            record.setPatient(sample);
            recordingDAO.createRecording(record);
            samples.edit().putBoolean(SAMP_REC, true).apply();
        }

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
