package com.example.hrplus;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddEmployeeActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPhone, editTextDepartment;
    private Button buttonSave, buttonCancel;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextDepartment = findViewById(R.id.editTextDepartment);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEmployee();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveEmployee() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String department = editTextDepartment.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            editTextPhone.setError("Phone is required");
            editTextPhone.requestFocus();
            return;
        }

        if (department.isEmpty()) {
            editTextDepartment.setError("Department is required");
            editTextDepartment.requestFocus();
            return;
        }

        long id = dbHelper.addEmployee(name, email, phone, department);

        if (id > 0) {
            Toast.makeText(this, "Employee added successfully!", Toast.LENGTH_SHORT).show();
            clearForm();
        } else {
            Toast.makeText(this, "Failed to add employee", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        editTextName.setText("");
        editTextEmail.setText("");
        editTextPhone.setText("");
        editTextDepartment.setText("");
        editTextName.requestFocus();
    }
}