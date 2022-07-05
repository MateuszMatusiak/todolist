package com.krab.todolist;

import java.util.Date;

public class Entry implements Comparable {
	int id;
	String title;
	String description;
	Date creationTime;
	Date executionTime;
	int notification;
	int status;
	String category;
	String file;

	public Entry(String title, String description, String creationTime, String executionTime, int notification, String category, String file) {
		this.title = title;
		this.description = description;
		this.creationTime = new Date(Long.parseLong(creationTime));
		this.executionTime = new Date(Long.parseLong(executionTime));
		this.notification = notification;
		this.category = category;
		this.status = 0;
		this.file = file;
	}

	public Entry(int id, String title, String description, String creationTime, String executionTime, int status, int notification, String category, String file) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.creationTime = new Date(Long.parseLong(creationTime));
		this.executionTime = new Date(Long.parseLong(executionTime));
		this.notification = notification;
		this.category = category;
		this.status = status;
		this.file = file;
	}

	public void changeStatus() {
		status = status == 0 ? 1 : 0;
	}

	public String getCreationTimeInMillis() {
		return String.valueOf(creationTime.getTime());
	}

	public String getCreationTime() {
		return creationTime.toString();
	}

	public String getExecutionTimeInMillis() {
		return String.valueOf(executionTime.getTime());
	}

	public String getExecutionTime() {
		return executionTime.toString();
	}

	public void setExecutionTime(int hour, int minute) {
		executionTime.setHours(hour);
		executionTime.setMinutes(minute);
	}

	public void setExecutionDate(int year, int month, int day) {
		executionTime.setYear(year - 1900);
		executionTime.setMonth(month);
		executionTime.setDate(day);
	}

	@Override
	public int compareTo(Object o) {
		int res = 0;
		if (executionTime.getTime() > ((Entry) o).executionTime.getTime())
			res = -1;
		else if (executionTime.getTime() < ((Entry) o).executionTime.getTime()) {
			res = 1;
		} else {
			res = (id < ((Entry) o).id) ? -1 : 1;
		}
		return res;
	}
}
