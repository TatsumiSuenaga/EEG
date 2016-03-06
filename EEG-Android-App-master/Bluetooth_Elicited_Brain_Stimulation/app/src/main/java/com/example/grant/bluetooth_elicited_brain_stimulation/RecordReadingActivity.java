package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Tatsumi on 3/5/16
 */

public class RecordReadingActivity extends AppCompatActivity {

    private TextView mIdText;


    private static final String EXTRA_RECORD_READING =
            "com.example.grant.bluetooth_elicited_brain_stimulation.record_reading";

    public static Intent newIntent (Context packageContext, Recording r){
        Intent i = new Intent(packageContext, RecordReadingActivity.class);
        i.putExtra(EXTRA_RECORD_READING, r);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_reading);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        Recording r = i.getParcelableExtra(EXTRA_RECORD_READING);
        mIdText = (TextView) findViewById(R.id.idTitle);
        mIdText.setText("Patient Name: " + r.getPatient() + "\nPatient ID: " + r.getID() + "\nRecording Date: " + r.getDate()
                + "\nMuscle: " + r.getMuscle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
