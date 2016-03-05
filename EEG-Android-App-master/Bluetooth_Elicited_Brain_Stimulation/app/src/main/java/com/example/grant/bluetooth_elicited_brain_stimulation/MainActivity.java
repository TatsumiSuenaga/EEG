package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button profileButton = (Button) findViewById(R.id.profile_button);
        Button recordsButton = (Button) findViewById(R.id.records_button);
        Button newRecordingButton = (Button) findViewById(R.id.new_recording_button);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if your activity is called "ProfileActivity", use the following line
//                Intent i = new Intent(this, ProfileActivity.class);
//                startActivity(i);
                Toast.makeText(MainActivity.this, "You clicked the profile button!", Toast.LENGTH_SHORT).show();
            }
        });

        recordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if your activity is called "RecordsActivity", use the following line
//                Intent i = new Intent(this, ProfileActivity.class);
//                startActivity(i);
                Intent i = new Intent(MainActivity.this, RecordsActivity.class);
                startActivity(i);
                //Toast.makeText(MainActivity.this, "You clicked the records button!", Toast.LENGTH_SHORT).show();
            }
        });

        newRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if your activity is called "NewRecordingActivity", use the following line
//                Intent i = new Intent(this, ProfileActivity.class);
//                startActivity(i);
                Toast.makeText(MainActivity.this, "You clicked the new recording button!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
