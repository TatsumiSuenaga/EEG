package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;

/**
 * Created by Chris on 4/12/2016.
 */
public class RegisterActivity extends AppCompatActivity{

    EditText mFirst;
    EditText mLast;
    EditText mEmail;
    EditText mPassword;
    ClinicianDAO clinicianDAO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        TextView loginScreen = (TextView) findViewById(R.id.link_to_login);

        Button create = (Button)findViewById(R.id.btnRegister);

        mFirst = (EditText)findViewById(R.id.reg_firstname);

        mLast = (EditText)findViewById(R.id.reg_lastname);

        mEmail = (EditText)findViewById(R.id.reg_email);

        mPassword = (EditText)findViewById(R.id.reg_password);

        clinicianDAO = new ClinicianDAO(getApplicationContext());

        loginScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                finish();
            }
        });

        create.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view){

                        String first = mFirst.getText().toString();
                        String last = mLast.getText().toString();
                        String email = mEmail.getText().toString();
                        String password = mPassword.getText().toString();

                        Clinician clinician = new Clinician(first, last, email, password);
                        clinician.setID(clinicianDAO.maxID() + 1);

                        if(clinicianDAO.createClinician(clinician) == -1){
                            Toast toast = Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else{
                            Toast toast = Toast.makeText(getApplicationContext(), "Account registered", Toast.LENGTH_SHORT);
                            toast.show();
                            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(i);
                        }

                    }
                }
        );
    }
}