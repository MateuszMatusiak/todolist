package com.krab.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

	private final Context context;
	private static final String DATABASE_NAME = "TodoList.db";
	private static final int DATABASE_VERSION = 1;

	private static final String TABLE_NAME = "tasks";
	private static final String COLUMN_ID = "_id";
	private static final String COLUMN_TITLE = "title";
	private static final String COLUMN_DESCRIPTION = "description";
	private static final String COLUMN_CREATION_TIME = "creation_time";
	private static final String COLUMN_EXECUTION_TIME = "execution_time";
	private static final String COLUMN_STATUS = "status";
	private static final String COLUMN_NOTIFICATION = "notification";
	private static final String COLUMN_CATEGORY = "category";
	private static final String COLUMN_FILE = "file";

	public DatabaseHandler(@Nullable Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String query = "CREATE TABLE " + TABLE_NAME +
				" (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				COLUMN_TITLE + " TEXT, " +
				COLUMN_DESCRIPTION + " TEXT, " +
				COLUMN_CREATION_TIME + " TEXT, " +
				COLUMN_EXECUTION_TIME + " TEXT, " +
				COLUMN_STATUS + " INTEGER, " +
				COLUMN_NOTIFICATION + " INTEGER, " +
				COLUMN_CATEGORY + " TEXT, " +
				COLUMN_FILE + " TEXT);";
		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	public void addTask(Entry entry) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_TITLE, entry.title);
		cv.put(COLUMN_DESCRIPTION, entry.description);
		cv.put(COLUMN_CREATION_TIME, entry.getCreationTimeInMillis());
		cv.put(COLUMN_EXECUTION_TIME, entry.getExecutionTimeInMillis());
		cv.put(COLUMN_STATUS, entry.status);
		cv.put(COLUMN_NOTIFICATION, entry.notification);
		cv.put(COLUMN_CATEGORY, entry.category);
		cv.put(COLUMN_FILE, entry.file);

		long res = db.insert(TABLE_NAME, null, cv);
		if (res == -1) {
			Toast.makeText(context, "Dodawanie nie powiodło się", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "Dodano wpis", Toast.LENGTH_SHORT).show();
		}
	}

	public static String arrayToSQL(String[] arr) {
		StringBuilder res = new StringBuilder("(");
		int size = arr.length;
		for (int i = 0; i < size; i++) {
			res.append("'");
			res.append(arr[i]);
			res.append("'");
			if (i < size - 1)
				res.append(", ");
		}
		res.append(")");
		return res.toString();
	}

	public Cursor readAllData() {
		String categories = arrayToSQL(CategoriesDB.getActiveCategoriesFromDBasStringArr(context));
		boolean hide = Options.getInstance(context).isHideCompleted();

		String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CATEGORY + " IN " + categories;
		if (hide) {
			query += " AND " + COLUMN_STATUS + " = 0";
		}
		query += ";";

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = null;
		if (db != null) {
			cursor = db.rawQuery(query, null);
		}
		return cursor;
	}

	public void updateData(Entry entry) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_TITLE, entry.title);
		cv.put(COLUMN_DESCRIPTION, entry.description);
		cv.put(COLUMN_EXECUTION_TIME, entry.getExecutionTimeInMillis());
		cv.put(COLUMN_STATUS, entry.status);
		cv.put(COLUMN_NOTIFICATION, entry.notification);
		cv.put(COLUMN_CATEGORY, entry.category);
		cv.put(COLUMN_FILE, entry.file);

		long res = db.update(TABLE_NAME, cv, "_id=?", new String[]{String.valueOf(entry.id)});
		if (res == -1) {
			Toast.makeText(context, "Aktualizacja nie powiodła się", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "Zaktualizowano wpis", Toast.LENGTH_SHORT).show();
		}
	}

	public void deleteElement(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		long res = db.delete(TABLE_NAME, "_id=?", new String[]{id + ""});
		if (res == -1) {
			Toast.makeText(context, "Usunięcie nie powiodło się", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "Usunięto wpis", Toast.LENGTH_SHORT).show();

		}
	}

	void deleteAllElements() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("delete from tasks;");
	}

}
