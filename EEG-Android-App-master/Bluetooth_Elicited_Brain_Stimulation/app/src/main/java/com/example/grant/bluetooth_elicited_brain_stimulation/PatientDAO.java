package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.content.ContentValues;
import android.content.Context;
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

    public long createPatient(Patient patient) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PATIENT_FIRSTNAME, patient.getFirstName());
        values.put(DatabaseHelper.COLUMN_PATIENT_LASTNAME, patient.getLastName());

        return database.insert(DatabaseHelper.TABLE_PATIENTS, null, values);
    }

    public long updatePatient(Patient patient) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PATIENT_FIRSTNAME, patient.getFirstName());
        values.put(DatabaseHelper.COLUMN_PATIENT_LASTNAME, patient.getLastName());

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

    public Patient getPatient(int ID){
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_PATIENTS + " WHERE "
                + DatabaseHelper.COLUMN_PATIENT_ID + " = " + ID + ";";

        Cursor c = database.rawQuery(selectQuery, null);
        c.moveToFirst();
        Patient patient = new Patient();
        patient.setID(c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_PATIENT_ID)));
        patient.setFirstName(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_PATIENT_FIRSTNAME)));
        patient.setLastName(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_PATIENT_LASTNAME)));

        return patient;
    }

    public ArrayList<Patient> getPatients() {
        ArrayList<Patient> patients = new ArrayList<Patient>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_PATIENTS,
                new String[] { DatabaseHelper.COLUMN_PATIENT_ID,
                        DatabaseHelper.COLUMN_PATIENT_FIRSTNAME,
                        DatabaseHelper.COLUMN_PATIENT_LASTNAME }, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Patient patient = new Patient();
            patient.setID(cursor.getInt(0));
            patient.setFirstName(cursor.getString(1));
            patient.setLastName(cursor.getString(2));
            patients.add(patient);
        }
        return patients;
    }

    public void deleteAllPatients(){
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_PATIENTS);
        database.execSQL(DatabaseHelper.CREATE_TABLE_PATIENTS);
    }
}
