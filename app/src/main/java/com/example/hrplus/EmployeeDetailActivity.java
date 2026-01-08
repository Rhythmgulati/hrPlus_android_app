package com.example.hrplus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;

public class EmployeeDetailActivity extends AppCompatActivity {

    private TextView textEmployeeName, textEmployeeEmail, textEmployeePhone, textEmployeeDept;
    private TextView textTotalLeaves, textPendingLeaves, textApprovedLeaves, textRejectedLeaves;
    private ListView listViewEmployeeLeaves;
    private Button buttonAddLeave, buttonBack;

    private String empId, empName, empEmail, empPhone, empDept;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_detail);

        // Get employee data from intent
        Intent intent = getIntent();
        empId = intent.getStringExtra("emp_id");
        empName = intent.getStringExtra("emp_name");
        empEmail = intent.getStringExtra("emp_email");
        empPhone = intent.getStringExtra("emp_phone");
        empDept = intent.getStringExtra("emp_dept");

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        initializeViews();

        // Set employee data
        setEmployeeData();

        // Load employee leaves
        loadEmployeeLeaves();

        // Set click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        textEmployeeName = findViewById(R.id.textEmployeeName);
        textEmployeeEmail = findViewById(R.id.textEmployeeEmail);
        textEmployeePhone = findViewById(R.id.textEmployeePhone);
        textEmployeeDept = findViewById(R.id.textEmployeeDept);

        textTotalLeaves = findViewById(R.id.textTotalLeaves);
        textPendingLeaves = findViewById(R.id.textPendingLeaves);
        textApprovedLeaves = findViewById(R.id.textApprovedLeaves);
        textRejectedLeaves = findViewById(R.id.textRejectedLeaves);

        listViewEmployeeLeaves = findViewById(R.id.listViewEmployeeLeaves);
        buttonAddLeave = findViewById(R.id.buttonAddLeave);
        buttonBack = findViewById(R.id.buttonBack);
    }

    private void setEmployeeData() {
        textEmployeeName.setText(empName);
        textEmployeeEmail.setText("Email: " + empEmail);
        textEmployeePhone.setText("Phone: " + empPhone);
        textEmployeeDept.setText("Department: " + empDept);
    }

    private void loadEmployeeLeaves() {
        // Get leave stats for this employee
        HashMap<String, Integer> leaveStats = dbHelper.getEmployeeLeaveStats(Integer.parseInt(empId));

        if (leaveStats != null) {
            textTotalLeaves.setText(String.valueOf(leaveStats.get("total")));
            textPendingLeaves.setText(String.valueOf(leaveStats.get("pending")));
            textApprovedLeaves.setText(String.valueOf(leaveStats.get("approved")));
            textRejectedLeaves.setText(String.valueOf(leaveStats.get("rejected")));
        }

        // Get leaves for this employee
        ArrayList<HashMap<String, String>> leaves = dbHelper.getLeavesByEmployee(Integer.parseInt(empId));

        if (leaves.size() > 0) {
            // Create a simple array of leave details
            ArrayList<String> leaveDetails = new ArrayList<>();
            for (HashMap<String, String> leave : leaves) {
                String status = leave.get("status");
                String statusIcon = status.equals("pending") ? "⏳" :
                        status.equals("approved") ? "✅" : "❌";

                String detail = statusIcon + " " + leave.get("start_date") + " to " + leave.get("end_date") + "\n" +
                        "📝 " + leave.get("reason") + "\n" +
                        "Status: " + status;
                leaveDetails.add(detail);
            }

            // Set adapter
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    leaveDetails
            );
            listViewEmployeeLeaves.setAdapter(adapter);
        } else {
            // Show empty message in list
            ArrayList<String> emptyMessage = new ArrayList<>();
            emptyMessage.add("No leave records found");

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    emptyMessage
            );
            listViewEmployeeLeaves.setAdapter(adapter);
        }
    }

    private void setupClickListeners() {
        // Add Leave Button
        buttonAddLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployeeDetailActivity.this, AddLeaveActivity.class);
                intent.putExtra("emp_id", empId);
                intent.putExtra("emp_name", empName);
                startActivity(intent);
            }
        });

        // Back Button
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Leave item click
        listViewEmployeeLeaves.setOnItemClickListener((parent, view, position, id) -> {
            ArrayList<HashMap<String, String>> leaves = dbHelper.getLeavesByEmployee(Integer.parseInt(empId));
            if (position < leaves.size()) {
                HashMap<String, String> leave = leaves.get(position);
                String leaveDetails = "Employee: " + empName + "\n" +
                        "Start Date: " + leave.get("start_date") + "\n" +
                        "End Date: " + leave.get("end_date") + "\n" +
                        "Reason: " + leave.get("reason") + "\n" +
                        "Status: " + leave.get("status");

                new android.app.AlertDialog.Builder(EmployeeDetailActivity.this)
                        .setTitle("Leave Details")
                        .setMessage(leaveDetails)
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh leaves when returning
        loadEmployeeLeaves();
    }
}