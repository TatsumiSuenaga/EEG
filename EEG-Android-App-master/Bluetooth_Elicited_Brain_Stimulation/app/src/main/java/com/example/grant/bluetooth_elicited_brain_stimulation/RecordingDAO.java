package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by David on 3/4/2016.
 */
public class RecordingDAO extends RecordingDBDAO {

    private static final String WHERE_ID_EQUALS = DatabaseHelper.COLUMN_RECORDING_ID + " =?";

    public RecordingDAO(Context context) {
        super(context);
    }

    public void createRecording(Recording recording){

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RECORDING_DATE, recording.getDate());
        values.put(DatabaseHelper.COLUMN_RECORDING_PATIENT_ID, recording.getPatient().getID());

        database.insert(DatabaseHelper.TABLE_RECORDINGS, null, values);
    }

    public ArrayList<Recording> getRecordings2(){
        ArrayList<Recording> recordings = new ArrayList<Recording>();

        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_RECORDINGS;

        Cursor c = database.rawQuery(selectQuery, null);
        if(c.moveToFirst()){
            do{
                Recording recording = new Recording();
                recording.setDate(c.getLong(c.getColumnIndex(DatabaseHelper.COLUMN_RECORDING_DATE)));
                recordings.add(recording);
            }while(c.moveToNext());
        }

        return recordings;
    }

    public ArrayList<Recording> getRecordings() {
        ArrayList<Recording> recordings = new ArrayList<Recording>();

        String query = "SELECT recordings.id, recordings.date,"
                + " patients.id, patients.first_name, patients.last_name FROM "
                + DatabaseHelper.TABLE_RECORDINGS + " recordings, "
                + DatabaseHelper.TABLE_PATIENTS + " patients WHERE recordings."
                + DatabaseHelper.COLUMN_RECORDING_PATIENT_ID + " = patients."
                + DatabaseHelper.COLUMN_PATIENT_ID;

        Log.d("query", query);
        Cursor cursor = database.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Recording recording = new Recording();
            recording.setID(cursor.getInt(0));
            recording.setDate(cursor.getLong(1));

            Patient patient = new Patient();
            patient.setID(cursor.getInt(2));
            patient.setFirstName(cursor.getString(3));
            patient.setLastName(cursor.getString(4));

            recording.setPatient(patient);

            recordings.add(recording);
        }
        return recordings;
    }

    public int deleteRecording(Recording recording) {
        return database.delete(DatabaseHelper.TABLE_RECORDINGS,
                WHERE_ID_EQUALS, new String[] { recording.getID() + "" });
    }

    public void deleteAllRecordings(){
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_RECORDINGS);
        database.execSQL(DatabaseHelper.CREATE_TABLE_RECORDINGS);
    }
}
