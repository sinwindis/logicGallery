package com.example.nemologic.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
            db.execSQL(SqlManager.LevelDBSql._CREATE);
            db.execSQL(SqlManager.CategoryDBSql._CREATE);
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

    public long insertLevel(String name, String category, int width, int height, String dataSet, String colorSet){
        ContentValues values = new ContentValues();
        values.put(SqlManager.LevelDBSql.NAME, name);
        values.put(SqlManager.LevelDBSql.CATEGORY, category);
        values.put(SqlManager.LevelDBSql.WIDTH, width);
        values.put(SqlManager.LevelDBSql.HEIGHT, height);
        values.put(SqlManager.LevelDBSql.PROGRESS, 0);
        values.put(SqlManager.LevelDBSql.DATASET, dataSet);
        values.put(SqlManager.LevelDBSql.COLORSET, colorSet);

        return mDB.insert("level", null, values);
    }

    public int updateLevel(int id, int progress, String saveData){
        ContentValues values = new ContentValues();

        values.put(SqlManager.LevelDBSql.SAVEDATA, saveData);
        values.put(SqlManager.LevelDBSql.PROGRESS, progress);

        return mDB.update("level", values, SqlManager.LevelDBSql.ID + "=?", new String[]{String.valueOf(id)});
    }

    public long insertCategory(String name){
        ContentValues values = new ContentValues();
        values.put(SqlManager.CategoryDBSql.NAME, name);

        return mDB.insert(SqlManager.CategoryDBSql._TABLENAME, null, values);
    }

    public Cursor getCategoryCursor(){
        return mDB.query(SqlManager.CategoryDBSql._TABLENAME, null, null, null, null, null, null);
    }


    public Cursor getLevelCursorByCategory(String category){
        Cursor c = mDB.rawQuery( "SELECT * FROM " + SqlManager.LevelDBSql._TABLENAME + " WHERE " + SqlManager.LevelDBSql.CATEGORY + "='" + category + "';", null);

        return c;
    }

    public Cursor getLevelCursorById(int id){

        Cursor c = mDB.rawQuery( "SELECT * FROM " + SqlManager.LevelDBSql._TABLENAME + " WHERE " + SqlManager.LevelDBSql.ID + "='" + id + "';", null);

        return c;
    }

}