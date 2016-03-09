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
    PatientDAO patientDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        patientDAO = new PatientDAO(getApplicationContext());

        Patient patient1 = new Patient("David","Kerns","1600 Penn Ave", "kernsdavida@gmail.com");
        patient1.setID(1);
        Patient patient2 = new Patient("Tom", "Carlin","DisneyWorld","noreply@tomcarlin.com");
        patient2.setID(2);
        Patient patient3 = new Patient("Val", "Kilmer","Top Gun Lane","val.kilmer@batmanforever.com");
        patient3.setID(3);
        patientDAO.createPatient(patient1);
        patientDAO.createPatient(patient2);
        patientDAO.createPatient(patient3);

        //Change these to listview
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
                Intent i = new Intent(MainActivity.this, NewRecordingActivity.class);
                startActivity(i);
                //Toast.makeText(MainActivity.this, "You clicked the new recording button!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
