package com.example.aust_fp_mci;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StudentDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "studentAttendance.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "students";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_STATUS = "status";

    private static final String TABLE_NAME_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_DATE + " TEXT NOT NULL, " +
                    COLUMN_STATUS + " INTEGER NOT NULL DEFAULT 0);";

    public StudentDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_NAME_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}