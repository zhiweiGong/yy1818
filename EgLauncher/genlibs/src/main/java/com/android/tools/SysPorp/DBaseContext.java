package com.android.tools.SysPorp;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import java.io.File;

/**
 * 用于支持对存储在SD卡上的数据库的访问
**/
public class DBaseContext extends ContextWrapper {
    private final String TAG = "DBaseHelper";

    private String mDBase_AbsPath = "";

    public DBaseContext(Context base, String absPath){
        super(base);
        mDBase_AbsPath = absPath;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory, DatabaseErrorHandler errorHandler) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(new File(mDBase_AbsPath), null);

        Log.d(TAG,"openOrCreateDatabase");

        return result;
    }
}