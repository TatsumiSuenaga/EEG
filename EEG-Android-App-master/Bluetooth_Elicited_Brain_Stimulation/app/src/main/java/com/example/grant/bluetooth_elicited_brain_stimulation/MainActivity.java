package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Change these to listview, maybe
        Button profileButton = (Button) findViewById(R.id.profile_button);
        Button recordsButton = (Button) findViewById(R.id.records_button);
        Button newRecordingButton = (Button) findViewById(R.id.new_recording_button);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ProfileListActivity.class);
                startActivity(i);
            }
        });

        recordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Tag", "Wow, shits broke");
                Intent i = new Intent(MainActivity.this, RecordsActivity.class);
                startActivity(i);
            }
        });

        newRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, NewRecordingActivity.class);
                startActivity(i);
            }
        });
    }
}
