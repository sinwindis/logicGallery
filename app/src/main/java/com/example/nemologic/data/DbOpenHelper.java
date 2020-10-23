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
            db.execSQL(SqlManager.BigPuzzleDBSql._CREATE);
            db.execSQL(SqlManager.BigLevelDBSql._CREATE);
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

    public long insertLevel(String name, String category, int width, int height, byte[] dataSet, byte[] colorSet, int custom){


        ContentValues values = new ContentValues();
        values.put(SqlManager.LevelDBSql.NAME, name);
        values.put(SqlManager.LevelDBSql.CATEGORY, category);
        values.put(SqlManager.LevelDBSql.WIDTH, width);
        values.put(SqlManager.LevelDBSql.HEIGHT, height);
        values.put(SqlManager.LevelDBSql.PROGRESS, 0);
        values.put(SqlManager.LevelDBSql.DATASET, dataSet);
        values.put(SqlManager.LevelDBSql.COLORSET, colorSet);
        values.put(SqlManager.LevelDBSql.CUSTOM, custom);

        return mDB.insert(SqlManager.LevelDBSql._TABLENAME, null, values);
    }

    public long insertBigLevel(int p_id, int number, int width, int height, byte[] dataSet, byte[] colorSet){

        ContentValues values = new ContentValues();
        values.put(SqlManager.BigLevelDBSql.P_ID, p_id);
        values.put(SqlManager.BigLevelDBSql.NUMBER, number);
        values.put(SqlManager.BigLevelDBSql.WIDTH, width);
        values.put(SqlManager.BigLevelDBSql.HEIGHT, height);
        values.put(SqlManager.BigLevelDBSql.PROGRESS, 0);
        values.put(SqlManager.BigLevelDBSql.DATASET, dataSet);
        values.put(SqlManager.BigLevelDBSql.COLORSET, colorSet);

        return mDB.insert(SqlManager.BigLevelDBSql._TABLENAME, null, values);
    }

    public long insertBigPuzzle(String name, int width, int height, byte[] colorSet, int custom){

        ContentValues values = new ContentValues();
        values.put(SqlManager.BigPuzzleDBSql.NAME, name);
        values.put(SqlManager.BigPuzzleDBSql.WIDTH, width);
        values.put(SqlManager.BigPuzzleDBSql.HEIGHT, height);
        values.put(SqlManager.BigPuzzleDBSql.PROGRESS, 0);
        values.put(SqlManager.BigPuzzleDBSql.COLORSET, colorSet);
        values.put(SqlManager.BigPuzzleDBSql.CUSTOM, custom);

        return mDB.insert(SqlManager.BigPuzzleDBSql._TABLENAME, null, values);
    }

    public int updateLevel(int id, int progress, byte[] saveData){
        ContentValues values = new ContentValues();

        values.put(SqlManager.LevelDBSql.SAVEDATA, saveData);
        values.put(SqlManager.LevelDBSql.PROGRESS, progress);

        return mDB.update(SqlManager.LevelDBSql._TABLENAME, values, SqlManager.LevelDBSql.ID + "=?", new String[]{String.valueOf(id)});
    }

    public int updateBigLevel(int id, int progress, byte[] saveData){
        ContentValues values = new ContentValues();

        values.put(SqlManager.BigLevelDBSql.SAVEDATA, saveData);
        values.put(SqlManager.BigLevelDBSql.PROGRESS, progress);

        return mDB.update(SqlManager.BigLevelDBSql._TABLENAME, values, SqlManager.BigLevelDBSql.ID + "=?", new String[]{String.valueOf(id)});
    }

    public long insertCategory(String name){
        ContentValues values = new ContentValues();
        values.put(SqlManager.CategoryDBSql.NAME, name);
        values.put(SqlManager.CategoryDBSql.PROGRESS, 0);

        return mDB.insert(SqlManager.CategoryDBSql._TABLENAME, null, values);
    }

    public Cursor getCategoryCursor(){
        return mDB.query(SqlManager.CategoryDBSql._TABLENAME, null, null, null, null, null, null);
    }



    public Cursor getLevelCursorByCategory(String category){
        Cursor c = mDB.rawQuery( "SELECT * FROM " + SqlManager.LevelDBSql._TABLENAME + " WHERE " + SqlManager.LevelDBSql.CATEGORY + "='" + category + "';", null);

        return c;
    }

    public Cursor getBigPuzzleCursor(){
        Cursor c = mDB.rawQuery( "SELECT * FROM " + SqlManager.BigPuzzleDBSql._TABLENAME + ";", null);

        return c;
    }

    public Cursor getBigPuzzleCursorById(int id){
        Cursor c = mDB.rawQuery( "SELECT * FROM " + SqlManager.BigPuzzleDBSql._TABLENAME + " WHERE " + SqlManager.BigLevelDBSql.ID + "='" + id + "';", null);

        return c;
    }

    public Cursor getLevelCursorById(int id){

        Cursor c = mDB.rawQuery( "SELECT * FROM " + SqlManager.LevelDBSql._TABLENAME + " WHERE " + SqlManager.LevelDBSql.ID + "='" + id + "';", null);

        return c;
    }

    public Cursor getBigLevelsCursorByParentId(int id){

        Cursor c = mDB.rawQuery( "SELECT * FROM " + SqlManager.BigLevelDBSql._TABLENAME + " WHERE " + SqlManager.BigLevelDBSql.P_ID + "='" + id + "';", null);

        return c;
    }

    public Cursor getBigLevelsCursorById(int id){

        Cursor c = mDB.rawQuery( "SELECT * FROM " + SqlManager.BigLevelDBSql._TABLENAME + " WHERE " + SqlManager.BigLevelDBSql.ID + "='" + id + "';", null);

        return c;
    }

}