package com.example.grant.bluetooth_elicited_brain_stimulation;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.TextView;

public class ProfileActivity extends SingleFragmentActivity {

    public static final String EXTRA_PATIENT_ID =
            "com.example.grant.bluetooth_elicited_brain_stimulation.patient_id";

    public static Intent newIntent(Context packageContext, int crimeId) {
        Intent intent = new Intent(packageContext, ProfileActivity.class);
        intent.putExtra(EXTRA_PATIENT_ID, crimeId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        int patientId = (int) getIntent().getSerializableExtra(EXTRA_PATIENT_ID);

        return ProfileActivityFragment.newInstance(patientId);
    }

}
