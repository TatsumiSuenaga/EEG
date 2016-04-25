package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Chris on 4/24/2016.
 */
public class ClinicianSettingsActivity extends AppCompatActivity {

    EditText mFName;
    EditText mLName;
    EditText mPassword;
    EditText mEAddress;
    Button save;
    Button delete;
    private final String PREFS_NAME = "MyPrefsFile";
    private final static String OPT_EMAIL="email";
    private final String DEF_VALUE = "noemail";
    ClinicianDAO clinicianDAO;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinician_settings);
        clinicianDAO = new ClinicianDAO(getApplicationContext());
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String email = settings.getString(OPT_EMAIL, DEF_VALUE);
        Clinician mClinician = clinicianDAO.getClinician(email);
        mFName = (EditText) findViewById(R.id.clinician_settings_fName);
        mLName = (EditText) findViewById(R.id.clinician_settings_lName);
        mPassword = (EditText) findViewById(R.id.clinician_settings_passwords);
        mEAddress = (EditText) findViewById(R.id.clinician_settings_email);
        mFName.setText(mClinician.getFirstName());
        mLName.setText(mClinician.getLastName());
        mPassword.setText(mClinician.getPassword());
        mEAddress.setText(mClinician.getEmail());
        save = (Button) findViewById(R.id.save_clinician_button);
        delete = (Button) findViewById(R.id.delete_clinician_button);
        save.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        String email = settings.getString(OPT_EMAIL, DEF_VALUE);
                        Clinician clinician = clinicianDAO.getClinician(email);
                        mFName = (EditText) findViewById(R.id.clinician_settings_fName);
                        mLName = (EditText) findViewById(R.id.clinician_settings_lName);
                        mEAddress = (EditText) findViewById(R.id.clinician_settings_email);
                        mPassword = (EditText) findViewById(R.id.clinician_settings_passwords);
                        clinician.setFirstName(mFName.getText().toString());
                        clinician.setLastName(mLName.getText().toString());
                        clinician.setEmail(mEAddress.getText().toString());
                        clinician.setPassword(mPassword.getText().toString());
                        clinicianDAO.updateClinician(clinician);
                        Intent i = new Intent(ClinicianSettingsActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                }
        );
        delete.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        String email = settings.getString(OPT_EMAIL, DEF_VALUE);
                        Clinician clinician = clinicianDAO.getClinician(email);
                        clinicianDAO.deleteClinician(clinician);
                        settings = getSharedPreferences(PREFS_NAME, 0);
                        settings.edit().putBoolean("first_time_login", true).apply();
                        Intent i = new Intent(ClinicianSettingsActivity.this, LoginActivity.class);
                        startActivity(i);
                    }
                }
        );
    }
}
