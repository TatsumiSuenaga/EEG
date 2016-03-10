package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileListActivityFragment extends Fragment {
    private ArrayList<Patient> mPatients;
    private RecyclerView mProfileRecyclerView;
    private ProfileAdapter mProfileAdapter;
    private PatientDAO patientDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_list, container, false);

        mProfileRecyclerView = (RecyclerView) view
                .findViewById(R.id.profile_recycler_view);
        mProfileRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        patientDAO = new PatientDAO((getActivity()));
        updateUI();
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        return view;
    }

    private void updateUI() {
        List<Patient> patients = patientDAO.getPatients();
        mProfileAdapter = new ProfileAdapter(patients);
        mProfileRecyclerView.setAdapter(mProfileAdapter);
    }

    private class ProfileHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Patient mPatient;
        public TextView mPatientFirstName;
        public TextView mPatientLastName;
        private CardView mCardView;

        public ProfileHolder(View itemView) {
            super(itemView);

            mPatientFirstName = (TextView) itemView.findViewById(R.id.firstName);
            mPatientLastName = (TextView) itemView.findViewById(R.id.lastName);

            mCardView = (CardView) itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = ProfileActivity.newIntent(getActivity(), mPatient.getID());
            startActivity(intent);
        }

        public void bindPatient(Patient patient) {
            mPatient = patient;
            mPatientFirstName.setText(mPatient.getFirstName());
            mPatientLastName.setText(mPatient.getLastName());
        }
    }
    private class ProfileAdapter extends RecyclerView.Adapter<ProfileHolder> {

        private List<Patient> mPatients;

        //This crimes stuff is from BigNerdRanch, prob change it to something appropriate
        public ProfileAdapter(List<Patient> crimes) {
            mPatients = crimes;
        }
        @Override
        public ProfileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.row_profile_list, parent, false);
            return new ProfileHolder(view);
        }

        @Override
        public void onBindViewHolder(ProfileHolder holder, int position) {
            Patient patient = mPatients.get(position);
            holder.bindPatient(patient);
        }

        @Override
        public int getItemCount() {
            return mPatients.size();
        }
    }
}