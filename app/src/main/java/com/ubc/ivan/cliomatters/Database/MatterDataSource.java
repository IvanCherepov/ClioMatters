package com.ubc.ivan.cliomatters.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ubc.ivan.cliomatters.Model.Matter;

import java.util.ArrayList;

/**
 * Created by ivan on 22/09/15.
 */
public class MatterDataSource {

    private Context mContext;
    private MatterSQLHelper mMatterSQLHelper;

    public MatterDataSource(Context context) {
        this.mContext = context;
        mMatterSQLHelper = new MatterSQLHelper(context);
        SQLiteDatabase database = mMatterSQLHelper.getReadableDatabase();
        database.close();
    }

    public SQLiteDatabase open() {
        return mMatterSQLHelper.getWritableDatabase();
    }

    public void close(SQLiteDatabase database) {
        database.close();
    }

    public void deleteMatter(int matterID) {
        SQLiteDatabase database = open();
        database.beginTransaction();

        database.delete(MatterSQLHelper.TABLE_MATTERS,
                String.format("%s=%s", MatterSQLHelper.COLUMN_MATTER_ID, String.valueOf(matterID)),
                null);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    public void updateMatter(Matter matter) {
        SQLiteDatabase database = open();
        database.beginTransaction();

        ContentValues updateMatterValues = new ContentValues();
        updateMatterValues.put(MatterSQLHelper.COLUMN_MATTER_ID, matter.getId());
        updateMatterValues.put(MatterSQLHelper.COLUMN_DISPLAY_NUMBER, matter.getDisplayName());
        updateMatterValues.put(MatterSQLHelper.COLUMN_CLIENT_NAME, matter.getClientName());
        updateMatterValues.put(MatterSQLHelper.COLUMN_DESCRIPTION, matter.getDescription());
        updateMatterValues.put(MatterSQLHelper.COLUMN_OPEN_DATE, matter.getOpenDate());
        updateMatterValues.put(MatterSQLHelper.COLUMN_OPEN_STATUS, matter.getStatus());
        updateMatterValues.put(MatterSQLHelper.COLUMN_BILLABLE, matter.getBillable());
        updateMatterValues.put(MatterSQLHelper.COLUMN_PRACTICE_AREA, matter.getPracticeArea());

        database.update(MatterSQLHelper.TABLE_MATTERS, updateMatterValues,
                String.format("%s=%d", MatterSQLHelper.COLUMN_MATTER_ID, matter.getId()), null);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    public ArrayList<Matter> readMatters() {
        SQLiteDatabase database = open();

        Cursor cursor = database.rawQuery("SELECT * FROM " + MatterSQLHelper.TABLE_MATTERS, null);

        ArrayList<Matter> matters = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Matter matter = new Matter(getIntFromColumnName(cursor, MatterSQLHelper.COLUMN_MATTER_ID),
                        getStringFromColumnName(cursor, MatterSQLHelper.COLUMN_DISPLAY_NUMBER),
                        getStringFromColumnName(cursor, MatterSQLHelper.COLUMN_CLIENT_NAME),
                        getStringFromColumnName(cursor, MatterSQLHelper.COLUMN_DESCRIPTION),
                        getStringFromColumnName(cursor, MatterSQLHelper.COLUMN_OPEN_DATE),
                        getStringFromColumnName(cursor, MatterSQLHelper.COLUMN_OPEN_STATUS),
                        Boolean.getBoolean(getStringFromColumnName(cursor,
                                MatterSQLHelper.COLUMN_BILLABLE)),
                        getStringFromColumnName(cursor, MatterSQLHelper.COLUMN_PRACTICE_AREA));
                matters.add(matter);

            } while (cursor.moveToNext());
        }

        cursor.close();
        close(database);
        return matters;
    }

    public Matter readMatter(int position) {
        Matter matter = null;
        SQLiteDatabase database = open();

        Cursor cursor = database.rawQuery("SELECT * FROM " +
                MatterSQLHelper.TABLE_MATTERS +
                " WHERE _id = " + Integer.toString(position + 1), null);

        if (cursor.moveToFirst()) {
            matter = new Matter(cursor.getInt(cursor.getColumnIndexOrThrow(MatterSQLHelper.COLUMN_MATTER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MatterSQLHelper.COLUMN_DISPLAY_NUMBER)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MatterSQLHelper.COLUMN_CLIENT_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MatterSQLHelper.COLUMN_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MatterSQLHelper.COLUMN_OPEN_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MatterSQLHelper.COLUMN_OPEN_STATUS)),
                    Boolean.getBoolean(cursor.getString(cursor.getColumnIndexOrThrow(MatterSQLHelper.COLUMN_BILLABLE))),
                    cursor.getString(cursor.getColumnIndexOrThrow(MatterSQLHelper.COLUMN_PRACTICE_AREA)));
        }
        cursor.close();
        close(database);
        return matter;
    }

    private int getIntFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getInt(columnIndex);
    }

    private String getStringFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getString(columnIndex);
    }

    public void createMatter(Matter matter, int counter) {
        SQLiteDatabase database = open();
        database.beginTransaction();

        ContentValues conventValues = new ContentValues();
        conventValues.put(MatterSQLHelper.COLUMN_ID, counter);
        conventValues.put(MatterSQLHelper.COLUMN_MATTER_ID, matter.getId());
        conventValues.put(MatterSQLHelper.COLUMN_DISPLAY_NUMBER, matter.getDisplayName());
        conventValues.put(MatterSQLHelper.COLUMN_CLIENT_NAME, matter.getClientName());
        conventValues.put(MatterSQLHelper.COLUMN_DESCRIPTION, matter.getDescription());
        conventValues.put(MatterSQLHelper.COLUMN_OPEN_DATE, matter.getOpenDate());
        conventValues.put(MatterSQLHelper.COLUMN_OPEN_STATUS, matter.getStatus());
        conventValues.put(MatterSQLHelper.COLUMN_BILLABLE, matter.getBillable());
        conventValues.put(MatterSQLHelper.COLUMN_PRACTICE_AREA, matter.getPracticeArea());

        database.insert(MatterSQLHelper.TABLE_MATTERS, null, conventValues);

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }
}
