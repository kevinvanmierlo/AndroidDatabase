package com.test.databasetest.holders;

public class Assignment
{
	private long id;
	private long courseId;
	private String assignment;
	private String deadline;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public long getCourse(){
		return courseId;
	}
	
	public void setCourse(long courseId)
	{
		this.courseId = courseId;
	}

	public String getAssignment() {
		return assignment;
	}

	public void setAssignment(String assignment) {
		this.assignment = assignment;
	}
	
	public String getDeadline()
	{
		return deadline;
	}
	
	public void setDeadline(String deadline)
	{
		this.deadline = deadline;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return courseId + ", " + assignment + ", " + deadline;
	}
}
