package com.test.databasetest.adapters;

import java.util.List;

import com.test.databasetest.holders.Course;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DrawerArrayAdapter extends ArrayAdapter<Course>
{	
	public DrawerArrayAdapter(Context context, List<Course> values)
	{
		super(context, android.R.layout.simple_list_item_activated_1, values);
	}
    
    @Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = super.getView(position, convertView, parent);
		TextView text = (TextView) view.findViewById(android.R.id.text1);
		text.setTextColor(Color.WHITE);
		return view;
	}
}
