package com.krab.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Collections;

public class Options extends SQLiteOpenHelper {

	private boolean hideCompleted = false;
	private Integer hours = 0;
	private Integer minutes = 0;
	private static volatile Options instance;

	private final Context context;
	private static final String DATABASE_NAME = "Settings.db";
	private static final int DATABASE_VERSION = 1;

	private static final String TABLE_NAME = "settings";
	private static final String COLUMN_ID = "_id";
	private static final String COLUMN_HIDE = "hide";
	private static final String COLUMN_HOURS = "hours";
	private static final String COLUMN_MINUTES = "minutes";

	public Options(@Nullable Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	public static Options getInstance(Context context) {
		Options result = instance;
		if (result != null) {
			instance.readAllData();
			return result;
		}
		synchronized (Options.class) {
			if (instance == null) {
				instance = new Options(context);
				instance.readAllData();
			}
			return instance;
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String query = "CREATE TABLE " + TABLE_NAME +
				" (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				COLUMN_HIDE + " INTEGER, " +
				COLUMN_HOURS + " INTEGER, " +
				COLUMN_MINUTES + " INTEGER);";
		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	private void saveOptions() {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();

		db.execSQL("delete from settings;");
		cv.put(COLUMN_HIDE, !hideCompleted ? 0 : 1);
		cv.put(COLUMN_HOURS, hours);
		cv.put(COLUMN_MINUTES, minutes);
		long res = db.insert(TABLE_NAME, null, cv);
		if (res == -1) {
			Toast.makeText(context, "Dodawanie nie powiodło się", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "Dodano wpis", Toast.LENGTH_SHORT).show();
		}
	}

	public void readAllData() {
		String query = "SELECT * FROM " + TABLE_NAME;
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = null;
		if (db != null) {
			cursor = db.rawQuery(query, null);
		}
		if (cursor.getCount() != 0) {
			while (cursor.moveToNext()) {
				hideCompleted = cursor.getInt(1) != 0;
				hours = cursor.getInt(2);
				minutes = cursor.getInt(3);
			}
		}
	}

	public boolean isHideCompleted() {
		return hideCompleted;
	}

	public void setHideCompleted(boolean hideCompleted) {
		this.hideCompleted = hideCompleted;
		saveOptions();
	}

	public int getHours() {
		return hours;
	}

	public void setHours(int hours) {
		this.hours = hours;
		saveOptions();

	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
		saveOptions();

	}

}
