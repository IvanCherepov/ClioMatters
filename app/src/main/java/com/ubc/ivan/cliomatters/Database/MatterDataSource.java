package com.ubc.ivan.cliomatters.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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
}
