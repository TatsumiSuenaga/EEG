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
public class LoginActivity extends AppCompatActivity {

    EditText mEmail;
    EditText mPassword;
    Button mLogin;
    ClinicianDAO clinicianDAO;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView registerScreen = (TextView) findViewById(R.id.link_to_register);

        mEmail = (EditText) findViewById(R.id.log_email);

        mPassword = (EditText) findViewById(R.id.log_password);

        mLogin = (Button) findViewById(R.id.btnLogin);

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
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                        }
                        else{
                            Toast toast = Toast.makeText(getApplicationContext(), "Password or email incorrect", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                    }
                }
        );

    }
}
