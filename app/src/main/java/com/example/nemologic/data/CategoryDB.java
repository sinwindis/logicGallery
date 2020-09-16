package com.example.nemologic.data;

import android.provider.BaseColumns;

public class CategoryDB {

    public static final class CreateDB implements BaseColumns {
        public static final String NAME = "name";
        public static final String _TABLENAME = "categorytable";
        public static final String _CREATE =
                "create table if not exists "+ _TABLENAME +"("
                + NAME +" text primary key );";
    }
}
