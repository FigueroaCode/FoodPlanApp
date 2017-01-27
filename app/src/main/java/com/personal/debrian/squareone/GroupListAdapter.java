package com.personal.debrian.squareone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by debrian on 1/26/17.
 */

public class GroupListAdapter extends ArrayAdapter<String> {

    public GroupListAdapter(Context context, ArrayList<String> objects) {
        super(context,R.layout.group_row , objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.group_row,parent,false);

        TextView groupName = (TextView) view.findViewById(R.id.groupNameText);

        String name = getItem(position);
        groupName.setText(name);
        return view;
    }
}
