package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class NewRecordingActivity extends AppCompatActivity {

    private static final String TAG = NewRecordingActivity.class.getSimpleName();
    private Button mButton;

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
                Log.d(TAG, "Broken");
                Intent i = new Intent(NewRecordingActivity.this, GraphActivity.class);
                startActivity(i);
            }
        });



    }

}
