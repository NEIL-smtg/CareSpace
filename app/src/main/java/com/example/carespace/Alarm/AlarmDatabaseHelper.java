package com.example.carespace.Alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class AlarmDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "Alarm.db";
    private static final int DATABASE_VERSION =1;

    private static final String TABLE_NAME = "my_alarm";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "alarm_title";
    private static final String COLUMN_TIME = "alarm_time";
    private static final String COLUMN_DESCRIPTION = "alarm_description";
    private static final String COLUMN_PENDING_ID = "pending_ID";


    public AlarmDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null , DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_TITLE + " TEXT, "+
                COLUMN_TIME + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_PENDING_ID + " INTEGER);";

        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    
    public void addAlarm(String title, String time, String desc, int pending_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_TIME, time);
        cv.put(COLUMN_DESCRIPTION, desc);
        cv.put(COLUMN_PENDING_ID, pending_id);
        long result  = db.insert(TABLE_NAME, null, cv);
        
        if (result == -1)
        {
            Toast.makeText(context, "Failed to save to local db", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, "Added succesfully", Toast.LENGTH_SHORT).show();
        }

    }

    public Cursor readAllData()
    {
        String query = "SELECT * FROM " +TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null)
        {
            cursor  = db.rawQuery(query,null);

        }

        return cursor;
    }

    public void deleteOneRow(String row_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});

        if (result == -1)
        {
            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, "Delete Successfully", Toast.LENGTH_SHORT).show();
        }
    }

}
