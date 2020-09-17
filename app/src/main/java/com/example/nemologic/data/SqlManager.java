package com.example.nemologic.data;

import android.provider.BaseColumns;

public class SqlManager {

    public SqlManager()
    {

    }

    public static final class CreateLevelDB implements BaseColumns {
        public static final String NAME = "name";
        public static final String WIDTH = "width";
        public static final String HEIGHT = "height";
        public static final String PROGRESS = "progress";
        public static final String DATASET = "dataset";
        public static final String SAVEDATA = "savedata";

        public static String getCreateStr(String category)
        {
            return "create table if not exists " + category + " ("
                    + NAME +" text primary key, "
                    + WIDTH +" integer not null , "
                    + HEIGHT +" integer not null , "
                    + PROGRESS +" integer not null , "
                    + DATASET +" text not null, "
                    + SAVEDATA +" text );";
        }
    }

    public static final class CreateCategoryDB implements BaseColumns {
        public static final String NAME = "name";
        public static final String _TABLENAME = "category";
        public static final String _CREATE =
                "create table if not exists "+ _TABLENAME + " ("
                        + NAME +" text primary key );";
    }
}
