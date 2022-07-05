package com.krab.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class SettingsActivity extends AppCompatActivity {

	CheckBox hide;
	TextView estimatedTimeF;
	Button timePick;
	Button deleteButton;
	Button addCategory;
	EditText categoryInput;
	RecyclerView categoriesRec;

	CategoryItemAdapter categoryItemAdapter;
	ArrayList<Category> categories;

	Options options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		options = Options.getInstance(SettingsActivity.this);
		fillArray();

		hide = findViewById(R.id.hide);
		estimatedTimeF = findViewById(R.id.estimatedTime);
		timePick = findViewById(R.id.timePick3);
		deleteButton = findViewById(R.id.deleteButton2);
		categoryInput = findViewById(R.id.categoryInput);
		addCategory = findViewById(R.id.addCategoryBTN);
		categoriesRec = findViewById(R.id.categoriesRec);
		hide.setChecked(options.isHideCompleted());

		estimatedTimeF.setText(getTime());
		timePick.setOnClickListener(v -> {
			TimePickerDialog.OnTimeSetListener onTimeSetListener = (view, hourOfDay, selectedMinute) -> {
				options.setHours(hourOfDay);
				options.setMinutes(selectedMinute);

				estimatedTimeF.setText(getTime());
			};
			TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, options.getHours(), options.getMinutes(), true);
			timePickerDialog.setTitle("Wybierz czas");
			timePickerDialog.show();
		});

		categoryItemAdapter = new CategoryItemAdapter(SettingsActivity.this, categories);
		categoriesRec.setAdapter(categoryItemAdapter);
		categoriesRec.setLayoutManager(new LinearLayoutManager(SettingsActivity.this));

		addCategory.setOnClickListener(v -> {
			CategoriesDB dh = new CategoriesDB(SettingsActivity.this);
			String cat = categoryInput.getText().toString();
			if (!categories.contains(cat)) {
				Category category = new Category(0, cat, true);
				dh.addCategory(category);
				categories.add(category);
				reload();
			}
		});

		deleteButton.setOnClickListener(v -> {
			onDelete();
		});
		hide.setOnCheckedChangeListener((buttonView, isChecked) -> {
			options.setHideCompleted(isChecked);
		});
	}

	private void fillArray() {
		CategoriesDB categoriesDB = new CategoriesDB(SettingsActivity.this);
		categories = categoriesDB.categories;
	}

	private String getTime() {
		return ((options.getHours() == 0) ? "00" : options.getHours()) + ":" + ((options.getMinutes() == 0) ? "00" : options.getMinutes());
	}

	private void onDelete() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Usuwanie wszystkich elementów ");
		builder.setMessage("Czy na pewno chcesz usunąć wszystkie elementy?\nTa operacja jest nieodwracalna.");
		builder.setPositiveButton("Tak", (dialog, which) -> {
			DatabaseHandler db = new DatabaseHandler(SettingsActivity.this);
			db.deleteAllElements();
		});
		builder.setNegativeButton("Nie", (dialog, which) -> {

		});
		builder.create().show();
	}

	public void reload() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
		finish();
	}
}