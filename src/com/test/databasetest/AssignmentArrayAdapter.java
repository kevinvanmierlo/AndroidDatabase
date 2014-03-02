package com.test.databasetest;

import java.util.ArrayList;

import com.test.databasetest.holders.Assignment;

import android.content.Context;
import android.widget.ArrayAdapter;

public class AssignmentArrayAdapter extends ArrayAdapter<Assignment>
{
	private Context context;
	private ArrayList<Assignment> values;
	
	public AssignmentArrayAdapter(Context context, ArrayList<Assignment> values)
	{
		super(context, R.layout.assignment_list_row, values);
		this.context = context;
		this.values = values;
	}
}
