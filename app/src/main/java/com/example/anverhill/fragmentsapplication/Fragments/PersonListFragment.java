package com.example.anverhill.fragmentsapplication.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.anverhill.fragmentsapplication.Adapters.PersonListAdapter;
import com.example.anverhill.fragmentsapplication.MainActivity;
import com.example.anverhill.fragmentsapplication.Models.Person;
import com.example.anverhill.fragmentsapplication.R;
import com.example.anverhill.fragmentsapplication.Services.AppConfiguration;
import com.example.anverhill.fragmentsapplication.Services.AppSession;

import java.util.List;

/**
 * Created by anver.hill on 2017/06/07.
 */

public class PersonListFragment extends Fragment{
    private TextView countDownTextView;
    private CountDownTimer countDownTimer;
    private PersonListAdapter personListAdapter = null;
    private List<Person> personList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    public void updateInfo(String info, boolean isError)
    {
        View view = getView();
        if(view != null)
        {
            TextView textView = (TextView) view.findViewById(R.id.info_textview);
            textView.setText(info);

            if(isError)
            {
                textView.setTextColor(Color.RED);
            }
            else
            {
                textView.setTextColor(Color.BLACK);
            }
        }
    }

    public void updateTimer()
    {
        View view = getView();
        if(view != null)
        {
            countDownTextView = (TextView) view.findViewById(R.id.countdown_textview);
            if(countDownTimer == null)
            {
                countDownTimer = new CountDownTimer(AppConfiguration.DefaultCountdownTime, 1000L) {
                    public void onTick(long millisUntilFinished) {
                        if(countDownTextView != null)
                        {
                            Long seconds = millisUntilFinished/1000L;
                            String secondsValue = String.valueOf(seconds);
                            countDownTextView.setText(secondsValue);
                            AppSession.getInstance().SetCountDownTime(millisUntilFinished);
                        }
                    }

                    public void onFinish() {
                        if(getActivity() != null)
                        {
                            ((MainActivity)getActivity()).updatePersonListRequest(true);
                            AppSession.getInstance().SetCountDownTime(0L);
                        }
                    }
                };
            }
            countDownTimer.start();
        }
    }

    public void stopTimer()
    {
        if(countDownTimer != null)
        {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onDestroy() {
        if(countDownTimer != null)
        {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI(true);
        updateTimer();
    }

    public void setPersonData(List<Person> persons)
    {
        personList = persons;
    }

    public void updateUI(boolean clearInfo)
    {
        if(personList != null)
        {
            personListAdapter = new PersonListAdapter(getActivity(), personList);
            View view = getView();
            if(view != null)
            {
                ListView personListView = (ListView) view.findViewById(R.id.person_list);
                personListView.setAdapter(personListAdapter);
                personListView.setOnItemClickListener((MainActivity)getActivity());
            }

        }
        if(clearInfo)
        {
            updateInfo("", false);
        }
    }
}
