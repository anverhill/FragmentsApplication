package com.example.anverhill.fragmentsapplication.Models;

import android.content.Context;

import com.example.anverhill.fragmentsapplication.Database.DatabaseRepository;
import com.example.anverhill.fragmentsapplication.Services.TestAppException;

import org.json.JSONObject;

/**
 * Created by anver.hill on 2017/06/10.
 */

public class Person {
    public int Id;
    public long objectId;
    public String firstName;
    public String lastName;
    public long detailId;

    public static void syncFromJson(Context context, JSONObject dbEntry) throws TestAppException
    {
        try
        {
            long objectId = dbEntry.getLong("id");
            String firstName = dbEntry.getString("firstName");
            String lastName = dbEntry.getString("lastName");
            DatabaseRepository.getInstance(context).insertOrUpdatePerson(objectId, firstName, lastName);
        }
        catch (Exception e)
        {
            TestAppException exception = new TestAppException(true, false, e);
            exception.Explanation = "Error saving to Person";
            throw exception;
        }
    }
}
