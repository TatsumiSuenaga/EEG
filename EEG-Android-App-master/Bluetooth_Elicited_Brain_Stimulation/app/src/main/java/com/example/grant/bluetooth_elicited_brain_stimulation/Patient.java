package com.example.grant.bluetooth_elicited_brain_stimulation;

import java.util.List;
import java.util.UUID;

/**
 * Created by ryan on 2/23/16.
 */
public class Patient {
    private UUID mID;
    private List<Reading> mReadings;

    private String mFirstName;
    private String mLastName;

    public Patient(String firstName, String lastName) {
        mFirstName = firstName;
        mLastName = lastName;
        mID = UUID.randomUUID();
    }


    public List<Reading> getReadings() {
        return mReadings;
    }

    public void addReadings(Reading reading) {
        mReadings.add(reading);
    }

    public UUID getmID() {
        return mID;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }
}
