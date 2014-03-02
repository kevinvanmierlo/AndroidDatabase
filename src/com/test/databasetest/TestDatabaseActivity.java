package com.test.databasetest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.test.databasetest.database.DataSource;
import com.test.databasetest.holders.Assignment;
import com.test.databasetest.holders.Course;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TestDatabaseActivity extends ListActivity implements ActionBar.OnNavigationListener
{
	private DataSource datasource;

	private DrawerLayout drawer;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_database);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(new ArrayAdapter<String>(actionBar.getThemedContext(), android.R.layout.simple_list_item_1, android.R.id.text1,
				getResources().getStringArray(R.array.navigation_items)), this);

		datasource = new DataSource(this);
		datasource.open();

		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

		drawerToggle = new ActionBarDrawerToggle(this, drawer, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close)
		{

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view)
			{
				super.onDrawerClosed(view);
				/*
				 * getActionBar().setTitle(mTitle); invalidateOptionsMenu();
				 */// creates call to onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView)
			{
				super.onDrawerOpened(drawerView);
				/*
				 * getActionBar().setTitle(mDrawerTitle);
				 * invalidateOptionsMenu();
				 */// creates call to onPrepareOptionsMenu()
			}
		};

		drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		drawer.setDrawerListener(drawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		drawerList = (ListView) findViewById(R.id.left_drawer);

		List<Course> drawerItems = datasource.getAllCourses();
		Course course = new Course();
		course.setCourse("Add course");
		drawerItems.add(0, course);
		
		Course course2 = new Course();
		course2.setCourse("All");
		drawerItems.add(1, course2);

		drawerList.setAdapter(new ArrayAdapter<Course>(this, android.R.layout.simple_list_item_1, drawerItems){
			@Override
		    public View getView(int position, View convertView, ViewGroup parent) {
		        View view = super.getView(position, convertView, parent);
		        TextView text = (TextView) view.findViewById(android.R.id.text1);
		        text.setTextColor(Color.WHITE);
		        return view;
		    }
		});

		drawerList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				System.out.println("position: " + position);
				
				if(position == 0)
				{
					datasource.open();
					@SuppressWarnings("unchecked")
					ArrayAdapter<Course> adapter = (ArrayAdapter<Course>) drawerList.getAdapter();
					Course course = null;
					course = datasource.createCourse("Persistent Storage");
					adapter.add(course);
					adapter.notifyDataSetChanged();
				}else if(position == 1)
				{
					datasource.open();
					@SuppressWarnings("unchecked")
					ArrayAdapter<Course> adapter = (ArrayAdapter<Course>) drawerList.getAdapter();
					Course course = (Course) adapter.getItem(position);
					datasource.deleteCourse(course);
					adapter.remove(course);
				}
			}
		});

		List<Assignment> values = datasource.getAllAssignments();

		// use the SimpleCursorAdapter to show the elements in a ListView
		final ArrayAdapter<Assignment> adapter = new ArrayAdapter<Assignment>(this, android.R.layout.simple_list_item_activated_1, values);
		setListAdapter(adapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		getListView().setMultiChoiceModeListener(new AssignmentMultiChoiceModeListener(this, getListView()));
	}

	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		System.out.println("activityresult");

		if (resultCode == Activity.RESULT_OK)
		{
			datasource.open();
			@SuppressWarnings("unchecked")
			ArrayAdapter<Assignment> adapter = (ArrayAdapter<Assignment>) getListAdapter();
			adapter.clear();
			adapter.addAll(datasource.getAllAssignments());
			adapter.notifyDataSetChanged();

			sortAssignments(getActionBar().getSelectedNavigationIndex());
		}
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id)
	{
		System.out.println(position);

		sortAssignments(position);
		return true;
	}

	private void sortAssignments(int position)
	{
		@SuppressWarnings("unchecked")
		ArrayAdapter<Assignment> adapter = (ArrayAdapter<Assignment>) getListAdapter();

		if (position == 0)
		{
			adapter.sort(compareDate());
		} else if (position == 1)
		{
			adapter.sort(compareCourse());
		}
	}

	private Comparator<Assignment> compareDate()
	{
		return new Comparator<Assignment>()
		{
			@Override
			public int compare(Assignment assignment, Assignment other)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM", Locale.getDefault());
				Date first = new Date();
				Date second = new Date();
				try
				{
					first = sdf.parse(assignment.getDeadline());
					second = sdf.parse(other.getDeadline());
				} catch (ParseException e)
				{
					e.printStackTrace();
				}

				int i = first.compareTo(second);
				if (i != 0)
					return i;

				return datasource.getCourse(assignment.getCourse()).compareTo(datasource.getCourse(other.getCourse()));
			}
		};
	}

	private Comparator<Assignment> compareCourse()
	{
		return new Comparator<Assignment>()
		{
			@Override
			public int compare(Assignment assignment, Assignment other)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM", Locale.getDefault());
				Date first = new Date();
				Date second = new Date();
				try
				{
					first = sdf.parse(assignment.getDeadline());
					second = sdf.parse(other.getDeadline());
				} catch (ParseException e)
				{
					e.printStackTrace();
				}

				int i = datasource.getCourse(assignment.getCourse()).compareTo(datasource.getCourse(other.getCourse()));
				if (i != 0)
					return i;

				return first.compareTo(second);
			}
		};
	}

	@Override
	protected void onResume()
	{
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		datasource.close();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (drawerToggle.onOptionsItemSelected(item))
		{
			return true;
		}

		switch (item.getItemId())
		{
			case R.id.menu_add:
				startActivityForResult(new Intent(this, SetAssignmentActivity.class), 1);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
	{
		new MenuInflater(this).inflate(R.menu.context_menu, menu);
	}

	public boolean performActions(MenuItem item)
	{
		@SuppressWarnings("unchecked")
		ArrayAdapter<Assignment> adapter = (ArrayAdapter<Assignment>) getListAdapter();
		SparseBooleanArray checked = getListView().getCheckedItemPositions();

		switch (item.getItemId())
		{
			case R.id.menu_delete:
				ArrayList<Integer> positions = new ArrayList<Integer>();
				for (int i = 0; i < checked.size(); i++)
				{
					if (checked.valueAt(i))
					{
						positions.add(checked.keyAt(i));
					}
				}

				Collections.sort(positions, Collections.reverseOrder());

				for (int position : positions)
				{
					Assignment assignment = (Assignment) adapter.getItem(position);
					System.out.println("assignment : " + assignment.getDeadline() + ", position: " + position + ", keyat: " + checked.keyAt(position));
					datasource.deleteAssignment(assignment);
					adapter.remove(assignment);
				}

				getListView().clearChoices();
				return (true);
		}
		return (false);
	}
}