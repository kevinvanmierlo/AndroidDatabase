package com.test.databasetest.adapters;

import java.util.ArrayList;
import java.util.List;

import com.test.databasetest.R;
import com.test.databasetest.database.DataSource;
import com.test.databasetest.holders.Assignment;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AssignmentArrayAdapter extends BaseAdapter
{
	private int SECTION = 0;
	private int NORMAL = 1;

	private DataSource datasource;
	private Context context;
	private List<Assignment> values;
	private List<SparseArray<Assignment>> rowValues;
	private int sortBy;

	public AssignmentArrayAdapter(Context context, List<Assignment> values, int sortBy)
	{
//		super(context, R.layout.assignment_list_row, values);
		this.context = context;
		this.values = values;
		datasource = new DataSource(context);
		datasource.open();
		rowValues = new ArrayList<SparseArray<Assignment>>();
		this.sortBy = sortBy;

		getRowValues();
	}

	private void getRowValues()
	{
		rowValues.clear();
		
		long course = -1;
		String deadline = "0000-00-00";

		for (int a = 0; a < values.size(); a++)
		{
			if(sortBy == DataSource.ORDER_BY_COURSE)
			{
				if (values.get(a).getCourseID() != (course))
				{
					SparseArray<Assignment> value = new SparseArray<Assignment>();
					value.put(SECTION, values.get(a));
					rowValues.add(value);
					course = values.get(a).getCourseID();
				}
			}else
			{
				if (!values.get(a).getDeadline().equals(deadline))
				{
					SparseArray<Assignment> value = new SparseArray<Assignment>();
					value.put(SECTION, values.get(a));
					rowValues.add(value);
					deadline = values.get(a).getDeadline();
				}
			}

			SparseArray<Assignment> value = new SparseArray<Assignment>();
			value.put(NORMAL, values.get(a));
			rowValues.add(value);
		}
	}

	/*private Comparator<Course> compareCourses()
	{
		return new Comparator<Course>()
		{
			@Override
			public int compare(Course course, Course other)
			{
				return course.getCourse().compareTo(course.getCourse());
			}
		};
	}*/
	
	public void clear()
	{
		values.clear();
	}
	
	public void addAll(List<Assignment> list, int sortBy)
	{
		values.addAll(list);
		this.sortBy = sortBy;
		notifyDataSetChanged();
	}
	
	public void remove(Assignment assignment)
	{
		values.remove(assignment);
		notifyDataSetChanged();
	}
	
	@Override
	public void notifyDataSetChanged()
	{
		getRowValues();
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount()
	{
		return rowValues.size();
	}

	@Override
	public Assignment getItem(int position)
	{
		return rowValues.get(position).valueAt(0);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public int getViewTypeCount()
	{
		return 2;
	}

	@Override
	public int getItemViewType(int position)
	{
		return rowValues.get(position).keyAt(0);
	}

	@Override
	public boolean isEnabled(int position)
	{
		// A separator cannot be clicked !
		return getItemViewType(position) != SECTION;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final int type = getItemViewType(position);

		if (convertView == null)
		{
			final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final int layoutID = type == SECTION ? R.layout.assignment_list_section : R.layout.assignment_list_row;
			convertView = inflater.inflate(layoutID, parent, false);
		}

		if (type == SECTION)
		{
			TextView course = (TextView) convertView.findViewById(R.id.assignment_section);
			if(sortBy == DataSource.ORDER_BY_COURSE)
				course.setText(rowValues.get(position).valueAt(0).getCourse());
			else
				course.setText(rowValues.get(position).valueAt(0).getDeadline());
		} else
		{
			if(sortBy == DataSource.ORDER_BY_COURSE)
				((TextView) convertView.findViewById(R.id.assignment_row_first)).setText(rowValues.get(position).valueAt(0).getDeadline());
			else
				((TextView) convertView.findViewById(R.id.assignment_row_first)).setText(rowValues.get(position).valueAt(0).getCourse());
			
			((TextView) convertView.findViewById(R.id.assignment_row_assignment)).setText(rowValues.get(position).valueAt(0).getAssignment());
		}

		return convertView;
	}
}
