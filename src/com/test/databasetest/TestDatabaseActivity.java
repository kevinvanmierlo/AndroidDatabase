package com.test.databasetest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.test.databasetest.adapters.AssignmentArrayAdapter;
import com.test.databasetest.adapters.DrawerArrayAdapter;
import com.test.databasetest.database.DataSource;
import com.test.databasetest.holders.Assignment;
import com.test.databasetest.holders.Course;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class TestDatabaseActivity extends ListActivity implements ActionBar.OnNavigationListener
{
	private DataSource datasource;

	private DrawerLayout drawer;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;
	private AssignmentArrayAdapter assignmentAdapter;
	private DrawerArrayAdapter drawerAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_database);

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		actionBar.setListNavigationCallbacks(new ArrayAdapter<String>(actionBar.getThemedContext(), android.R.layout.simple_list_item_1, android.R.id.text1,
				getResources().getStringArray(R.array.navigation_items)), this);

		datasource = new DataSource(this);
		datasource.open();

		setupDrawer();

		List<Assignment> values = datasource.getAllAssignments(getActionBar().getSelectedNavigationIndex());

		assignmentAdapter = new AssignmentArrayAdapter(this, values, getActionBar().getSelectedNavigationIndex());
		setListAdapter(assignmentAdapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		getListView().setMultiChoiceModeListener(new AssignmentMultiChoiceModeListener(this, getListView()));
	}

	private void setupDrawer()
	{
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerToggle = new ActionBarDrawerToggle(this, drawer, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);
		drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		drawer.setDrawerListener(drawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		drawerList = (ListView) findViewById(R.id.left_drawer);
		drawerAdapter = new DrawerArrayAdapter(this, getDrawerItems());
		drawerList.setAdapter(drawerAdapter);

		drawerList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				view.setTag(position);
				if (position == 0)
				{
					filterAssignments(1);
					setInputAlert(false, "Add Course", "Type your new course here", "Add", "", null);
				} else
				{
					filterAssignments(position);
					drawer.closeDrawer(drawerList);
				}
			}
		});

		drawerList.setItemChecked(1, true);

		registerForContextMenu(drawerList);
	}

	private List<Course> getDrawerItems()
	{
		List<Course> drawerItems = datasource.getAllCourses();
		Course course = new Course();
		course.setCourse("Add course");
		drawerItems.add(0, course);

		Course course2 = new Course();
		course2.setCourse("All");
		drawerItems.add(1, course2);

		return drawerItems;
	}

	private void setInputAlert(final boolean edit, String title, String message, String positiveButton, String inputText, final Course course)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(TestDatabaseActivity.this);

		alert.setTitle(title);
		alert.setMessage(message);

		final EditText input = new EditText(TestDatabaseActivity.this);
		input.setLines(1);
		input.setSingleLine(true);
		input.setText(inputText);
		alert.setView(input);

		alert.setPositiveButton(positiveButton, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				if (edit)
				{
					course.setCourse(input.getText().toString());
					datasource.updateCourse(course);
					drawerAdapter.clear();
					drawerAdapter.addAll(getDrawerItems());
					assignmentAdapter.clear();
					assignmentAdapter.addAll(datasource.getAllAssignments(getActionBar().getSelectedNavigationIndex()), getActionBar().getSelectedNavigationIndex());
				} else
				{
					String value = input.getText().toString();
					datasource.open();
					datasource.createCourse(value);
					drawerAdapter.clear();
					drawerAdapter.addAll(getDrawerItems());
				}
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
			}
		});

		alert.show();
	}

	private void filterAssignments(int position)
	{
		drawerList.setItemChecked(position, true);

		if (position == 1)
		{
			assignmentAdapter.clear();
			assignmentAdapter.addAll(datasource.getAllAssignments(getActionBar().getSelectedNavigationIndex()), getActionBar().getSelectedNavigationIndex());
		} else
		{
			assignmentAdapter.clear();
			assignmentAdapter.addAll(datasource.getAllAssignmentsWithCourse(getActionBar().getSelectedNavigationIndex(), drawerAdapter.getItem(position)),
					getActionBar().getSelectedNavigationIndex());
		}
	}

	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK)
		{
			datasource.open();
			
			filterAssignments(1);
		}
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id)
	{
		assignmentAdapter.clear();
		assignmentAdapter.addAll(datasource.getAllAssignments(getActionBar().getSelectedNavigationIndex()), getActionBar().getSelectedNavigationIndex());

		return true;
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
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (drawerToggle.onOptionsItemSelected(item))
			return true;

		switch (item.getItemId())
		{
			case R.id.menu_add:
				if(drawerAdapter.getCount() > 2)
				{
					startActivityForResult(new Intent(this, SetAssignmentActivity.class), 1);
				}else
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("No courses");
					builder.setMessage("Before you add an assignment you need to add a course first.");
					builder.setNegativeButton("Oke", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
						}
					});

					builder.show();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		int index = info.position;
		if (index != 0 && index != 1)
			new MenuInflater(this).inflate(R.menu.drawer_context_menu, menu);
		else
			super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		switch (item.getItemId())
		{
			case R.id.drawer_delete:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Are you sure?");
				builder.setMessage("Every assignment that belongs to this course will be deleted.");
				builder.setPositiveButton("Ja", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						Course course = (Course) drawerAdapter.getItem(info.position);
						datasource.deleteCourse(course);
						drawerAdapter.remove(course);
						assignmentAdapter.clear();
						assignmentAdapter
								.addAll(datasource.getAllAssignments(getActionBar().getSelectedNavigationIndex()), getActionBar().getSelectedNavigationIndex());
					}
				});
				builder.setNegativeButton("Nee", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
					}
				});

				builder.show();
				return true;
			case R.id.drawer_edit:
				Course course = (Course) drawerAdapter.getItem(info.position);
				setInputAlert(true, "Edit", "Edit your course here", "OK", course.getCourse(), course);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	public boolean performActions(MenuItem item)
	{
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
					Assignment assignment = (Assignment) assignmentAdapter.getItem(position);
					datasource.deleteAssignment(assignment);
					assignmentAdapter.remove(assignment);
				}

				getListView().clearChoices();

				closeContextMenu();
				return (true);
		}
		return (false);
	}
}