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
    private String mAddress;
    private String mEmail;
    private String mEthnicity;

    public Patient() {
        super();
    }

    public Patient(String firstName, String lastName, String address, String email, String ethnicity) {
        super();
        mFirstName = firstName;
        mLastName = lastName;
        mAddress = address;
        mEmail = email;
        mEthnicity = ethnicity;
    }

    private Patient(Parcel in){
        super();
        mID = in.readInt();
        mFirstName = in.readString();
        mLastName = in.readString();
        mAddress = in.readString();
        mEmail = in.readString();
        mEthnicity = in.readString();
    }

    public int getID() { return mID; }

    public void setID(int id) { mID = id; }

    public String getFirstName() { return mFirstName; }

    public void setFirstName(String firstName) { mFirstName = firstName; }

    public String getLastName() { return mLastName; }

    public void setLastName(String lastName) { mLastName = lastName; }

    public String getAddress() { return mAddress; }

    public void setAddress(String address) { mAddress = address; }

    public String getEmail() { return mEmail; }

    public void setEmail(String email) { mEmail = email; }

    public String getEthnicity() { return mEthnicity;}

    public void setEthnicity(String ethnicity) { mEthnicity = ethnicity;}

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
        parcel.writeString(getAddress());
        parcel.writeString(getEmail());
        parcel.writeString(getEthnicity());
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