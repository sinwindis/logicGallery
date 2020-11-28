package com.sinwindis.nemologic.data;

import android.provider.BaseColumns;

public class SqlManager {

    public SqlManager()
    {

    }

    public static final class LevelDBSql implements BaseColumns {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String CATEGORY = "category";
        public static final String WIDTH = "width";
        public static final String HEIGHT = "height";
        public static final String PROGRESS = "progress";
        public static final String DATASET = "dataSet";
        public static final String SAVEDATA = "saveData";
        public static final String COLORSET = "colorSet";
        public static final String CUSTOM = "custom";
        public static final String _TABLENAME = "Level";
        public static final String _CREATE =
                "create table if not exists "+ _TABLENAME + " ("
                        + ID +" integer primary key autoincrement, "
                        + NAME +" text not null, "
                        + CATEGORY +" text not null, "
                        + WIDTH +" integer not null , "
                        + HEIGHT +" integer not null , "
                        + PROGRESS +" integer not null , "
                        + DATASET +" BLOB not null, "
                        + COLORSET +" BLOB not null, "
                        + SAVEDATA +" BLOB, "
                        + CUSTOM +" integer not null);";
    }

    public static final class CategoryDBSql implements BaseColumns {
        public static final String NAME = "name";
        public static final String PROGRESS = "progress";
        public static final String _TABLENAME = "Category";
        public static final String _CREATE =
                "create table if not exists "+ _TABLENAME + " ("
                        + PROGRESS +" integer not null , "
                        + NAME +" text primary key );";
    }

    public static final class BigPuzzleDBSql implements BaseColumns {
        public static final String ID = "id";
        public static final String A_ID = "a_id";
        public static final String P_WIDTH = "p_width";
        public static final String P_HEIGHT = "p_height";
        public static final String L_WIDTH = "l_width";
        public static final String L_HEIGHT = "l_height";
        public static final String PROGRESS = "progress";
        public static final String COLORSET = "colorSet";
        public static final String _TABLENAME = "BigPuzzle";
        public static final String _CREATE =
                "create table if not exists "+ _TABLENAME + " ("
                        + ID +" integer primary key autoincrement, "
                        + A_ID +" integer not null, "
                        + P_WIDTH +" integer not null , "
                        + P_HEIGHT +" integer not null , "
                        + L_WIDTH +" integer not null , "
                        + L_HEIGHT +" integer not null , "
                        + PROGRESS +" integer not null , "
                        + COLORSET +" BLOB not null );";
    }

    public static final class BigLevelDBSql implements BaseColumns {
        public static final String ID = "id";
        public static final String P_ID = "p_id";
        public static final String NUMBER = "number";
        public static final String WIDTH = "width";
        public static final String HEIGHT = "height";
        public static final String PROGRESS = "progress";
        public static final String DATASET = "dataSet";
        public static final String SAVEDATA = "saveData";
        public static final String COLORSET = "colorSet";
        public static final String _TABLENAME = "BigLevel";
        public static final String _CREATE =
                "create table if not exists "+ _TABLENAME + " ("
                        + ID +" integer primary key autoincrement, "
                        + P_ID +" integer not null, "
                        + NUMBER +" integer not null, "
                        + WIDTH +" integer not null , "
                        + HEIGHT +" integer not null , "
                        + PROGRESS +" integer not null , "
                        + DATASET +" BLOB not null, "
                        + COLORSET +" BLOB not null, "
                        + SAVEDATA +" BLOB);";
    }

    public static final class CustomBigPuzzleDBSql implements BaseColumns {
        public static final String ID = "id";
        public static final String A_NAME = "a_name";
        public static final String P_NAME = "p_name";
        public static final String P_WIDTH = "p_width";
        public static final String P_HEIGHT = "p_height";
        public static final String L_WIDTH = "l_width";
        public static final String L_HEIGHT = "l_height";
        public static final String PROGRESS = "progress";
        public static final String COLORSET = "colorSet";
        public static final String _TABLENAME = "CustomBigPuzzle";
        public static final String _CREATE =
                "create table if not exists "+ _TABLENAME + " ("
                        + ID +" integer primary key autoincrement, "
                        + A_NAME +" text not null , "
                        + P_NAME +" text not null , "
                        + P_WIDTH +" integer not null , "
                        + P_HEIGHT +" integer not null , "
                        + L_WIDTH +" integer not null , "
                        + L_HEIGHT +" integer not null , "
                        + PROGRESS +" integer not null , "
                        + COLORSET +" BLOB not null );";
    }

    public static final class CustomBigLevelDBSql implements BaseColumns {
        public static final String ID = "id";
        public static final String P_ID = "p_id";
        public static final String NUMBER = "number";
        public static final String WIDTH = "width";
        public static final String HEIGHT = "height";
        public static final String PROGRESS = "progress";
        public static final String DATASET = "dataSet";
        public static final String SAVEDATA = "saveData";
        public static final String COLORSET = "colorSet";
        public static final String _TABLENAME = "CustomBigLevel";
        public static final String _CREATE =
                "create table if not exists "+ _TABLENAME + " ("
                        + ID +" integer primary key autoincrement, "
                        + P_ID +" integer not null, "
                        + NUMBER +" integer not null, "
                        + WIDTH +" integer not null , "
                        + HEIGHT +" integer not null , "
                        + PROGRESS +" integer not null , "
                        + DATASET +" BLOB not null, "
                        + COLORSET +" BLOB not null, "
                        + SAVEDATA +" BLOB);";
    }
}
