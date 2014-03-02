package com.test.databasetest.holders;

public class Course
{
	private long id;
	private String course;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getCourse(){
		return course;
	}
	
	public void setCourse(String course)
	{
		this.course = course;
	}
	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return course;
	}
}
