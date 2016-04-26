package com.example.grant.bluetooth_elicited_brain_stimulation;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileActivityFragment extends Fragment {
    private Patient mPatient;
    private static final String ARG_PROFILE_ID = "crime_id";
    private TextView patient_fName;
    private PatientDAO patientDAO;
    private Button save;
    private Button delete;
    int patientId;
    private final String PREFS_NAME = "MyPrefsFile";
    private final static String OPT_EMAIL="email";
    private final String DEF_VALUE = "noemail";
    private String clinicianEmail;
    private int clinicianId;


    public ProfileActivityFragment() {
    }

    public static ProfileActivityFragment newInstance(int crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROFILE_ID, crimeId);

        ProfileActivityFragment fragment = new ProfileActivityFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getContext();
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        clinicianEmail = settings.getString(OPT_EMAIL, DEF_VALUE);
        ClinicianDAO clinicianDAO = new ClinicianDAO(context);
        Clinician user = clinicianDAO.getClinician(clinicianEmail);
        clinicianId = user.getID();

        patientId = (int)getArguments().getSerializable(ARG_PROFILE_ID);
        patientDAO = new PatientDAO(getActivity());
        mPatient = patientDAO.getPatient((patientId), clinicianId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ((EditText)v.findViewById(R.id.patient_fName)).setText(mPatient.getFirstName());
        ((EditText)v.findViewById(R.id.patient_lName)).setText(mPatient.getLastName());
        ((EditText)v.findViewById(R.id.location)).setText(mPatient.getAddress());
        ((EditText)v.findViewById(R.id.email)).setText(mPatient.getEmail());
        ((EditText)v.findViewById(R.id.ethnicity)).setText(mPatient.getEthnicity());
        save = (Button)v.findViewById(R.id.save_patient_button);
        delete = (Button)v.findViewById(R.id.delete_patient_button);
        save.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        patientDAO = new PatientDAO(getActivity());
                        Patient patient = patientDAO.getPatient(patientId, clinicianId);
                        EditText mFirst = (EditText) v.findViewById(R.id.patient_fName);
                        EditText mLast = (EditText) v.findViewById(R.id.patient_lName);
                        EditText mAddress = (EditText) v.findViewById(R.id.location);
                        EditText mEmail = (EditText) v.findViewById(R.id.email);
                        EditText mEthnicity = (EditText) v.findViewById(R.id.ethnicity);
                        patient.setFirstName(mFirst.getText().toString());
                        patient.setLastName(mLast.getText().toString());
                        patient.setEmail(mEmail.getText().toString());
                        patient.setAddress(mAddress.getText().toString());
                        patient.setEthnicity(mEthnicity.getText().toString());
                        patientDAO.updatePatient(patient);
                        Intent i = new Intent(getActivity(), ProfileListActivity.class);
                        startActivity(i);
                    }
                }
        );
        delete.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        patientDAO = new PatientDAO(getActivity());
                        Patient patient = patientDAO.getPatient(patientId, clinicianId);
                        patientDAO.deletePatient(patient);
                        Intent i = new Intent(getActivity(), ProfileListActivity.class);
                        startActivity(i);
                    }
                }
        );
        return v;
    }



}
