package com.sinwindis.logicgallery.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.TextView;

import com.sinwindis.logicgallery.R;

import java.sql.SQLException;
import java.util.ArrayList;

public class DbOpenHelper {

    private static final String DATABASE_NAME = "NemoLogicDB.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private final Context ctx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SqlManager.BigPuzzleDBSql._CREATE);
            db.execSQL(SqlManager.BigLevelDBSql._CREATE);
            db.execSQL(SqlManager.CustomBigPuzzleDBSql._CREATE);
            db.execSQL(SqlManager.CustomBigLevelDBSql._CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            //saveBlob 들을 보존하면서 db를 업데이트하는 기능을 구현해야 함
            //onCreate(db);
        }
    }

    public DbOpenHelper(Context context) {
        this.ctx = context;
    }

    public DbOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void create() {
        mDBHelper.onCreate(mDB);
    }

    public void close() {
        mDB.close();
    }

    public long insertBigLevel(int p_id, int number, int width, int height, byte[] dataSet, byte[] colorSet) {

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

    public long insertBigPuzzle(int id, int a_id, int width, int height, int l_width, int l_height, byte[] colorSet) {

        ContentValues values = new ContentValues();
        values.put(SqlManager.BigPuzzleDBSql.ID, id);
        values.put(SqlManager.BigPuzzleDBSql.A_ID, a_id);
        values.put(SqlManager.BigPuzzleDBSql.P_WIDTH, width);
        values.put(SqlManager.BigPuzzleDBSql.P_HEIGHT, height);
        values.put(SqlManager.BigPuzzleDBSql.L_WIDTH, l_width);
        values.put(SqlManager.BigPuzzleDBSql.L_HEIGHT, l_height);
        values.put(SqlManager.BigPuzzleDBSql.PROGRESS, 0);
        values.put(SqlManager.BigPuzzleDBSql.COLORSET, colorSet);

        return mDB.insert(SqlManager.BigPuzzleDBSql._TABLENAME, null, values);
    }

    public long insertCustomBigLevel(int p_id, int number, int width, int height, byte[] dataSet, byte[] colorSet) {

        ContentValues values = new ContentValues();
        values.put(SqlManager.CustomBigLevelDBSql.P_ID, p_id);
        values.put(SqlManager.CustomBigLevelDBSql.NUMBER, number);
        values.put(SqlManager.CustomBigLevelDBSql.WIDTH, width);
        values.put(SqlManager.CustomBigLevelDBSql.HEIGHT, height);
        values.put(SqlManager.CustomBigLevelDBSql.PROGRESS, 0);
        values.put(SqlManager.CustomBigLevelDBSql.DATASET, dataSet);
        values.put(SqlManager.CustomBigLevelDBSql.COLORSET, colorSet);

        return mDB.insert(SqlManager.CustomBigLevelDBSql._TABLENAME, null, values);
    }

    public long insertCustomBigPuzzle(String a_name, String p_name, int p_width, int p_height, int l_width, int l_height, byte[] colorSet) {

        ContentValues values = new ContentValues();

        values.put(SqlManager.CustomBigPuzzleDBSql.A_NAME, a_name);
        values.put(SqlManager.CustomBigPuzzleDBSql.P_NAME, p_name);
        values.put(SqlManager.CustomBigPuzzleDBSql.P_WIDTH, p_width);
        values.put(SqlManager.CustomBigPuzzleDBSql.P_HEIGHT, p_height);
        values.put(SqlManager.CustomBigPuzzleDBSql.L_WIDTH, l_width);
        values.put(SqlManager.CustomBigPuzzleDBSql.L_HEIGHT, l_height);
        values.put(SqlManager.CustomBigPuzzleDBSql.PROGRESS, 0);
        values.put(SqlManager.CustomBigPuzzleDBSql.COLORSET, colorSet);

        return mDB.insert(SqlManager.CustomBigPuzzleDBSql._TABLENAME, null, values);
    }

    public void deleteCustomBigPuzzle(int puzzleId) {
        mDB.execSQL("DELETE FROM " + SqlManager.CustomBigPuzzleDBSql._TABLENAME + " WHERE " + SqlManager.CustomBigPuzzleDBSql.ID + " = '" + puzzleId + "';");
    }

    public int updateBigLevel(int levelId, int progress, byte[] saveBlob) {
        ContentValues values = new ContentValues();

        values.put(SqlManager.BigLevelDBSql.SAVEDATA, saveBlob);
        values.put(SqlManager.BigLevelDBSql.PROGRESS, progress);

        return mDB.update(SqlManager.BigLevelDBSql._TABLENAME, values, SqlManager.BigLevelDBSql.ID + "=?", new String[]{String.valueOf(levelId)});
    }

    public int updateCustomBigLevel(int id, int progress, byte[] saveData) {
        ContentValues values = new ContentValues();

        values.put(SqlManager.CustomBigLevelDBSql.SAVEDATA, saveData);
        values.put(SqlManager.CustomBigLevelDBSql.PROGRESS, progress);

        return mDB.update(SqlManager.CustomBigLevelDBSql._TABLENAME, values, SqlManager.CustomBigLevelDBSql.ID + "=?", new String[]{String.valueOf(id)});
    }

    public void increaseBigPuzzleProgress(int puzzleId) {

        Log.d("increaseBPP", "increaseBigPuzzleProgress called");

        mDB.execSQL("UPDATE " + SqlManager.BigPuzzleDBSql._TABLENAME +
                        " SET " + SqlManager.BigPuzzleDBSql.PROGRESS + " = " + SqlManager.BigPuzzleDBSql.PROGRESS + " + ?" +
                        " WHERE " + SqlManager.BigPuzzleDBSql.ID + " = ?",
                new String[]{"1", String.valueOf(puzzleId)});
    }

    public void increaseCustomBigPuzzleProgress(int puzzleId) {

        Log.d("increaseBPP", "increaseBigPuzzleProgress called");

        mDB.execSQL("UPDATE " + SqlManager.CustomBigPuzzleDBSql._TABLENAME +
                        " SET " + SqlManager.CustomBigPuzzleDBSql.PROGRESS + " = " + SqlManager.CustomBigPuzzleDBSql.PROGRESS + " + ?" +
                        " WHERE " + SqlManager.CustomBigPuzzleDBSql.ID + " = ?",
                new String[]{"1", String.valueOf(puzzleId)});
    }

    public LevelDto getLevelDto(int levelId) {
        Cursor cursor = getBigLevelCursorById(levelId);
        if (cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToNext();

        int puzzleId = cursor.getInt(cursor.getColumnIndex(SqlManager.BigLevelDBSql.P_ID));
        String name = StringGetter.p_name.get(puzzleId);

        int number = cursor.getInt(cursor.getColumnIndex(SqlManager.BigLevelDBSql.NUMBER));
        int width = cursor.getInt(cursor.getColumnIndex(SqlManager.BigLevelDBSql.WIDTH));
        int height = cursor.getInt(cursor.getColumnIndex(SqlManager.BigLevelDBSql.HEIGHT));
        int progress = cursor.getInt(cursor.getColumnIndex(SqlManager.BigLevelDBSql.PROGRESS));
        byte[] dataSet = cursor.getBlob(cursor.getColumnIndex(SqlManager.BigLevelDBSql.DATASET));
        byte[] colorSet = cursor.getBlob(cursor.getColumnIndex(SqlManager.BigLevelDBSql.COLORSET));

        byte[] saveData = null;
        switch (progress) {
            case 1:
            case 3:
                saveData = cursor.getBlob(cursor.getColumnIndex(SqlManager.BigLevelDBSql.SAVEDATA));
                break;
        }

        return new LevelDto(levelId, puzzleId, name, number, width, height, progress, dataSet, saveData, colorSet, false);
    }

    public LevelDto getCustomLevelDto(int levelId) {

        Cursor cursor = getCustomBigLevelCursorById(levelId);
        cursor.moveToNext();

        int puzzleId = cursor.getInt(cursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.P_ID));
        String name;
        PuzzleDto puzzleDto = getCustomPuzzleDto(puzzleId);
        if (puzzleDto == null) {
            return null;
        } else {
            name = puzzleDto.getPuzzleName();
        }

        int number = cursor.getInt(cursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.NUMBER));
        int width = cursor.getInt(cursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.WIDTH));
        int height = cursor.getInt(cursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.HEIGHT));
        int progress = cursor.getInt(cursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.PROGRESS));
        byte[] dataSet = cursor.getBlob(cursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.DATASET));
        byte[] colorSet = cursor.getBlob(cursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.COLORSET));

        byte[] saveData = new byte[0];
        switch (progress) {
            case 1:
            case 3:
                //저장된 게임이 있을 때
                saveData = cursor.getBlob(cursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.SAVEDATA));
                break;
        }

        return new LevelDto(levelId, puzzleId, name, number, width, height, progress, dataSet, saveData, colorSet, true);
    }

    public ArrayList<LevelDto> getLevelDtos(int puzzleId) {
        Cursor bigLevelCursor = getBigLevelCursorByParentId(puzzleId);
        ArrayList<LevelDto> levelDtos = new ArrayList<>();

        while (bigLevelCursor.moveToNext()) {
            int id = bigLevelCursor.getInt(bigLevelCursor.getColumnIndex(SqlManager.BigLevelDBSql.ID));
            levelDtos.add(getLevelDto(id));
        }

        return levelDtos;
    }


    public ArrayList<LevelDto> getCustomLevelDtos(int puzzleId) {
        Cursor customBigLevelCursor = getCustomBigLevelCursorByParentId(puzzleId);
        ArrayList<LevelDto> levelDtos = new ArrayList<>();

        while (customBigLevelCursor.moveToNext()) {
            int id = customBigLevelCursor.getInt(customBigLevelCursor.getColumnIndex(SqlManager.CustomBigLevelDBSql.ID));
            levelDtos.add(getCustomLevelDto(id));
        }

        return levelDtos;
    }

    public PuzzleDto getPuzzleDto(int puzzleId) {
        Cursor puzzleCursor = getBigPuzzleCursorById(puzzleId);

        puzzleCursor.moveToNext();

        String puzzleName = StringGetter.p_name.get(puzzleId);
        int artistId = puzzleCursor.getInt(puzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.A_ID));
        String artistName = StringGetter.a_name.get(artistId);
        Bitmap bitmap = BitmapMaker.getColorBitmap(puzzleCursor.getBlob(puzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.COLORSET)));
        int puzzleWidth = puzzleCursor.getInt(puzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.P_WIDTH));
        int puzzleHeight = puzzleCursor.getInt(puzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.P_HEIGHT));
        int levelWidth = puzzleCursor.getInt(puzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.L_WIDTH));
        int levelHeight = puzzleCursor.getInt(puzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.L_HEIGHT));
        int progress = puzzleCursor.getInt(puzzleCursor.getColumnIndex(SqlManager.BigPuzzleDBSql.PROGRESS));

        return new PuzzleDto(puzzleId, artistName, puzzleName, bitmap, puzzleWidth, puzzleHeight, levelWidth, levelHeight, progress, false);
    }

    public PuzzleDto getCustomPuzzleDto(int puzzleId) {
        Cursor puzzleCursor = getCustomBigPuzzleCursorById(puzzleId);
        if (puzzleCursor.getCount() == 0) {
            return null;
        }

        puzzleCursor.moveToNext();

        String puzzleName = puzzleCursor.getString(puzzleCursor.getColumnIndex(SqlManager.CustomBigPuzzleDBSql.P_NAME));
        String artistName = puzzleCursor.getString(puzzleCursor.getColumnIndex(SqlManager.CustomBigPuzzleDBSql.A_NAME));
        Bitmap bitmap = BitmapMaker.getColorBitmap(puzzleCursor.getBlob(puzzleCursor.getColumnIndex(SqlManager.CustomBigPuzzleDBSql.COLORSET)));
        int puzzleWidth = puzzleCursor.getInt(puzzleCursor.getColumnIndex(SqlManager.CustomBigPuzzleDBSql.P_WIDTH));
        int puzzleHeight = puzzleCursor.getInt(puzzleCursor.getColumnIndex(SqlManager.CustomBigPuzzleDBSql.P_HEIGHT));
        int levelWidth = puzzleCursor.getInt(puzzleCursor.getColumnIndex(SqlManager.CustomBigPuzzleDBSql.L_WIDTH));
        int levelHeight = puzzleCursor.getInt(puzzleCursor.getColumnIndex(SqlManager.CustomBigPuzzleDBSql.L_HEIGHT));
        int progress = puzzleCursor.getInt(puzzleCursor.getColumnIndex(SqlManager.CustomBigPuzzleDBSql.PROGRESS));

        return new PuzzleDto(puzzleId, artistName, puzzleName, bitmap, puzzleWidth, puzzleHeight, levelWidth, levelHeight, progress, true);
    }

    public Cursor getBigPuzzleCursor() {
        Cursor c = mDB.rawQuery("SELECT * FROM " + SqlManager.BigPuzzleDBSql._TABLENAME + " ORDER BY " + SqlManager.BigPuzzleDBSql.A_ID + " , " + SqlManager.BigPuzzleDBSql.L_WIDTH + " , " + SqlManager.BigPuzzleDBSql.L_HEIGHT, null);

        return c;
    }

    public Cursor getCustomBigPuzzleCursor() {
        Cursor c = mDB.rawQuery("SELECT * FROM " + SqlManager.CustomBigPuzzleDBSql._TABLENAME + " ORDER BY " + SqlManager.CustomBigPuzzleDBSql.A_NAME + " , " + SqlManager.CustomBigPuzzleDBSql.L_WIDTH + " , " + SqlManager.CustomBigPuzzleDBSql.L_HEIGHT, null);

        return c;
    }

    public Cursor getBigPuzzleCursorById(int id) {
        Cursor c = mDB.rawQuery("SELECT * FROM " + SqlManager.BigPuzzleDBSql._TABLENAME + " WHERE " + SqlManager.BigPuzzleDBSql.ID + "='" + id + "';", null);

        return c;
    }

    public Cursor getCustomBigPuzzleCursorById(int id) {
        Cursor c = mDB.rawQuery("SELECT * FROM " + SqlManager.CustomBigPuzzleDBSql._TABLENAME + " WHERE " + SqlManager.CustomBigPuzzleDBSql.ID + "='" + id + "';", null);

        return c;
    }

    public Cursor getBigLevelCursorByParentId(int id) {

        Cursor c = mDB.rawQuery("SELECT * FROM " + SqlManager.BigLevelDBSql._TABLENAME + " WHERE " + SqlManager.BigLevelDBSql.P_ID + "='" + id + "' ORDER BY " + SqlManager.BigLevelDBSql.NUMBER + " ASC;", null);

        return c;
    }

    public Cursor getCustomBigLevelCursorByParentId(int id) {

        Cursor c = mDB.rawQuery("SELECT * FROM " + SqlManager.CustomBigLevelDBSql._TABLENAME + " WHERE " + SqlManager.CustomBigLevelDBSql.P_ID + "='" + id + "' ORDER BY " + SqlManager.CustomBigLevelDBSql.NUMBER + " ASC;", null);

        return c;
    }

    public Cursor getBigLevelCursorById(int id) {

        Cursor c = mDB.rawQuery("SELECT * FROM " + SqlManager.BigLevelDBSql._TABLENAME + " WHERE " + SqlManager.BigLevelDBSql.ID + "='" + id + "';", null);

        return c;
    }

    public Cursor getCustomBigLevelCursorById(int id) {

        Cursor c = mDB.rawQuery("SELECT * FROM " + SqlManager.CustomBigLevelDBSql._TABLENAME + " WHERE " + SqlManager.CustomBigLevelDBSql.ID + "='" + id + "';", null);

        return c;
    }

    public void saveLevelDto(LevelDto levelDto) {

        Log.d("DbOpenHelper", "saveLevelDto parameter: " + levelDto.toString());

        switch (levelDto.getProgress()) {
            case Progress.PROGRESS_FIRST:
                break;
            case Progress.PROGRESS_PLAYING:
            case Progress.PROGRESS_REPLAYING:
                if (levelDto.isCustom()) {
                    updateCustomBigLevel(levelDto.getLevelId(), levelDto.getProgress(), levelDto.getSaveBlob());
                } else {
                    updateBigLevel(levelDto.getLevelId(), levelDto.getProgress(), levelDto.getSaveBlob());
                }
                break;
            case Progress.PROGRESS_COMPLETE:
                if (levelDto.isCustom()) {
                    //커스텀 퍼즐인 경우

                    updateCustomBigLevel(levelDto.getLevelId(), levelDto.getProgress(), null);
                    //해당 레벨의 퍼즐에 해당하는 db 의 progress 값 늘려주기
                    increaseCustomBigPuzzleProgress(levelDto.getPuzzleId());
                } else {
                    //스탠다드 퍼즐인 경우

                    updateBigLevel(levelDto.getLevelId(), levelDto.getProgress(), null);
                    //해당 레벨의 퍼즐에 해당하는 db 의 progress 값 늘려주기
                    increaseBigPuzzleProgress(levelDto.getPuzzleId());
                }
                break;
            case Progress.PROGRESS_RECOMPLETE:
                if (levelDto.isCustom()) {
                    //커스텀 퍼즐인 경우

                    updateCustomBigLevel(levelDto.getLevelId(), levelDto.getProgress(), null);
                } else {
                    //스탠다드 퍼즐인 경우

                    updateBigLevel(levelDto.getLevelId(), levelDto.getProgress(), null);
                }
                break;

        }
    }


    //will be used
    //    public Cursor getBigPuzzleCursorByArtistId(int a_id) {
//        Cursor c = mDB.rawQuery("SELECT * FROM " + SqlManager.BigPuzzleDBSql._TABLENAME + " WHERE " + SqlManager.BigPuzzleDBSql.A_ID + "='" + a_id + "';", null);
//
//        return c;
//    }


}