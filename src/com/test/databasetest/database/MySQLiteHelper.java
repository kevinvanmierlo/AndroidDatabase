package com.test.databasetest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper
{
	// Assignments
	public static final String TABLE_ASSIGNMENTS = "assignments";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_ASSIGNMENT = "assignment";
	public static final String COLUMN_DEADLINE = "deadline";
	
	// Courses
	public static final String TABLE_COURSES = "courses";
	// Also uses COLUMN_COURSE
	public static final String COURSE_COLUMN_ID = "courseId";
	public static final String COLUMN_COURSE = "course";

	private static final String DATABASE_NAME = "assignments.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE_COURSES =
			"CREATE TABLE " + TABLE_COURSES + 
			"(" + 
				COURSE_COLUMN_ID + " integer primary key autoincrement, " + 
				COLUMN_COURSE + " text not null" + 
			");";
	
	private static final String DATABASE_CREATE_ASSIGNMENTS = 
	"CREATE TABLE " + TABLE_ASSIGNMENTS + 
	"(" + 
		COLUMN_ID + " integer primary key autoincrement, " +  
		COLUMN_ASSIGNMENT + " text not null, " + 
		COLUMN_DEADLINE + " text not null, " +
		COURSE_COLUMN_ID + " integer, " +
		"FOREIGN KEY(" + COURSE_COLUMN_ID + ") REFERENCES " + TABLE_COURSES + "(" + COURSE_COLUMN_ID + ") ON DELETE CASCADE" +
	");";
	
	public MySQLiteHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database)
	{
		System.out.println("database : " + DATABASE_CREATE_COURSES + "\n " + DATABASE_CREATE_ASSIGNMENTS);
		database.execSQL(DATABASE_CREATE_COURSES);
		database.execSQL(DATABASE_CREATE_ASSIGNMENTS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSIGNMENTS + ", " + TABLE_COURSES);
		onCreate(db);
	}
}