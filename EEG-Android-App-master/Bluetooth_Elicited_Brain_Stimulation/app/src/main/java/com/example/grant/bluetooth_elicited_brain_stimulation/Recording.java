package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ryan on 2/23/16.
 */
public class Recording implements Parcelable{

    private int mID;
    private long mDate;
    private Patient mPatient;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Recording(){
        super();
    }

    public Recording(long date, Patient patient){
        mDate = date;
        mPatient = patient;
    }

    public Recording(Parcel in){
        super();
        mID = in.readInt();
        mDate = in.readLong();
        mPatient = in.readParcelable(Patient.class.getClassLoader());
    }

    public int getID() { return mID; }

    public void setID(int id) { mID = id; }

    public long getDate() { return mDate; }

    public void setDate(long date) { mDate = date; }

    public Patient getPatient() { return mPatient; }

    public void setPatient(Patient patient) { mPatient = patient; }

    @Override
    public String toString(){
        return mID + " " + mPatient.getFirstName() + " " + mPatient.getLastName() + " " + dateFormat.format(new Date(this.getDate()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(getID());
        parcel.writeLong(getDate());
        parcel.writeParcelable(getPatient(), flags);
    }

    public static final Parcelable.Creator<Recording> CREATOR = new Parcelable.Creator<Recording>() {
        public Recording createFromParcel(Parcel in) {
            return new Recording(in);
        }

        public Recording[] newArray(int size) {
            return new Recording[size];
        }
    };
}
