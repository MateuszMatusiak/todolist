package com.krab.todolist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class UpdateActivity extends AppCompatActivity {

	Switch notifications;
	Spinner categorySpinner;
	EditText titleInput;
	EditText descriptionInput;
	Button addAttachment;
	Button updateButton;
	Button timePick;
	Button datePick;
	TextView executionTimeField;
	Button deleteButton;
	CheckBox completed;
	TextView fileName;
	Button openAttachment;
	Button deleteAttachment;

	int isNotifications = 0;
	String actualCategory = "";

	String title, description;
	int id, status;
	ArrayAdapter<String> adapter;
	String executionTime = "0";
	int hour, minute, year, month, day;
	String file = "";

	private static final int CHOOSE_FILE = 1001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);

		titleInput = findViewById(R.id.titleInput2);
		descriptionInput = findViewById(R.id.descriptionInput2);
		addAttachment = findViewById(R.id.addAttachment2);
		updateButton = findViewById(R.id.updateButton);
		notifications = findViewById(R.id.notifications2);
		categorySpinner = findViewById(R.id.category2);
		timePick = findViewById(R.id.timePick2);
		datePick = findViewById(R.id.datePick2);
		executionTimeField = findViewById(R.id.executionTime2);
		deleteButton = findViewById(R.id.deleteButton);
		completed = findViewById(R.id.completed);
		fileName = findViewById(R.id.fileName2);
		openAttachment = findViewById(R.id.openAttachment2);
		deleteAttachment = findViewById(R.id.deleteAttachment2);

		fileName.setText("");

		status = completed.isChecked() ? 1 : 0;

		String[] categories = CategoriesDB.getCategoriesFromDBasStringArr(UpdateActivity.this);
		adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
		categorySpinner.setAdapter(adapter);
		categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				actualCategory = parent.getItemAtPosition(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				actualCategory = "";
			}
		});
		getIntentData();
		executionTimeField.setText((new Date(Long.parseLong(executionTime))).toString());
		datePick.setOnClickListener(v -> {
			DatePickerDialog.OnDateSetListener dateSetListener = (view, selectedYear, selectedMonth, dayOfMonth) -> {
				year = selectedYear;
				month = selectedMonth;
				day = dayOfMonth;

				setExecutionDate(year, month, day);
				executionTimeField.setText(new Date(Long.parseLong(executionTime)).toString());
			};

			DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
			datePickerDialog.setTitle("Wybierz dzień");
			datePickerDialog.show();
		});
		timePick.setOnClickListener(v -> {
			TimePickerDialog.OnTimeSetListener onTimeSetListener = (view, hourOfDay, selectedMinute) -> {
				hour = hourOfDay;
				minute = selectedMinute;

				setExecutionTime(hour, minute);
				executionTimeField.setText(new Date(Long.parseLong(executionTime)).toString());

			};
			TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);
			timePickerDialog.setTitle("Wybierz godzinę");
			timePickerDialog.show();
		});

		updateButton.setOnClickListener(v -> onSave());
		notifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (isChecked) {
				isNotifications = 1;
			} else {
				isNotifications = 0;
			}
		});

		deleteButton.setOnClickListener(v -> {
			onDelete();
		});

		completed.setOnCheckedChangeListener((buttonView, isChecked) -> {
			status = isChecked ? 1 : 0;
		});
		addAttachment.setOnClickListener(v -> {
			callChooseFileFromDevice();
		});
		deleteAttachment.setOnClickListener(v -> {
			file = "";
			fileName.setText(file);
			setVisibilityAttach(View.GONE);
		});
		openAttachment.setOnClickListener(v -> {
			Uri selectedUri = Uri.parse(file);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(selectedUri, "*/*");

			if (intent.resolveActivityInfo(getPackageManager(), 0) != null) {
				startActivity(intent);
			} else {
				Toast.makeText(UpdateActivity.this, "Coś poszło nie tak", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void callChooseFileFromDevice() {
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, CHOOSE_FILE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CHOOSE_FILE && resultCode == RESULT_OK) {
			if (data != null) {
				file = data.getData().toString();
				fileName.setText(file);
				setVisibilityAttach(View.VISIBLE);
			}
		}
	}

	private void getIntentData() {
		if (getIntent().hasExtra("title") &&
				getIntent().hasExtra("description") &&
				getIntent().hasExtra("notifications") &&
				getIntent().hasExtra("category") &&
				getIntent().hasExtra("status") &&
				getIntent().hasExtra("executionTime") &&
				getIntent().hasExtra("file") &&
				getIntent().hasExtra("id")
		) {
			title = getIntent().getStringExtra("title");
			description = getIntent().getStringExtra("description");
			isNotifications = getIntent().getIntExtra("notifications", 0);
			id = getIntent().getIntExtra("id", 0);
			status = getIntent().getIntExtra("status", 0);
			actualCategory = getIntent().getStringExtra("category");
			executionTime = getIntent().getStringExtra("executionTime");
			file = getIntent().getStringExtra("file");

			fileName.setText(file);
			if (!file.equals("")) {
				setVisibilityAttach(View.VISIBLE);
			} else {
				setVisibilityAttach(View.GONE);
			}

			fillTime(executionTime);
			titleInput.setText(title);
			descriptionInput.setText(description);
			notifications.setChecked(isNotifications == 1);
			completed.setChecked(status == 1);

			int spinnerPosition = adapter.getPosition(actualCategory);
			categorySpinner.setSelection(spinnerPosition);

		} else {
			Toast.makeText(this, "Brak danych", Toast.LENGTH_SHORT).show();
		}
	}

	private void setVisibilityAttach(int status) {
		fileName.setVisibility(status);
		openAttachment.setVisibility(status);
		deleteAttachment.setVisibility(status);
		if (status == View.VISIBLE) {
			addAttachment.setVisibility(View.GONE);
		} else {
			addAttachment.setVisibility(View.VISIBLE);
		}
	}

	private void fillTime(String time) {
		Date temp = new Date(Long.parseLong(time));
		year = temp.getYear() + 1900;
		month = temp.getMonth();
		day = temp.getDay();
		hour = temp.getHours();
		minute = temp.getMinutes();
	}

	private void onSave() {
		DatabaseHandler db = new DatabaseHandler(UpdateActivity.this);
		db.updateData(new Entry(id, titleInput.getText().toString(), descriptionInput.getText().toString(), "0", executionTime, status, isNotifications, actualCategory, file));
		finish();
	}

	private void onDelete() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Usuwanie " + title);
		builder.setMessage("Czy na pewno chcesz usunąć wydarzenie?");
		builder.setPositiveButton("Tak", (dialog, which) -> {
			DatabaseHandler db = new DatabaseHandler(UpdateActivity.this);
			db.deleteElement(id);
			finish();
		});
		builder.setNegativeButton("Nie", (dialog, which) -> {

		});
		builder.create().show();
	}

	public void setExecutionTime(int hour, int minute) {
		Date temp = new Date(Long.parseLong(executionTime));
		temp.setHours(hour);
		temp.setMinutes(minute);
		executionTime = String.valueOf(temp.getTime());
	}

	public void setExecutionDate(int year, int month, int day) {
		Date temp = new Date(Long.parseLong(executionTime));
		temp.setYear(year - 1900);
		temp.setMonth(month);
		temp.setDate(day);
		executionTime = String.valueOf(temp.getTime());
	}

}