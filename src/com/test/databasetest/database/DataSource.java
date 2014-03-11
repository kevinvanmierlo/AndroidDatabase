package com.test.databasetest.database;

import java.util.ArrayList;
import java.util.List;

import com.test.databasetest.holders.Assignment;
import com.test.databasetest.holders.Course;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataSource
{
	public static final int ORDER_BY_COURSE = 1;
	public static final int ORDER_BY_DATE = 0;
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] assignmentAllColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_ASSIGNMENT, MySQLiteHelper.COLUMN_DEADLINE,
			MySQLiteHelper.COURSE_COLUMN_ID };
	private String[] coursesAllColumns = { MySQLiteHelper.COURSE_COLUMN_ID, MySQLiteHelper.COLUMN_COURSE };

	public DataSource(Context context)
	{
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException
	{
		database = dbHelper.getWritableDatabase();
	}

	public void close()
	{
		dbHelper.close();
	}

	public void createAssignment(long courseId, String assignment, String deadline)
	{
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COURSE_COLUMN_ID, courseId);
		values.put(MySQLiteHelper.COLUMN_ASSIGNMENT, assignment);
		values.put(MySQLiteHelper.COLUMN_DEADLINE, deadline);
		System.out.println(deadline);
		/*long insertId = */database.insert(MySQLiteHelper.TABLE_ASSIGNMENTS, null, values);
		/*Cursor cursor = database.query(MySQLiteHelper.TABLE_ASSIGNMENTS, assignmentAllColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null,
				null);
		cursor.moveToFirst();
		Assignment newAssignment = cursorToAssignment(cursor);
		cursor.close();*/
		//return newAssignment;
	}

	public void deleteAssignment(Assignment assignment)
	{
		long id = assignment.getId();
		System.out.println("Comment deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_ASSIGNMENTS, MySQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	public List<Assignment> getAllAssignments(int orderBy)
	{
		List<Assignment> assignments = new ArrayList<Assignment>();

		Cursor cursor = database.rawQuery("SELECT assignments.*, courses." + MySQLiteHelper.COLUMN_COURSE + 
				" FROM " + MySQLiteHelper.TABLE_ASSIGNMENTS + " assignments" + 
				" INNER JOIN " + MySQLiteHelper.TABLE_COURSES + " courses" +
				" ON assignments." + MySQLiteHelper.COURSE_COLUMN_ID + " = courses." + MySQLiteHelper.COURSE_COLUMN_ID +
				((orderBy == ORDER_BY_COURSE) ? " ORDER BY courses." + MySQLiteHelper.COLUMN_COURSE + ", assignments." + MySQLiteHelper.COLUMN_DEADLINE : 
					" ORDER BY assignments." + MySQLiteHelper.COLUMN_DEADLINE + ", courses." + MySQLiteHelper.COLUMN_COURSE), null);
//		Cursor cursor = database.query(MySQLiteHelper.TABLE_ASSIGNMENTS, assignmentAllColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			Assignment assignment = cursorToAssignment(cursor);
			assignments.add(assignment);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return assignments;
	}
	
	public List<Assignment> getAllAssignmentsWithCourse(int orderBy, Course course)
	{
		List<Assignment> assignments = new ArrayList<Assignment>();

		Cursor cursor = database.rawQuery("SELECT assignments.*, courses." + MySQLiteHelper.COLUMN_COURSE + 
				" FROM " + MySQLiteHelper.TABLE_ASSIGNMENTS + " assignments" + 
				" INNER JOIN " + MySQLiteHelper.TABLE_COURSES + " courses" +
				" ON assignments." + MySQLiteHelper.COURSE_COLUMN_ID + " = courses." + MySQLiteHelper.COURSE_COLUMN_ID +
				" WHERE courses." + MySQLiteHelper.COURSE_COLUMN_ID + " = " + course.getId() +
				((orderBy == ORDER_BY_COURSE) ? " ORDER BY courses." + MySQLiteHelper.COLUMN_COURSE + ", assignments." + MySQLiteHelper.COLUMN_DEADLINE : 
					" ORDER BY assignments." + MySQLiteHelper.COLUMN_DEADLINE + ", courses." + MySQLiteHelper.COLUMN_COURSE), null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			Assignment assignment = cursorToAssignment(cursor);
			assignments.add(assignment);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return assignments;
	}

	private Assignment cursorToAssignment(Cursor cursor)
	{
		for(int i = 0 ; i < cursor.getColumnNames().length; i++)
		{
			System.out.println(cursor.getColumnNames()[i]);
		}
		Assignment assignment = new Assignment();
		assignment.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_ID)));
		assignment.setCourseID(cursor.getLong(cursor.getColumnIndexOrThrow(MySQLiteHelper.COURSE_COLUMN_ID)));
		assignment.setCourse(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_COURSE)));
		assignment.setAssignment(cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_ASSIGNMENT)));
		assignment.setDeadline(cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_DEADLINE)));
		return assignment;
	}

	public Course createCourse(String course)
	{
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_COURSE, course);
		long insertId = database.insert(MySQLiteHelper.TABLE_COURSES, null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_COURSES, coursesAllColumns, MySQLiteHelper.COURSE_COLUMN_ID + " = " + insertId, null, null, null,
				null);
		cursor.moveToFirst();
		Course newCourse = cursorToCourse(cursor);
		cursor.close();
		return newCourse;
	}

	public void deleteCourse(Course course)
	{
		database.setForeignKeyConstraintsEnabled(true);
		System.out.println("Course deleted with name: " + course.getCourse());
		database.delete(MySQLiteHelper.TABLE_COURSES, MySQLiteHelper.COURSE_COLUMN_ID + " = " + course.getId(), null);
	}
	
	public void updateCourse(Course course)
	{
		ContentValues args = new ContentValues();
		args.put(MySQLiteHelper.COLUMN_COURSE, course.getCourse());
		database.update(MySQLiteHelper.TABLE_COURSES, args, MySQLiteHelper.COURSE_COLUMN_ID + "=?", new String[] { Long.toString(course.getId()) });
	}

	public List<Course> getAllCourses()
	{
		List<Course> courses = new ArrayList<Course>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_COURSES, coursesAllColumns, null, null, null, null, MySQLiteHelper.COLUMN_COURSE);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			Course course = cursorToCourse(cursor);
			courses.add(course);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return courses;
	}
	
	public Course getCourse(long columnId)
	{
		Cursor cursor = database.query(MySQLiteHelper.TABLE_COURSES, coursesAllColumns, MySQLiteHelper.COURSE_COLUMN_ID + " = " + columnId, null, null, null,
				null);

		cursor.moveToFirst();
		Course course = cursorToCourse(cursor);
		cursor.close();
		return course;
	}

	private Course cursorToCourse(Cursor cursor)
	{
		Course course = new Course();
		course.setId(cursor.getLong(0));
		course.setCourse(cursor.getString(1));
		return course;
	}
}
