package com.example.grant.bluetooth_elicited_brain_stimulation;


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
        int patientId = (int)getArguments().getSerializable(ARG_PROFILE_ID);
        patientDAO = new PatientDAO(getActivity());
        mPatient = patientDAO.getPatient((patientId));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ((EditText)v.findViewById(R.id.patient_fName)).setText(mPatient.getFirstName());
        ((EditText)v.findViewById(R.id.patient_lName)).setText(mPatient.getLastName());
        ((EditText)v.findViewById(R.id.location)).setText(mPatient.getAddress());
        ((EditText)v.findViewById(R.id.email)).setText(mPatient.getEmail());
        ((EditText)v.findViewById(R.id.ethnicity)).setText(mPatient.getEthnicity());
        return v;
    }

}
