package com.krab.todolist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class AddActivity extends AppCompatActivity {

	Switch notifications;
	Spinner categorySpinner;
	EditText titleInput;
	EditText descriptionInput;
	Button addAttachment;
	Button saveButton;
	Button timePick;
	Button datePick;
	TextView executionTimeField;
	TextView fileName;
	Button openAttachment;
	Button deleteAttachment;

	Date executionTime;
	int isNotifications = 0;
	String actualCategory = "";
	int hour, minute, year, month, day;
	String file = "";

	private static final int CHOOSE_FILE = 1001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		Calendar calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		executionTime = new Date(0);
		setExecutionDate(year, month, day);
		setExecutionTime(hour, minute);

		titleInput = findViewById(R.id.titleInput);
		descriptionInput = findViewById(R.id.descriptionInput);
		addAttachment = findViewById(R.id.addAttachment);
		saveButton = findViewById(R.id.saveButton);
		saveButton.setOnClickListener(v -> onSave());
		notifications = findViewById(R.id.notifications);
		timePick = findViewById(R.id.timePick);
		datePick = findViewById(R.id.datePick);
		fileName = findViewById(R.id.fileName);
		openAttachment = findViewById(R.id.openAttachment);
		deleteAttachment = findViewById(R.id.deleteAttachment);

		fileName.setText("");
		executionTimeField = findViewById(R.id.executionTime);
		executionTimeField.setText(executionTime.toString());
		datePick.setOnClickListener(v -> {
			DatePickerDialog.OnDateSetListener dateSetListener = (view, selectedYear, selectedMonth, dayOfMonth) -> {
				year = selectedYear;
				month = selectedMonth;
				day = dayOfMonth;

				setExecutionDate(year, month, day);
				executionTimeField.setText(executionTime.toString());
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
				executionTimeField.setText(executionTime.toString());
			};
			TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);
			timePickerDialog.setTitle("Wybierz godzinę");
			timePickerDialog.show();
		});
		notifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (isChecked) {
				isNotifications = 1;
			} else {
				isNotifications = 0;
			}
		});
		categorySpinner = findViewById(R.id.category);
		String[] categories = CategoriesDB.getCategoriesFromDBasStringArr(AddActivity.this);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
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
				Toast.makeText(AddActivity.this, "Coś poszło nie tak", Toast.LENGTH_SHORT).show();
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

	private void onSave() {
		DatabaseHandler dh = new DatabaseHandler(AddActivity.this);
		Entry entry = new Entry(titleInput.getText().toString(), descriptionInput.getText().toString(), String.valueOf(System.currentTimeMillis()), "0", isNotifications, actualCategory, file);
		entry.setExecutionTime(hour, minute);
		entry.setExecutionDate(year, month, day);
		dh.addTask(entry);
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
}