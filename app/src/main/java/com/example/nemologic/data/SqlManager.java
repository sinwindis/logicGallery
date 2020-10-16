package com.example.nemologic.data;

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
        public static final String _TABLENAME = "Category";
        public static final String _CREATE =
                "create table if not exists "+ _TABLENAME + " ("
                        + NAME +" text primary key );";
    }

    public static final class BigPuzzleDBSql implements BaseColumns {
        public static final String NAME = "name";
        public static final String _TABLENAME = "BigPuzzle";
        public static final String WIDTH = "width";
        public static final String HEIGHT = "height";
        public static final String _CREATE =
                "create table if not exists "+ _TABLENAME + " ("
                        + WIDTH +" integer not null , "
                        + HEIGHT +" integer not null , "
                        + NAME +" text primary key );";
    }

    public static final class BigLevelDBSql implements BaseColumns {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String ORDER = "order";
        public static final String WIDTH = "width";
        public static final String HEIGHT = "height";
        public static final String PROGRESS = "progress";
        public static final String DATASET = "dataSet";
        public static final String SAVEDATA = "saveData";
        public static final String COLORSET = "colorSet";
        public static final String CUSTOM = "custom";
        public static final String _TABLENAME = "BigLevel";
        public static final String _CREATE =
                "create table if not exists "+ _TABLENAME + " ("
                        + ID +" integer primary key autoincrement, "
                        + NAME +" text not null, FOREIGN KEY("+ NAME +") REFERENCES " + BigPuzzleDBSql._TABLENAME + "(" + BigPuzzleDBSql.NAME + "),"
                        + ORDER +" integer not null, "
                        + WIDTH +" integer not null , "
                        + HEIGHT +" integer not null , "
                        + PROGRESS +" integer not null , "
                        + DATASET +" BLOB not null, "
                        + COLORSET +" BLOB not null, "
                        + SAVEDATA +" BLOB, "
                        + CUSTOM +" integer not null);";
    }
}
