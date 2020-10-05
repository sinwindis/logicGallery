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
        public static final String DATASET = "dataset";
        public static final String SAVEDATA = "savedata";
        public static final String COLORSET = "colorset";
        public static final String _TABLENAME = "level";
        public static final String _CREATE =
                "create table if not exists "+ _TABLENAME + " ("
                        + ID +" integer primary key autoincrement, "
                        + NAME +" text not null, "
                        + CATEGORY +" text not null, "
                        + WIDTH +" integer not null , "
                        + HEIGHT +" integer not null , "
                        + PROGRESS +" integer not null , "
                        + DATASET +" text not null, "
                        + COLORSET +" text not null, "
                        + SAVEDATA +" text );";
    }

    public static final class CategoryDBSql implements BaseColumns {
        public static final String NAME = "name";
        public static final String _TABLENAME = "category";
        public static final String _CREATE =
                "create table if not exists "+ _TABLENAME + " ("
                        + NAME +" text primary key );";
    }
}
