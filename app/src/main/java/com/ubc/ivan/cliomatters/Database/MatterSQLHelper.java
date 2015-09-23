package com.ubc.ivan.cliomatters.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ivan on 22/09/15.
 */
public class MatterSQLHelper extends SQLiteOpenHelper {

    public static final String TABLE_MATTERS = "MATTERS";
    //Matters Table functionality
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MATTER_ID = "matterId";
    public static final String COLUMN_DISPLAY_NUMBER = "displayNumber";
    public static final String COLUMN_CLIENT_NAME = "clientName";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_OPEN_DATE = "openDate";
    public static final String COLUMN_OPEN_STATUS = "status";
    private static final String DB_NAME = "clio.db";
    private static final int DB_VERSION = 1;
    private static final String TYPE_STRING = " TEXT,";
    private static final String TYPE_INT = " INTEGER,";
    private static final String DB_CREATE = "CREATE TABLE " + TABLE_MATTERS + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_MATTER_ID + TYPE_INT +
            COLUMN_DISPLAY_NUMBER + TYPE_STRING +
            COLUMN_CLIENT_NAME + TYPE_STRING +
            COLUMN_DESCRIPTION + TYPE_STRING +
            COLUMN_OPEN_DATE + TYPE_STRING +
            COLUMN_OPEN_STATUS + " TEXT)";

    private static final String DB_DELETE = "DROP TABLE IF EXISTS " + DB_NAME;


    public MatterSQLHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public MatterSQLHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_DELETE);
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DB_DELETE);
        onCreate(db);
    }
}
