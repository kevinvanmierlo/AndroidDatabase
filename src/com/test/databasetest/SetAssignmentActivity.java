package com.test.databasetest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import com.test.databasetest.database.DataSource;
import com.test.databasetest.holders.Course;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

public class SetAssignmentActivity extends Activity {
	private EditText edit2;
	private DatePicker datePicker1;
	private Spinner spinner1;
	private DataSource datasource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setassignment);
		
		datasource = new DataSource(this);
		datasource.open();
		
		linkUI();
		fillUI();
	}
	
	private void linkUI()
	{
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		edit2 = (EditText) findViewById(R.id.editText2);
		datePicker1 = (DatePicker) findViewById(R.id.datePicker1);
	}
	
	private void fillUI()
	{
		List<Course> courses = datasource.getAllCourses();
		ArrayAdapter<Course> spinnerArrayAdapter = new ArrayAdapter<Course>(this, android.R.layout.simple_spinner_dropdown_item, courses);
		spinner1.setAdapter(spinnerArrayAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu_create_assignment, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_accept:
				Calendar calendar = new GregorianCalendar(datePicker1.getYear(), datePicker1.getMonth(), datePicker1.getDayOfMonth());
				Date date = calendar.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM", Locale.getDefault());
				String dateString = sdf.format(date);
				
				datasource.createAssignment(((Course)spinner1.getSelectedItem()).getId(), edit2.getText().toString(), dateString);
				
				Intent resultIntent = new Intent();
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
				return true;
			case R.id.menu_cancel:
				onBackPressed();
			default:
				return super.onOptionsItemSelected(item);
		}
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
}
