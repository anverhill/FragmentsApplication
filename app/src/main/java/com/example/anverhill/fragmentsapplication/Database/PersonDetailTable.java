package com.example.anverhill.fragmentsapplication.Database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by anver.hill on 2017/06/10.
 */

public class PersonDetailTable {
    public static final String TABLE_NAME = "PersonDetail";
    public static final String COLUMN_ID = "_Id";
    public static final String OBJECT_ID = "object_id";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String AGE = "age";
    public static final String FAVOURITE_COLOUR= "favouriteColour";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + OBJECT_ID + " real default -1,"
            + FIRST_NAME + " text, "
            + LAST_NAME + " text, "
            + AGE + " integer, "
            + FAVOURITE_COLOUR + " text"
            + ")";

    public static final String[] Columns = {
            PersonDetailTable.COLUMN_ID,
            PersonDetailTable.OBJECT_ID,
            PersonDetailTable.FIRST_NAME,
            PersonDetailTable.LAST_NAME,
            PersonDetailTable.AGE,
            PersonDetailTable.FAVOURITE_COLOUR
    };

    public static void onCreate(SQLiteDatabase database)
    {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database)
    {
        database.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(database);
    }
}
