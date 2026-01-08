package com.example.hrplus; // Make sure this matches your package name

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private CardView cardEmployees, cardAddEmployee, cardLeaveManagement;
    private TextView textTotalEmployees, textPendingLeaves, textApprovedLeaves, textRejectedLeaves, textWelcome, textDate;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        initializeViews();

        // Update stats
        updateStats();

        // Set click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        // Text views - IMPORTANT: Check if these IDs exist in your layout!
        textTotalEmployees = findViewById(R.id.textTotalEmployees);
        textPendingLeaves = findViewById(R.id.textPendingLeaves);

        // These might not exist in your layout - add them or remove from code
        textApprovedLeaves = findViewById(R.id.textApprovedLeaves); // Check if this exists
        textRejectedLeaves = findViewById(R.id.textRejectedLeaves); // Check if this exists

        textWelcome = findViewById(R.id.textWelcome);
        textDate = findViewById(R.id.textDate);

        // Cards
        cardEmployees = findViewById(R.id.cardEmployees);
        cardAddEmployee = findViewById(R.id.cardAddEmployee);
        cardLeaveManagement = findViewById(R.id.cardLeaveManagement);

        // Set welcome message
        textWelcome.setText("Welcome, HR Admin");

        // Set current date
        String currentDate = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(new Date());
        textDate.setText(currentDate);
    }

    private void updateStats() {
        // Get stats from database
        int totalEmployees = dbHelper.getTotalEmployees();
        int pendingLeaves = dbHelper.getPendingLeavesCount();

        // Update UI - only update views that exist
        if (textTotalEmployees != null) {
            textTotalEmployees.setText(String.valueOf(totalEmployees));
        }

        if (textPendingLeaves != null) {
            textPendingLeaves.setText(String.valueOf(pendingLeaves));
        }

        // Check if these views exist before using them
        if (textApprovedLeaves != null) {
            int approvedLeaves = dbHelper.getLeavesCountByStatus("approved");
            textApprovedLeaves.setText(String.valueOf(approvedLeaves));
        }

        if (textRejectedLeaves != null) {
            int rejectedLeaves = dbHelper.getLeavesCountByStatus("rejected");
            textRejectedLeaves.setText(String.valueOf(rejectedLeaves));
        }
    }

    private void setupClickListeners() {
        // View Employees
        cardEmployees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, EmployeeListActivity.class);
                startActivity(intent);
            }
        });

        // Add Employee
        cardAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, AddEmployeeActivity.class);
                startActivity(intent);
            }
        });

        // Leave Management
        cardLeaveManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, LeaveManagementActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh stats when returning to dashboard
        updateStats();
    }
}