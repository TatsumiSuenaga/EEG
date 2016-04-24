package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Chris on 4/12/2016.
 */
public class PatientRegisterActivity extends AppCompatActivity{

    EditText mFirst;
    EditText mLast;
    EditText mEmail;
    EditText mAddress;
    PatientDAO patientDAO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_patient);

        TextView loginScreen = (TextView) findViewById(R.id.link_to_login);

        Button create = (Button)findViewById(R.id.btnRegister_patient);

        mFirst = (EditText)findViewById(R.id.reg_firstname_patient);

        mLast = (EditText)findViewById(R.id.reg_lastname_patient);

        mAddress = (EditText)findViewById(R.id.reg_address_patient);

        mEmail = (EditText)findViewById(R.id.reg_email_patient);

        patientDAO = new PatientDAO(getApplicationContext());

        create.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view){

                        String first = mFirst.getText().toString();
                        String last = mLast.getText().toString();
                        String address = mAddress.getText().toString();
                        String email = mEmail.getText().toString();

                        Patient patient = new Patient(first, last, email, address);
                        patient.setID(patientDAO.maxID() + 1);

                        if(patientDAO.createPatient(patient) == -1){
                            Toast toast = Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else{
                            Toast toast = Toast.makeText(getApplicationContext(), "Patient registered", Toast.LENGTH_SHORT);
                            toast.show();
                            Intent i = new Intent(PatientRegisterActivity.this, MainActivity.class);
                            startActivity(i);
                        }

                    }
                }
        );
    }
}
