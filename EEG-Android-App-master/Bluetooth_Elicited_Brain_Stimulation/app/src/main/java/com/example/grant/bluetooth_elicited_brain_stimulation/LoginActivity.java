package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.Bind;

/**
 * Created by Chris on 4/12/2016.
 */
public class LoginActivity extends AppCompatActivity {

    private final String PREFS_NAME = "MyPrefsFile";
    private final static String OPT_EMAIL="email";
//    EditText mEmail;
//    EditText mPassword;
//    Button mLogin;
    ClinicianDAO clinicianDAO;

    @Bind(R.id.log_email) EditText mEmail;
    @Bind(R.id.log_password) EditText mPassword;
    @Bind(R.id.btnLogin) Button mLogin;
    @Bind(R.id.link_to_register) TextView registerScreen;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        DatabaseHelper db = new DatabaseHelper(this);
        db.getWritableDatabase();

        // Fetch shared preferences file
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        // Check first time login
        if (!settings.getBoolean("first_time_login", true)) {
            // Bring up the Gallery
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

//        TextView registerScreen = (TextView) findViewById(R.id.link_to_register);
//
//        mEmail = (EditText) findViewById(R.id.log_email);
//
//        mPassword = (EditText) findViewById(R.id.log_password);
//
//        mLogin = (Button) findViewById(R.id.btnLogin);

        registerScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });

        mLogin.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view){
                        clinicianDAO = new ClinicianDAO(getApplicationContext());
                        String email = mEmail.getText().toString();
                        email = email.toLowerCase();
                        String password = mPassword.getText().toString();
                        boolean can_login = false;

                        Clinician clinician = clinicianDAO.getClinician(email);

                        if(clinician.getID() == -1){
                            //email is incorrect
                            Toast toast = Toast.makeText(getApplicationContext(), "Password or email incorrect", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else if(clinician.getPassword().equals(password)){
                            can_login = true;
                        }

                        if(can_login){
                            // Get preferences file
                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

                            // Check if first time login value is available
                            if (settings.getBoolean("first_time_login", true)) {
                                // Update value to be false
                                settings.edit().putBoolean("first_time_login", false).apply();
                                settings.edit().putString(OPT_EMAIL, email).apply();
                            }
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else{
                            Toast toast = Toast.makeText(getApplicationContext(), "Password or email incorrect", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                    }
                }
        );

    }
    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }
}
