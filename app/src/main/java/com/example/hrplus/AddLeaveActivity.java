package com.example.hrplus;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddLeaveActivity extends AppCompatActivity {

    private TextView textEmployeeName;
    private EditText editTextStartDate, editTextEndDate, editTextReason;
    private Button buttonPickStartDate, buttonPickEndDate, buttonSaveLeave, buttonCancel;

    private String empId, empName;
    private DatabaseHelper dbHelper;
    private Calendar calendarStart, calendarEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_leave);

        Intent intent = getIntent();
        empId = intent.getStringExtra("emp_id");
        empName = intent.getStringExtra("emp_name");

        dbHelper = new DatabaseHelper(this);

        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();

        initializeViews();

        textEmployeeName.setText("Add Leave for: " + empName);

        setupClickListeners();
    }

    private void initializeViews() {
        textEmployeeName = findViewById(R.id.textEmployeeName);

        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextEndDate = findViewById(R.id.editTextEndDate);
        editTextReason = findViewById(R.id.editTextReason);

        buttonPickStartDate = findViewById(R.id.buttonPickStartDate);
        buttonPickEndDate = findViewById(R.id.buttonPickEndDate);
        buttonSaveLeave = findViewById(R.id.buttonSaveLeave);
        buttonCancel = findViewById(R.id.buttonCancel);
    }

    private void setupClickListeners() {
        buttonPickStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(true);
            }
        });

        buttonPickEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(false);
            }
        });

        buttonSaveLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLeave();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showDatePicker(final boolean isStartDate) {
        Calendar calendar = isStartDate ? calendarStart : calendarEnd;
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    updateDateInView(isStartDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void updateDateInView(boolean isStartDate) {
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());

        if (isStartDate) {
            editTextStartDate.setText(sdf.format(calendarStart.getTime()));
        } else {
            editTextEndDate.setText(sdf.format(calendarEnd.getTime()));
        }
    }

    private void saveLeave() {
        String startDate = editTextStartDate.getText().toString().trim();
        String endDate = editTextEndDate.getText().toString().trim();
        String reason = editTextReason.getText().toString().trim();

        // Validation
        if (startDate.isEmpty()) {
            editTextStartDate.setError("Start date is required");
            editTextStartDate.requestFocus();
            return;
        }

        if (endDate.isEmpty()) {
            editTextEndDate.setError("End date is required");
            editTextEndDate.requestFocus();
            return;
        }

        if (reason.isEmpty()) {
            editTextReason.setError("Reason is required");
            editTextReason.requestFocus();
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            java.util.Date start = sdf.parse(startDate);
            java.util.Date end = sdf.parse(endDate);

            if (end.before(start)) {
                editTextEndDate.setError("End date must be after start date");
                editTextEndDate.requestFocus();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Save to database
        long id = dbHelper.addLeave(Integer.parseInt(empId), startDate, endDate, reason);

        if (id > 0) {
            Toast.makeText(this, "Leave added successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to add leave", Toast.LENGTH_SHORT).show();
        }
    }
}
