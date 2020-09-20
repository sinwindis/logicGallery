package com.example.nemologic.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;

public class DbOpenHelper {

    private static final String DATABASE_NAME = "NemoLogicDB.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context ctx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            //모든 카테고리에 해당하는 레벨 테이블을 만들어야 하는데...
            ArrayList<String> categories = DataManager.getCategoriesFromXml(ctx);
            for(int i = 0; i < categories.size(); i++)
            {
                db.execSQL(SqlManager.CreateLevelDB.getCreateStr(categories.get(i)));
            }
            db.execSQL(SqlManager.CreateCategoryDB._CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

            //savedata를 보존하면서 db를 업데이트하는 기능을 구현해야 함
            //db.execSQL("DROP TABLE IF EXISTS "+ SqlManager.CreateLevelDB.getCreateStr());
            //db.execSQL("DROP TABLE IF EXISTS "+ CategoryDB.CreateDB._TABLENAME);
            onCreate(db);
        }
    }

    public DbOpenHelper(Context context){
        this.ctx = context;
    }

    public DbOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void create(){
        mDBHelper.onCreate(mDB);
    }

    public void close(){
        mDB.close();
    }

    public long insertLevel(String name, String category, int width, int height, String dataSet){
        ContentValues values = new ContentValues();
        values.put(SqlManager.CreateLevelDB.NAME, name);
        values.put(SqlManager.CreateLevelDB.WIDTH, width);
        values.put(SqlManager.CreateLevelDB.HEIGHT, height);
        values.put(SqlManager.CreateLevelDB.PROGRESS, 0);
        values.put(SqlManager.CreateLevelDB.DATASET, dataSet);

        return mDB.insert(category, null, values);
    }

    public int updateLevel(String name, String category, int progress, String saveData){
        ContentValues values = new ContentValues();

        values.put(SqlManager.CreateLevelDB.SAVEDATA, saveData);
        values.put(SqlManager.CreateLevelDB.PROGRESS, progress);

        return mDB.update(category, values, SqlManager.CreateLevelDB.NAME + "=?", new String[]{name});
    }

    public long insertCategory(String name){
        ContentValues values = new ContentValues();
        values.put(SqlManager.CreateCategoryDB.NAME, name);

        return mDB.insert(SqlManager.CreateCategoryDB._TABLENAME, null, values);
    }

    public Cursor getCategoryCursor(){
        return mDB.query(SqlManager.CreateCategoryDB._TABLENAME, null, null, null, null, null, null);
    }

    public Cursor getLevelCursorByCategory(String category){
        Cursor c = mDB.rawQuery( "SELECT * FROM " + category + ";", null);

        return c;
    }

    public Cursor getLevelCursorByCategoryAndName(String category, String name){

        Cursor c = mDB.rawQuery( "SELECT * FROM " + category + " WHERE name='" + name + "';", null);

        return c;
    }

}