package com.example.anverhill.fragmentsapplication.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.anverhill.fragmentsapplication.Database.DatabaseRepository;
import com.example.anverhill.fragmentsapplication.Models.PersonDetail;
import com.example.anverhill.fragmentsapplication.R;

/**
 * Created by anver.hill on 2017/06/07.
 */

public class PersonDetailFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.detail_fragment, container, false);
    }
    public void updateDetails(PersonDetail personDetail)
    {
        if(personDetail != null)
        {
            View view = getView();
            if(personDetail.firstName != null)
            {
                if(view != null)
                {
                    TextView textView = (TextView) view.findViewById(R.id.firstNameTextView);
                    textView.setText(personDetail.firstName);
                }
            }
            if(personDetail.lastName != null)
            {
                if(view != null)
                {
                    TextView textView = (TextView) view.findViewById(R.id.lastNameTextView);
                    textView.setText(personDetail.lastName);
                }
            }
            if(personDetail.age > 0)
            {
                if(view != null)
                {
                    TextView textView = (TextView) view.findViewById(R.id.ageTextView);
                    textView.setText(Long.toString(personDetail.age));
                }
            }
            if(personDetail.favouriteColour != null)
            {
                if(view != null)
                {
                    TextView textView = (TextView) view.findViewById(R.id.favouriteColourTextView);
                    textView.setText(personDetail.favouriteColour);
                }
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            long personId = bundle.getLong(getResources().getString(R.string.selected_person_id));
            PersonDetail personDetail = DatabaseRepository.getInstance(getContext()).getPersonDetail(personId);
            updateDetails(personDetail);
        }
    }
}
