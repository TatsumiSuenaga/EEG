package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("LEGAL STUFF");

        // set dialog message
        alertDialogBuilder
                .setMessage("Do you accept the terms of this app?")
                .setCancelable(false)
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(i);
                        Toast toast = Toast.makeText(getApplicationContext(), "Account not registered", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                })
                .setPositiveButton("YES",new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog,int id) {
                        String first = mFirst.getText().toString();
                        String last = mLast.getText().toString();
                        String email = mEmail.getText().toString();
                        email = email.toLowerCase();
                        String password = mPassword.getText().toString();

                        boolean validated = Validate(password, email);

                        Clinician clinician = new Clinician(first, last, email, password);
                        clinician.setID(clinicianDAO.maxID() + 1);

                        if(!validated){
                            Toast toast = Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else if (clinicianDAO.createClinician(clinician) != -1){
                            Toast toast = Toast.makeText(getApplicationContext(), "Account registered", Toast.LENGTH_SHORT);
                            toast.show();
                            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else{
                            Toast toast = Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        loginScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });

        create.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view){
                        // show it
                        alertDialog.show();
                    }
                }
        );
    }

    private boolean Validate(String password, String email){
        boolean isValid = true;

        ClinicianDAO clinicianDAO = new ClinicianDAO(getApplicationContext());

        Clinician test = clinicianDAO.getClinician(email);

        if(password.length() < 4 || password.isEmpty()){
            isValid = false;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty() || test.getID() != -1){
            isValid = false;
        }

        return isValid;
    }
}
