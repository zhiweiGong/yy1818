package com.android.tools.SysPorp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBaseHelper extends SQLiteOpenHelper {
    private final String TAG = "DBaseHelper";

    private String mDBaseTable = "";

    public DBaseHelper(Context context, String absPath) {
        super(context, absPath, null, 1);//DATABASE_VERSION
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,"onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            db.execSQL("DROP TABLE IF EXISTS " + mDBaseTable);
        }catch(SQLException se){
            se.printStackTrace();
        }

        Log.d(TAG,"onUpgrade");

        onCreate(db);
    }

    public void CreateTable(String table){
        SQLiteDatabase db = getWritableDatabase();

        mDBaseTable = table;
        try{
            db.execSQL("create table if not exists " + mDBaseTable + " (tag_name varchar primary key,tag_value varchar);");
        }catch(SQLException se){
            se.printStackTrace();
        }
    }

    public String query(String name) {
        String strRet = null;
        Cursor cursor = null;

        SQLiteDatabase db = getReadableDatabase();

        try{
            cursor = db.query(mDBaseTable, new String[] { "tag_name", "tag_value" }, "tag_name==?", new String[] { name }, null, null, null);
        }catch(SQLException se){
            se.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor != null){
                try{
                    cursor.moveToFirst();
                    strRet = cursor.getString(cursor.getColumnIndex("tag_value"));
                    cursor.close();
                }catch(Exception e){
                    System.out.println("input have error!");
                }
            }
        }

        return strRet;
    }

    public boolean delete(String name) {
        boolean ret = false;

        SQLiteDatabase db = getWritableDatabase();

        String where = "tag_name = ?";
        String[] whereValue = { name };

        try{
            db.delete(mDBaseTable, where, whereValue);
            ret = true;
        }catch(SQLException se){
            se.printStackTrace();
        }

        return ret;
    }

    public boolean update(String name,String value) {
        boolean ret = false;

        SQLiteDatabase db = getWritableDatabase();

        String where = "tag_name = ?";
        String[] whereValue = { name };
        ContentValues cv = new ContentValues();
        cv.put(name, value);

        try{
            db.update(mDBaseTable, cv, where, whereValue);
            ret = true;
        }catch(SQLException se){
            se.printStackTrace();
        }

        return ret;
    }

    public boolean insert(String name,String value) {
        boolean ret = false;

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("tag_name", name);
        cv.put("tag_value", value);

        try{
            db.insert(mDBaseTable, null, cv);
            ret = true;
        }catch(SQLException se){
            se.printStackTrace();
        }

        return ret;
    }
}
    
 
