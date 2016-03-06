package com.example.grant.bluetooth_elicited_brain_stimulation;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by David on 3/4/2016.
 */
public class RecordingDBDAO {

    protected SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private Context mContext;

    public RecordingDBDAO(Context context) {
        this.mContext = context;
        dbHelper = DatabaseHelper.getHelper(mContext);
        open();
    }

    public void open() throws SQLException {
        if(dbHelper == null)
            dbHelper = DatabaseHelper.getHelper(mContext);
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.closeDB();
        database = null;
    }
}
