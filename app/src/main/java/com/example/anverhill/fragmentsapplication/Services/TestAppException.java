package com.example.anverhill.fragmentsapplication.Services;

/**
 * Created by anver.hill on 2017/06/10.
 */

public class TestAppException extends Exception
{

    public boolean Ignore;
    public boolean Handled;
    public String Explanation;
    public Exception InnerException;


    public TestAppException(boolean ignore, boolean handled, Exception innerException)
    {
        Ignore = ignore;
        Handled = handled;
        InnerException = innerException;
    }
}