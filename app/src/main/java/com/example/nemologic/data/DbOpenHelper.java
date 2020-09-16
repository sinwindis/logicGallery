package com.example.nemologic.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.util.Locale;

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
            db.execSQL(LevelDB.CreateDB._CREATE);
            db.execSQL(CategoryDB.CreateDB._CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

            //savedata를 보존하면서 db를 업데이트하는 기능을 구현해야 함
            db.execSQL("DROP TABLE IF EXISTS "+ LevelDB.CreateDB._TABLENAME);
            db.execSQL("DROP TABLE IF EXISTS "+ CategoryDB.CreateDB._TABLENAME);
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
        values.put(LevelDB.CreateDB.NAME, name);
        values.put(LevelDB.CreateDB.CATEGORY, category);
        values.put(LevelDB.CreateDB.WIDTH, width);
        values.put(LevelDB.CreateDB.HEIGHT, height);
        values.put(LevelDB.CreateDB.PROGRESS, 0);
        values.put(LevelDB.CreateDB.DATASET, dataSet);

        return mDB.insert(LevelDB.CreateDB._TABLENAME, null, values);
    }

    public long insertCategory(String name){
        ContentValues values = new ContentValues();
        values.put(CategoryDB.CreateDB.NAME, name);

        return mDB.insert(CategoryDB.CreateDB._TABLENAME, null, values);
    }

    public Cursor getCategoryCursor(){
        return mDB.query(LevelDB.CreateDB._TABLENAME, null, null, null, null, null, null);
    }

    public Cursor getLevelCursor(){
        return mDB.query(LevelDB.CreateDB._TABLENAME, null, null, null, null, null, null);
    }

    public Cursor getLevelCursorByCategory(String category){
        Cursor c = mDB.rawQuery( "SELECT * FROM " + LevelDB.CreateDB._TABLENAME + " WHERE " + LevelDB.CreateDB.CATEGORY + " = '" + category + "';", null);

        return c;
    }

}