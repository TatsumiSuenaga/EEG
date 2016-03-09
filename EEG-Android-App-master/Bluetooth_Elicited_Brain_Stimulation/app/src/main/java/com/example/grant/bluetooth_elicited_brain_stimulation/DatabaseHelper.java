package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

/**
 * Created by David on 3/3/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "EMG_Manager";

    public static final String TABLE_PATIENTS = "Patients";
    public static final String TABLE_RECORDINGS = "Recordings";

    // Patients Table column names
    public static final String COLUMN_PATIENT_ID = "id";
    public static final String COLUMN_PATIENT_FIRSTNAME = "first_name";
    public static final String COLUMN_PATIENT_LASTNAME = "last_name";
    public static final String COLUMN_PATIENT_ADDRESS = "address";
    public static final String COLUMN_PATIENT_EMAIL = "email";

    // Recordings Table column names
    public static final String COLUMN_RECORDING_ID = COLUMN_PATIENT_ID;
    public static final String COLUMN_RECORDING_DATE = "date";
    public static final String COLUMN_RECORDING_MUSCLE = "muscle";
    public static final String COLUMN_RECORDING_PATIENT_ID = "patient_id";

    public static final String CREATE_TABLE_PATIENTS = "CREATE TABLE " + TABLE_PATIENTS
            + "(" + COLUMN_PATIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PATIENT_FIRSTNAME + " TEXT NOT NULL,"
            + COLUMN_PATIENT_LASTNAME + " TEXT NOT NULL,"
            + COLUMN_PATIENT_ADDRESS + " TEXT NOT NULL,"
            + COLUMN_PATIENT_EMAIL + " TEXT NOT NULL" + ")";

    public static final String CREATE_TABLE_RECORDINGS = "CREATE TABLE " + TABLE_RECORDINGS
            + "(" + COLUMN_RECORDING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_RECORDING_DATE + " DATETIME NOT NULL,"
            + COLUMN_RECORDING_MUSCLE + " TEXT NOT NULL,"
            + COLUMN_RECORDING_PATIENT_ID + " INTEGER,"
            + " FOREIGN KEY (" + COLUMN_RECORDING_PATIENT_ID + ") REFERENCES " + TABLE_PATIENTS + "(" + COLUMN_PATIENT_ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getHelper(Context context) {
        if (instance == null)
            instance = new DatabaseHelper(context);
        return instance;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PATIENTS);
        db.execSQL(CREATE_TABLE_RECORDINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENTS);

        // create new tables
        onCreate(db);
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}