package com.krab.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;

public class CategoriesDB extends SQLiteOpenHelper {

	private final Context context;
	private static final String DATABASE_NAME = "Categories.db";
	private static final int DATABASE_VERSION = 1;

	private static final String TABLE_NAME = "categories";
	private static final String COLUMN_ID = "_id";
	private static final String COLUMN_NAME = "title";
	private static final String COLUMN_STATUS = "status";

	ArrayList<Category> categories;

	public CategoriesDB(@Nullable Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
		this.categories = new ArrayList<>();
		readAllData();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String query = "CREATE TABLE " + TABLE_NAME +
				" (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				COLUMN_NAME + " TEXT, " +
				COLUMN_STATUS + " INTEGER);";
		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	public void addCategory(Category category) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();

		cv.put(COLUMN_NAME, category.name);
		cv.put(COLUMN_STATUS, category.getStatus());

		long res = db.insert(TABLE_NAME, null, cv);
		if (res == -1) {
			Toast.makeText(context, "Dodawanie nie powiodło się", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "Dodano wpis", Toast.LENGTH_SHORT).show();
		}
	}

	void cl(){
		SQLiteDatabase db = this.getWritableDatabase();
		String queru = "DROP TABLE " + TABLE_NAME;
		db.execSQL(queru);
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
				Category category = new Category(cursor.getInt(0), cursor.getString(1), cursor.getInt(2) == 1);
				categories.add(category);
			}
			Collections.sort(categories);
		}
	}

	public void updateData(Category category) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_NAME, category.name);
		cv.put(COLUMN_STATUS, category.getStatus());

		long res = db.update(TABLE_NAME, cv, "_id=?", new String[]{String.valueOf(category.id)});
		if (res == -1) {
			Toast.makeText(context, "Aktualizacja nie powiodła się", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "Zaktualizowano wpis", Toast.LENGTH_SHORT).show();
		}
	}

	public void deleteElement(String name) {
		SQLiteDatabase db = this.getWritableDatabase();
		long res = db.delete(TABLE_NAME, "title=?", new String[]{name});
		if (res == -1) {
			Toast.makeText(context, "Usunięcie nie powiodło się", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "Usunięto wpis", Toast.LENGTH_SHORT).show();

		}
	}

	public static String[] getCategoriesFromDBasStringArr(Context context) {
		CategoriesDB db = new CategoriesDB(context);
		ArrayList<Category> cat = db.categories;
		int size = cat.size();
		String[] res = new String[size];
		for (int i = 0; i < size; ++i) {
			res[i] = cat.get(i).name;
		}
		return res;
	}

	public static String[] getActiveCategoriesFromDBasStringArr(Context context) {
		CategoriesDB db = new CategoriesDB(context);
		ArrayList<Category> cat = db.categories;
		ArrayList<String> res = new ArrayList<>();

		for (Category c : cat) {
			if(c.checked){
				res.add(c.name);
			}
		}
		return res.toArray(new String[0]);
	}
}
