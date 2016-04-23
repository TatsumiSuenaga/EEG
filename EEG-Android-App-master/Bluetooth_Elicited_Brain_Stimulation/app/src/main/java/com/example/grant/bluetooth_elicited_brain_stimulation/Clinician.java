package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Chris on 2/23/16.
 */

public class Clinician implements Parcelable{

    private int mID;
    private String mFirstName;
    private String mLastName;
    private String mAddress;
    private String mEmail;
    private String mPassword;

    public Clinician() {
        super();
    }

    public Clinician(String firstName, String lastName, String email, String password) {
        super();
        mFirstName = firstName;
        mLastName = lastName;
        mEmail = email;
        mPassword = password;
    }

    private Clinician(Parcel in){
        super();
        mID = in.readInt();
        mFirstName = in.readString();
        mLastName = in.readString();
        mEmail = in.readString();
        mPassword = in.readString();
    }

    public int getID() { return mID; }

    public void setID(int id) { mID = id; }

    public String getFirstName() { return mFirstName; }

    public void setFirstName(String firstName) { mFirstName = firstName; }

    public String getLastName() { return mLastName; }

    public void setLastName(String lastName) { mLastName = lastName; }

    public String getEmail() { return mEmail; }

    public void setEmail(String email) { mEmail = email; }

    public String getPassword() { return mPassword; }

    public void setPassword(String password) { mPassword = password; }

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
        parcel.writeString(getEmail());
        parcel.writeString(getPassword());
    }

    public static final Creator<Clinician> CREATOR = new Creator<Clinician>() {
        public Clinician createFromParcel(Parcel in) {
            return new Clinician(in);
        }

        public Clinician[] newArray(int size) {
            return new Clinician[size];
        }
    };
}