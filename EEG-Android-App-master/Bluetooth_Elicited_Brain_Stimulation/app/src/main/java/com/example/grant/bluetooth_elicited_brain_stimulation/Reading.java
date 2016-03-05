package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.UUID;
import java.util.Date;

/**
 * Created by ryan on 2/23/16.
 */
public class Reading implements Parcelable {
    //private UUID mID;
    private String mID;
    private Graph mGraph;
    private Date recording_date;
    private File mFile;
    private String mMuscle; // this might become a different type of object!
    private int mData; //for parcelable

    public Reading(File file) {
        mFile = file;
        mID = UUID.randomUUID().toString();//change
    }

    /** For Parcelable implementation, describeContents(), writeToParcel(),
     * functions were created.
     *
     **/
    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags){
        out.writeString(mID);
    }

    public static final Parcelable.Creator<Reading> CREATOR = new Parcelable.Creator<Reading>(){
        public Reading createFromParcel(Parcel in){
            return new Reading(in);
        }
        public Reading[] newArray(int size){
            return new Reading[size];
        }
    };

    private Reading (Parcel in){
        mID = in.readString();
    }

    public Reading(){
        mID = UUID.randomUUID().toString();
    }

    public Date getDate() { return recording_date; }

    public void setDate(Date date) { recording_date = date; }

    public String getID() {//UUID
        return mID;
    }

    public Graph getGraph() {
        return mGraph;
    }

    public void setGraph(Graph graph) {
        mGraph = graph;
    }

    public File getFile() {
        return mFile;
    }

    public String getMuscle() {
        return mMuscle;
    }

    public void setMuscle(String muscle) {
        mMuscle = muscle;
    }

    @Override
    public String toString(){
        return this.mID + " " + this.getDate();
    }
}
