package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ryan on 2/23/16.
 */

public class Patient implements Parcelable{

    private int mID;
    private String mFirstName;
    private String mLastName;

    public Patient() {
        super();
    }

    public Patient(String firstName, String lastName) {
        super();
        mFirstName = firstName;
        mLastName = lastName;
    }

    private Patient(Parcel in){
        super();
        mID = in.readInt();
        mFirstName = in.readString();
        mLastName = in.readString();
    }

    public int getID() { return mID; }

    public void setID(int id) { mID = id; }

    public String getFirstName() { return mFirstName; }

    public void setFirstName(String firstName) { mFirstName = firstName; }

    public String getLastName() { return mLastName; }

    public void setLastName(String lastName) { mLastName = lastName; }

    @Override
    public String toString(){
        return mID + " " + mLastName;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(getID());
        parcel.writeString(getFirstName());
        parcel.writeString(getLastName());
    }

    public static final Parcelable.Creator<Patient> CREATOR = new Parcelable.Creator<Patient>() {
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };
}
