package com.example.grant.bluetooth_elicited_brain_stimulation;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Chris on 4/16/2016.
 */
public class ClinicianDAO extends RecordingDBDAO {

    private static final String WHERE_ID_EQUALS = DatabaseHelper.COLUMN_CLINICIAN_ID + " =?";

    public ClinicianDAO(Context context) {
        super(context);
    }

    public long createClinician(Clinician clinician) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CLINICIAN_FIRSTNAME, clinician.getFirstName());
        values.put(DatabaseHelper.COLUMN_CLINICIAN_LASTNAME, clinician.getLastName());
        values.put(DatabaseHelper.COLUMN_CLINICIAN_EMAIL, clinician.getEmail());
        values.put(DatabaseHelper.COLUMN_CLINICIAN_PASSWORD, clinician.getPassword());

        return database.insert(DatabaseHelper.TABLE_CLINICIANS, null, values);
    }

    public long updateClinician(Clinician clinician) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CLINICIAN_FIRSTNAME, clinician.getFirstName());
        values.put(DatabaseHelper.COLUMN_CLINICIAN_LASTNAME, clinician.getLastName());
        values.put(DatabaseHelper.COLUMN_CLINICIAN_EMAIL, clinician.getEmail());
        values.put(DatabaseHelper.COLUMN_CLINICIAN_PASSWORD, clinician.getPassword());

        long result = database.update(DatabaseHelper.TABLE_CLINICIANS, values,
                WHERE_ID_EQUALS,
                new String[] { String.valueOf(clinician.getID()) });
        Log.d("Update Result:", "=" + result);
        return result;

    }

    public int deleteClinician(Clinician clinician) {
        return database.delete(DatabaseHelper.TABLE_CLINICIANS,
                WHERE_ID_EQUALS, new String[] { clinician.getID() + "" });
    }

    public Clinician getClinician(int ID){
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_CLINICIANS + " WHERE "
                + DatabaseHelper.COLUMN_CLINICIAN_ID + " = " + ID + ";";

        Cursor c = database.rawQuery(selectQuery, null);
        c.moveToFirst();
        Clinician clinician = new Clinician();

        clinician.setID(c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_CLINICIAN_ID)));
        clinician.setFirstName(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_CLINICIAN_FIRSTNAME)));
        clinician.setLastName(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_CLINICIAN_LASTNAME)));
        clinician.setEmail(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_CLINICIAN_EMAIL)));
        clinician.setPassword(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_CLINICIAN_PASSWORD)));

        c.close();
        return clinician;
    }

    public Clinician getClinician(String email){
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_CLINICIANS + " WHERE "
                + DatabaseHelper.COLUMN_CLINICIAN_EMAIL + " = '" + email + "';";

        Clinician clinician = new Clinician();
        Cursor c = database.rawQuery(selectQuery, null);

        if(c.moveToFirst()){

            clinician.setID(c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_CLINICIAN_ID)));
            clinician.setFirstName(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_CLINICIAN_FIRSTNAME)));
            clinician.setLastName(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_CLINICIAN_LASTNAME)));
            clinician.setEmail(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_CLINICIAN_EMAIL)));
            clinician.setPassword(c.getString(c.getColumnIndex(DatabaseHelper.COLUMN_CLINICIAN_PASSWORD)));
        }
        else{
            clinician.setID(-1);
        }


        c.close();
        return clinician;
    }

    public void deleteAllClinicians(){
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_CLINICIANS);
        database.execSQL(DatabaseHelper.CREATE_TABLE_CLINICIANS);
    }

    public int maxID(){
        String selectQuery = "SELECT MAX("+ DatabaseHelper.COLUMN_CLINICIAN_ID + ") FROM " + DatabaseHelper.TABLE_CLINICIANS + ";";

        Cursor c = database.rawQuery(selectQuery, null);

        if(c.moveToFirst())
        {
            return c.getInt(0);
        }

        else{
            return 0;
        }
    }
}
