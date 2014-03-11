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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SetAssignmentActivity extends Activity {
	private EditText edit2;
	private Spinner spinner1;
	private TextView textview1;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
	private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());
	private Date date;
	private DataSource datasource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setassignment);
		
		datasource = new DataSource(this);
		datasource.open();
		
		linkUI();
		fillUI();
		
		date = new Date();
		textview1.setText(dateFormat.format(date));
		
		textview1.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AlertDialog.Builder alert = new AlertDialog.Builder(SetAssignmentActivity.this);

				alert.setTitle("Datum instellen");

				final DatePicker datepickertest = new DatePicker(SetAssignmentActivity.this);
				datepickertest.setSpinnersShown(false);
				alert.setView(datepickertest);

				alert.setPositiveButton("OK", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						Calendar calendar = new GregorianCalendar(datepickertest.getYear(), datepickertest.getMonth(), datepickertest.getDayOfMonth());
						date = calendar.getTime();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
						String dateString = dateFormat.format(date);
						textview1.setText(dateString);
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
		});
	}
	
	private void linkUI()
	{
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		edit2 = (EditText) findViewById(R.id.editText2);
		textview1 = (TextView) findViewById(R.id.textView1);
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
				if(edit2.getText().toString().equals(""))
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("No description");
					builder.setMessage("Please add a description to add this assignment");
					builder.setNegativeButton("Oke", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
						}
					});
					builder.show();
				}else
				{
					datasource.createAssignment(((Course)spinner1.getSelectedItem()).getId(), edit2.getText().toString(), sdf.format(date));
					
					Intent resultIntent = new Intent();
					setResult(Activity.RESULT_OK, resultIntent);
					finish();
				}
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
