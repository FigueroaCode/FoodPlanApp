package com.personal.debrian.squareone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import Models.Suggestion;

public class CustomListAdapter extends ArrayAdapter<Suggestion> {

    public CustomListAdapter(Context context, ArrayList<Suggestion> list) {
        super(context, R.layout.custom_row,list);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View view = inflater.inflate(R.layout.custom_row,parent,false);

        RelativeLayout background = (RelativeLayout) view.findViewById(R.id.row_background);
        TextView nameText = (TextView) view.findViewById(R.id.nameText);
        TextView foodText = (TextView) view.findViewById(R.id.foodText);

        String name = getItem(position).getName();
        String food = getItem(position).getFood();

        nameText.setText(name);
        foodText.setText(food);
        try {
            String color = getItem(position).getColor();
            switch (color) {
                case "breakfast":
                    background.setBackgroundResource(R.color.breakfast_color);
                    break;
                case "lunch":
                    background.setBackgroundResource(R.color.lunch_color);
                    break;
                case "dinner":
                    background.setBackgroundResource(R.color.dinner_color);
                    break;
                default:
                    Log.d("Setting Background", "Something wrong with color: " + color);
            }
        }catch (NullPointerException e){
            Log.d("Setting Background", "Color is Null");
        }
        return view;
    }
}
