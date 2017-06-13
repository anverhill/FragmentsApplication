package com.example.anverhill.fragmentsapplication.Models;

import android.content.Context;

import com.example.anverhill.fragmentsapplication.Database.DatabaseRepository;
import com.example.anverhill.fragmentsapplication.Services.TestAppException;

import org.json.JSONObject;

/**
 * Created by anver.hill on 2017/06/10.
 */

public class PersonDetail {
    public int Id;
    public long objectId;
    public String firstName;
    public String lastName;
    public int age;
    public String favouriteColour;

    public static long syncFromJson(Context context, JSONObject dbEntry) throws TestAppException
    {
        try
        {
            long objectId = dbEntry.getLong("id");
            String firstName = dbEntry.getString("firstName");
            String lastName = dbEntry.getString("lastName");
            int age = dbEntry.getInt("age");
            String favouriteColour = dbEntry.getString("favouriteColour");
            return DatabaseRepository.getInstance(context).insertPersonDetail(objectId, firstName, lastName, age, favouriteColour);
        }
        catch (Exception e)
        {
            TestAppException exception = new TestAppException(true, false, e);
            exception.Explanation = "Error saving to Person";
            throw exception;
        }
    }
}