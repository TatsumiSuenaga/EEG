package com.example.grant.bluetooth_elicited_brain_stimulation;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by David on 3/4/2016.
 */
public class PatientDAO extends RecordingDBDAO {

    private static final String WHERE_ID_EQUALS = DatabaseHelper.COLUMN_PATIENT_ID + " =?";

    public PatientDAO(Context context) {
        super(context);
    }

    public long createPatient(Patient patient, int userId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PATIENT_FIRSTNAME, patient.getFirstName());
        values.put(DatabaseHelper.COLUMN_PATIENT_LASTNAME, patient.getLastName());
        values.put(DatabaseHelper.COLUMN_PATIENT_ADDRESS, patient.getAddress());
        values.put(DatabaseHelper.COLUMN_PATIENT_EMAIL, patient.getEmail());
        values.put(DatabaseHelper.COLUMN_PATIENT_ETHNICITY, patient.getEthnicity());
        values.put(DatabaseHelper.COLUMN_PATIENT_CLINICIAN_ID, userId);

        return database.insert(DatabaseHelper.TABLE_PATIENTS, null, values);
    }

    public long updatePatient(Patient patient) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PATIENT_FIRSTNAME, patient.getFirstName());
        values.put(DatabaseHelper.COLUMN_PATIENT_LASTNAME, patient.getLastName());
        values.put(DatabaseHelper.COLUMN_PATIENT_ADDRESS, patient.getAddress());
        values.put(DatabaseHelper.COLUMN_PATIENT_EMAIL, patient.getEmail());
        values.put(DatabaseHelper.COLUMN_PATIENT_ETHNICITY, patient.getEthnicity());

        long result = database.update(DatabaseHelper.TABLE_PATIENTS, values,
                WHERE_ID_EQUALS,
                new String[] { String.valueOf(patient.getID()) });
        Log.d("Update Result:", "=" + result);
        return result;

    }

    public int deletePatient(Patient patient) {
        return database.delete(DatabaseHelper.TABLE_PATIENTS,
                WHERE_ID_EQUALS, new String[] { patient.getID() + "" });
    }

    public Patient getPatient(int ID, int userId){
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_PATIENTS + " WHERE "
                + DatabaseHelper.COLUMN_PATIENT_ID + " = " + ID + ";";

        Patient patient = new Patient();
        Cursor c = database.rawQuery(selectQuery, null);
        if(c.moveToFirst()){
            patient.setClinician((c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_PATIENT_CLINICIAN_ID))));
            if(patient.getClinician() == userId){
                patient.setID(c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_PATIENT_ID)));
                patient.setFirstName(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_PATIENT_FIRSTNAME)));
                patient.setLastName(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_PATIENT_LASTNAME)));
                patient.setAddress(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_PATIENT_ADDRESS)));
                patient.setEmail(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_PATIENT_EMAIL)));
                patient.setEthnicity((c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_PATIENT_ETHNICITY))));
            }
            else{
                patient.setID(-1);
                patient.setClinician(-1);
            }
        }
        else{
            patient.setID(-1);
        }



        c.close();
        return patient;
    }

    public Patient getPatient(String email, int userId){
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_PATIENTS + " WHERE "
                + DatabaseHelper.COLUMN_PATIENT_ETHNICITY + " = '" + email + "';";

        Patient patient = new Patient();
        Cursor c = database.rawQuery(selectQuery, null);

        if(c.moveToFirst()){
            //check to see if patient belongs to logged-in user
            patient.setClinician((c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_PATIENT_CLINICIAN_ID))));
            if(patient.getClinician() == userId){
                patient.setID(c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_PATIENT_ID)));
                patient.setFirstName(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_PATIENT_FIRSTNAME)));
                patient.setLastName(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_PATIENT_LASTNAME)));
                patient.setAddress(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_PATIENT_ADDRESS)));
                patient.setEmail(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_PATIENT_EMAIL)));
                patient.setEthnicity(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_PATIENT_ETHNICITY)));
            }
            else{
                patient.setID(-1);
                patient.setClinician(-1);
            }
        }
        else{
            //returns empty patient with id of -1 if patient is not found
            patient.setID(-1);
        }

        c.close();
        return patient;
    }

            public ArrayList<Patient> getPatients(int userId) {

                ArrayList<Patient> patients = new ArrayList<Patient>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_PATIENTS,
                new String[] { DatabaseHelper.COLUMN_PATIENT_ID,
                        DatabaseHelper.COLUMN_PATIENT_FIRSTNAME,
                        DatabaseHelper.COLUMN_PATIENT_LASTNAME,
                        DatabaseHelper.COLUMN_PATIENT_ADDRESS,
                        DatabaseHelper.COLUMN_PATIENT_EMAIL,
                        DatabaseHelper.COLUMN_PATIENT_ETHNICITY,
                        DatabaseHelper.COLUMN_PATIENT_CLINICIAN_ID}, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Patient patient = new Patient();
            patient.setID(cursor.getInt(0));
            patient.setFirstName(cursor.getString(1));
            patient.setLastName(cursor.getString(2));
            patient.setAddress(cursor.getString(3));
            patient.setEmail(cursor.getString(4));
            patient.setEthnicity(cursor.getString(5));
            patient.setClinician(cursor.getInt(6));
            if(patient.getClinician() == userId){
                patients.add(patient);
            }

        }
        cursor.close();
        return patients;
    }

    public void deleteAllPatients(){
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_PATIENTS);
        database.execSQL(DatabaseHelper.CREATE_TABLE_PATIENTS);
    }

    public int maxID() {
        String selectQuery = "SELECT MAX(" + DatabaseHelper.COLUMN_PATIENT_ID + ") FROM " + DatabaseHelper.TABLE_PATIENTS + ";";

        Cursor c = database.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            return c.getInt(0);
        } else {
            return 0;
        }
    }
}
