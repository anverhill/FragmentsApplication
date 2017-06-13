package com.example.anverhill.fragmentsapplication.Database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by anver.hill on 2017/06/10.
 */

public class PersonTable {
    public static final String TABLE_NAME = "Person";
    public static final String COLUMN_ID = "_Id";
    public static final String OBJECT_ID = "object_id";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String DETAIL_ID = "detailId";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + OBJECT_ID + " real default -1,"
            + FIRST_NAME + " text , "
            + LAST_NAME + " text, "
            + DETAIL_ID + " real default -1"
            + ")";

    public static final String[] Columns = {
            PersonTable.COLUMN_ID,
            PersonTable.OBJECT_ID,
            PersonTable.FIRST_NAME,
            PersonTable.LAST_NAME,
            PersonTable.DETAIL_ID
    };

    public static void onCreate(SQLiteDatabase database)
    {
        database.execSQL(DATABASE_CREATE);
        database.execSQL("CREATE UNIQUE INDEX IDX_" + TABLE_NAME + "_OBJECT_ID ON " + TABLE_NAME + "(" + OBJECT_ID + ");");
    }

    public static void onUpgrade(SQLiteDatabase database)
    {
        database.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(database);
    }
}
