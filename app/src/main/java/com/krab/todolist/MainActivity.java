package com.krab.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.krab.todolist.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

	RecyclerView mainList;
	FloatingActionButton addTask;
	DatabaseHandler db;
	ArrayList<Entry> taskList;
	TaskItemAdapter taskItemAdapter;
	ActivityMainBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		mainList = findViewById(R.id.mainList);
		addTask = findViewById(R.id.addTask);
		addTask.setOnClickListener(v -> {
			Intent intent = new Intent(MainActivity.this, AddActivity.class);
			startActivity(intent);
		});

		db = new DatabaseHandler(MainActivity.this);
		taskList = new ArrayList<>();

		fillTaskList();

		taskItemAdapter = new TaskItemAdapter(MainActivity.this, taskList);
		mainList.setAdapter(taskItemAdapter);
		mainList.setLayoutManager(new LinearLayoutManager(MainActivity.this));

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			reload();
		}
	}

	private void fillTaskList() {
		Cursor cursor = db.readAllData();
		if (cursor.getCount() == 0) {
			Toast.makeText(this, "Brak zadań", Toast.LENGTH_SHORT).show();
		} else {
			while (cursor.moveToNext()) {
				taskList.add(new Entry(cursor.getInt(0), //id
						cursor.getString(1), //title
						cursor.getString(2), //description
						cursor.getString(3), //creation
						cursor.getString(4), //execution
						cursor.getInt(5), //status
						cursor.getInt(6), //notifications
						cursor.getString(7), //category
						cursor.getString(8))); //file
			}
			Collections.sort(taskList);
			Collections.reverse(taskList);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.my_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if(item.getItemId()==R.id.settings){
			Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	public void reload() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}