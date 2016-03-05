package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Date;

import java.util.ArrayList;

public class RecordsActivity extends AppCompatActivity {

    private ArrayList<Reading> readingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        final Reading reading1 = new Reading();
        reading1.setDate(new Date(System.currentTimeMillis()));
        Reading reading2 = new Reading();
        reading2.setDate(new Date(System.currentTimeMillis()+1000000));

        ListView recordsList = (ListView)findViewById(R.id.listViewRecords);

        readingsList = new ArrayList<Reading>();
        readingsList.add(reading1);
        readingsList.add(reading2);

        final ArrayAdapter<Reading> readingAdapter = new ArrayAdapter<Reading>(RecordsActivity.this,android.R.layout.simple_list_item_1, readingsList);
        recordsList.setAdapter(readingAdapter);

        recordsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                Intent i = RecordReadingActivity.newIntent(RecordsActivity.this, reading1);
                startActivity(i);

            }
        });

    }

}
