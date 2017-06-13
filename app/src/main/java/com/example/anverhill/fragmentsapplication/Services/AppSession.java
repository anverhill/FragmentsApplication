package com.example.anverhill.fragmentsapplication.Services;


/**
 * Created by anver.hill on 2017/06/10.
 */

public class AppSession {
    private long selectedPersonId, countDownTime, personDetailId;
    private static AppSession instance = null;
    protected AppSession() {
    }

    public static AppSession getInstance() {
        if (instance == null) {
            instance = new AppSession();

        }
        return instance;
    }

    public void SetPersonId(long personId)
    {
        selectedPersonId = personId;
    }

    public long GetPersonId()
    {
        return selectedPersonId;
    }
    public void SetPersonDetailId(long personId)
    {
        personDetailId = personId;
    }

    public long GetPersonDetailId()
    {
        return personDetailId;
    }

    public void SetCountDownTime(long time)
    {
        countDownTime = time;
    }

    public long GetCountDownTime()
    {
        return countDownTime;
    }
}
