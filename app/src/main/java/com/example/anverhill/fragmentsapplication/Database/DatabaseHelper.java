package com.example.anverhill.fragmentsapplication.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by anver.hill on 2017/06/10.
 */

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "Test.db";
    private static final int DATABASE_VERSION = 1;
    public static final String[] AllTables = new String[]{
            PersonTable.TABLE_NAME,
            PersonDetailTable.TABLE_NAME
    };
    private static DatabaseHelper instance = null;

    protected DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        PersonTable.onCreate(database);
        PersonDetailTable.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        PersonTable.onUpgrade(database);
        PersonDetailTable.onUpgrade(database);
    }

    public void clearTable(SQLiteDatabase database, String tableName)
    {
        database.execSQL("DELETE FROM " + tableName);
    }

    public void dropTable(SQLiteDatabase database, String tableName)
    {
        database.execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    public void recreateAllTables(SQLiteDatabase database)
    {
        for (String tableName : AllTables)
        {
            dropTable(database, tableName);
        }
        onCreate(database);
    }
}
