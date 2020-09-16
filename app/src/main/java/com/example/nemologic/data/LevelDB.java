package com.example.nemologic.data;

import android.provider.BaseColumns;

public class LevelDB {

    public static final class CreateDB implements BaseColumns {
        public static final String NAME = "name";
        public static final String WIDTH = "width";
        public static final String HEIGHT = "height";
        public static final String CATEGORY = "category";
        public static final String PROGRESS = "progress";
        public static final String DATASET = "dataset";
        public static final String SAVEDATA = "savedata";
        public static final String _TABLENAME = "leveltable";
        public static final String _CREATE =
                "create table if not exists "+ _TABLENAME +"("
                + NAME +" text primary key, "
                + CATEGORY + " text not null, "
                + WIDTH +" integer not null , "
                + HEIGHT +" integer not null , "
                + PROGRESS +" integer not null , "
                + DATASET +" text not null, "
                + SAVEDATA +" text );";
    }
}
