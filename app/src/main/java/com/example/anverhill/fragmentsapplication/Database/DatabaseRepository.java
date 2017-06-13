package com.example.anverhill.fragmentsapplication.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.example.anverhill.fragmentsapplication.Models.Person;
import com.example.anverhill.fragmentsapplication.Models.PersonDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anver.hill on 2017/06/10.
 */
public class DatabaseRepository
{
    private static DatabaseRepository instance = null;
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public DatabaseRepository(Context context)
    {
        dbHelper = DatabaseHelper.getInstance(context);
        openDatabase();
    }

    public static synchronized DatabaseRepository getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new DatabaseRepository(context);
        }
        return instance;
    }

    private String[] getTableColumns(String tableName)
    {
        if (tableName.equals(PersonTable.TABLE_NAME))
        {
            return PersonTable.Columns;
        }
        else if (tableName.equals(PersonDetailTable.TABLE_NAME))
        {
            return PersonDetailTable.Columns;
        }
        return new String[]{""};
    }

    private void openDatabase() throws SQLException
    {
        if (database == null)
        {
            database = dbHelper.getWritableDatabase();
            assert database != null;
            database.enableWriteAheadLogging();
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Inserts or Updates">
    public void insertOrUpdatePerson(long objectId, String firstName, String lastName)
    {
        ContentValues values = new ContentValues();
        values.put(PersonTable.OBJECT_ID, objectId);
        values.put(PersonTable.FIRST_NAME, firstName);
        values.put(PersonTable.LAST_NAME, lastName);
        try
        {
            database.insertOrThrow(PersonTable.TABLE_NAME, null,
                    values);
        }
        catch (SQLiteConstraintException e)
        {
            Cursor cursor = database.query(PersonTable.TABLE_NAME,
                    PersonTable.Columns, PersonTable.OBJECT_ID + " = " + objectId, null, null, null, null);
            if (cursor.moveToFirst())
            {
                cursor.close();
                database.update(PersonTable.TABLE_NAME, values, PersonTable.OBJECT_ID + " = " + objectId, null);
            }
        }
    }

    public void updatePerson(long objectId, String firstName, String lastName, long detailId)
    {
        ContentValues values = new ContentValues();
        values.put(PersonTable.OBJECT_ID, objectId);
        values.put(PersonTable.FIRST_NAME, firstName);
        values.put(PersonTable.LAST_NAME, lastName);
        values.put(PersonTable.DETAIL_ID, detailId);
        try
        {
            database.insertOrThrow(PersonTable.TABLE_NAME, null,
                    values);
        }
        catch (SQLiteConstraintException e)
        {
            Cursor cursor = database.query(PersonTable.TABLE_NAME,
                    PersonTable.Columns, PersonTable.OBJECT_ID + " = " + objectId, null, null, null, null);
            if (cursor.moveToFirst())
            {
                cursor.close();
                database.update(PersonTable.TABLE_NAME, values, PersonTable.OBJECT_ID + " = " + objectId, null);
            }
        }
    }

    private Person cursorToPerson(Cursor cursor)
    {
        Person person = new Person();
        person.Id = cursor.getInt(0);
        person.objectId = cursor.getLong(1);
        person.firstName= cursor.getString(2);
        person.lastName = cursor.getString(3);
        person.detailId = cursor.getLong(4);
        return person;
    }

    public long insertPersonDetail(long objectId, String firstName, String lastName, int age, String favouriteColour)
    {
        ContentValues values = new ContentValues();
        values.put(PersonDetailTable.OBJECT_ID, objectId);
        values.put(PersonDetailTable.FIRST_NAME, firstName);
        values.put(PersonDetailTable.LAST_NAME, lastName);
        values.put(PersonDetailTable.AGE, age);
        values.put(PersonDetailTable.FAVOURITE_COLOUR, favouriteColour);

        Cursor cursor = database.query(PersonDetailTable.TABLE_NAME,
                PersonDetailTable.Columns, PersonDetailTable.OBJECT_ID + " = " + objectId
                + " AND " + PersonDetailTable.FIRST_NAME + " = " + "\'" + firstName + "\'"
                        + " AND " + PersonDetailTable.LAST_NAME + " = " + "\'" + lastName + "\'"
                        + " AND " + PersonDetailTable.AGE + " = " + age
                        + " AND " + PersonDetailTable.FAVOURITE_COLOUR + " = " + "\'" + favouriteColour + "\'"
                , null, null, null, null);

        if (!cursor.moveToFirst()){
            cursor.close();
            try
            {
                return database.insertOrThrow(PersonDetailTable.TABLE_NAME, null,
                        values);
            }
            catch (SQLiteConstraintException e)
            {
                return -1;
            }
        }
        else
        {
            cursor.close();
            PersonDetail personDetail = cursorToPersonDetail(cursor);
            return  personDetail.Id;
        }
    }

    private PersonDetail cursorToPersonDetail(Cursor cursor)
    {
        PersonDetail personDetail = new PersonDetail();
        personDetail.Id = cursor.getInt(0);
        personDetail.objectId = cursor.getLong(1);
        personDetail.firstName= cursor.getString(2);
        personDetail.lastName = cursor.getString(3);
        personDetail.age = cursor.getInt(4);
        personDetail.favouriteColour = cursor.getString(5);
        return personDetail;
    }

    //<editor-fold defaultstate="collapsed" desc="Return Data From Database">
    public List<Person> getPersonList()
    {
        Cursor  cursor = database.rawQuery("select * from " + PersonTable.TABLE_NAME,null);
        List<Person> playerList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Person player = cursorToPerson(cursor);
                playerList.add(player);
                cursor.moveToNext();
            }
        }
        return playerList;
    }

    public PersonDetail getPersonDetail(long localId)
    {
        String query = String.format(PersonDetailTable.COLUMN_ID + " = %d", localId);
        Cursor cursor = database.query(PersonDetailTable.TABLE_NAME, PersonDetailTable.Columns, query, null, null, null, null);
        cursor.moveToFirst();
        PersonDetail personDetail = null;
        if (!cursor.isAfterLast())
        {
            personDetail = cursorToPersonDetail(cursor);
        }
        return personDetail;
    }

//</editor-fold>
}
