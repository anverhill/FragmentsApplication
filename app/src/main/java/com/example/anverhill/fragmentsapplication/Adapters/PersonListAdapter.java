package com.example.anverhill.fragmentsapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.anverhill.fragmentsapplication.Models.Person;
import com.example.anverhill.fragmentsapplication.R;

import java.util.List;

/**
 * Created by anver.hill on 2017/06/10.
 */

public class PersonListAdapter extends BaseAdapter
{
    private List<Person> PersonList;
    private LayoutInflater inflater;

    public PersonListAdapter(Context context, List<Person> personList)
    {
        PersonList = personList;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;
        if (rowView == null)
        {
            rowView = inflater.inflate(R.layout.person_list_item, null);
            RowDataViewHolder viewHolder = new RowDataViewHolder();
            viewHolder.fullName = (TextView)rowView.findViewById(R.id.fullNameTextView);
            rowView.setTag(viewHolder);
        }

        RowDataViewHolder holder = (RowDataViewHolder)rowView.getTag();
        Person person = PersonList.get(position);
        if(person != null && person.firstName != null && person.lastName != null)
        {
            StringBuilder stringBuilder = new StringBuilder(person.firstName);
            stringBuilder.append(" ");
            stringBuilder.append(person.lastName);
            holder.fullName.setText(stringBuilder);
        }
        else
        {
            holder.fullName.setText("");
        }

        return rowView;
    }

    @Override
    public int getCount()
    {
        return PersonList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return PersonList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        Person person = PersonList.get(position);
        if(person != null)
        {
            return person.objectId;
        }
        return -1;
    }

    static class RowDataViewHolder
    {
        public TextView fullName;
    }
}