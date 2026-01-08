package com.example.hrplus;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;

public class LeaveManagementActivity extends AppCompatActivity {

    private ListView listViewLeaves;
    private TextView textEmpty;
    private Spinner spinnerFilter;
    private Button buttonShowAll, buttonShowPending, buttonShowApproved, buttonShowRejected;
    private DatabaseHelper dbHelper;
    private ArrayList<HashMap<String, String>> allLeaves;
    private boolean showAllLeaves = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);

        // Check if we should show all leaves
        if (getIntent().hasExtra("show_all")) {
            showAllLeaves = getIntent().getBooleanExtra("show_all", false);
        }

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        initializeViews();

        // Load all leaves initially
        loadLeaves("all");

        // Setup filter spinner
        setupFilterSpinner();
    }

    private void initializeViews() {
        listViewLeaves = findViewById(R.id.listViewLeaves);
        textEmpty = findViewById(R.id.textEmpty);
        spinnerFilter = findViewById(R.id.spinnerFilter);

//        buttonShowAll = findViewById(R.id.buttonShowAll);
//        buttonShowPending = findViewById(R.id.buttonShowPending);
//        buttonShowApproved = findViewById(R.id.buttonShowApproved);
//        buttonShowRejected = findViewById(R.id.buttonShowRejected);

        // Set click listeners for filter buttons
//        buttonShowAll.setOnClickListener(v -> loadLeaves("all"));
//        buttonShowPending.setOnClickListener(v -> loadLeaves("pending"));
//        buttonShowApproved.setOnClickListener(v -> loadLeaves("approved"));
//        buttonShowRejected.setOnClickListener(v -> loadLeaves("rejected"));
    }

    private void setupFilterSpinner() {
        // Create adapter for spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.leave_filter_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);

        // Set spinner listener
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                switch (selected) {
                    case "All Leaves":
                        loadLeaves("all");
                        break;
                    case "Pending Only":
                        loadLeaves("pending");
                        break;
                    case "Approved Only":
                        loadLeaves("approved");
                        break;
                    case "Rejected Only":
                        loadLeaves("rejected");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadLeaves(String filter) {
        // Get all leaves from database
        allLeaves = dbHelper.getAllLeaves();

        // Filter leaves if needed
        ArrayList<HashMap<String, String>> filteredLeaves = new ArrayList<>();

        if (filter.equals("all")) {
            filteredLeaves = allLeaves;
        } else {
            for (HashMap<String, String> leave : allLeaves) {
                if (leave.get("status").equals(filter)) {
                    filteredLeaves.add(leave);
                }
            }
        }

        if (filteredLeaves.size() > 0) {
            textEmpty.setVisibility(View.GONE);
            listViewLeaves.setVisibility(View.VISIBLE);

            // Create a simple array of leave details
            ArrayList<String> leaveDetails = new ArrayList<>();
            for (HashMap<String, String> leave : filteredLeaves) {
                String status = leave.get("status");
                String statusIcon = status.equals("pending") ? "⏳" :
                        status.equals("approved") ? "✅" : "❌";

                String detail = statusIcon + " " + leave.get("emp_name") + "\n" +
                        "📅 " + leave.get("start_date") + " to " + leave.get("end_date") + "\n" +
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
            listViewLeaves.setAdapter(adapter);

            // Set click listener for leaves
            final ArrayList<HashMap<String, String>> finalLeaves = filteredLeaves;
            listViewLeaves.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position < finalLeaves.size()) {
                        HashMap<String, String> leave = finalLeaves.get(position);
                        showLeaveOptionsDialog(leave);
                    }
                }
            });
        } else {
            textEmpty.setText("No leaves found with selected filter");
            textEmpty.setVisibility(View.VISIBLE);
            listViewLeaves.setVisibility(View.GONE);
        }
    }

    private void showLeaveOptionsDialog(final HashMap<String, String> leave) {
        String leaveId = leave.get("leave_id");
        String empName = leave.get("emp_name");
        String currentStatus = leave.get("status");

        String[] options;

        if (currentStatus.equals("pending")) {
            options = new String[]{"Approve Leave", "Reject Leave", "View Details"};
        } else {
            options = new String[]{"View Details", "Change Status"};
        }

        new android.app.AlertDialog.Builder(this)
                .setTitle("Leave Request - " + empName)
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // First option
                            if (currentStatus.equals("pending")) {
                                dbHelper.updateLeaveStatus(Integer.parseInt(leaveId), "approved");
                                loadLeaves(spinnerFilter.getSelectedItem().toString().toLowerCase().replace(" only", ""));
                            } else {
                                showLeaveDetails(leave);
                            }
                            break;
                        case 1: // Second option
                            if (currentStatus.equals("pending")) {
                                dbHelper.updateLeaveStatus(Integer.parseInt(leaveId), "rejected");
                                loadLeaves(spinnerFilter.getSelectedItem().toString().toLowerCase().replace(" only", ""));
                            } else {
                                showStatusChangeDialog(leaveId);
                            }
                            break;
                        case 2: // Third option
                            showLeaveDetails(leave);
                            break;
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showLeaveDetails(HashMap<String, String> leave) {
        String details = "Employee: " + leave.get("emp_name") + "\n" +
                "Start Date: " + leave.get("start_date") + "\n" +
                "End Date: " + leave.get("end_date") + "\n" +
                "Reason: " + leave.get("reason") + "\n" +
                "Status: " + leave.get("status");

        new android.app.AlertDialog.Builder(this)
                .setTitle("Leave Details")
                .setMessage(details)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showStatusChangeDialog(final String leaveId) {
        final String[] statuses = {"pending", "approved", "rejected"};

        new android.app.AlertDialog.Builder(this)
                .setTitle("Change Status")
                .setItems(new String[]{"⏳ Pending", "✅ Approved", "❌ Rejected"}, (dialog, which) -> {
                    dbHelper.updateLeaveStatus(Integer.parseInt(leaveId), statuses[which]);
                    loadLeaves(spinnerFilter.getSelectedItem().toString().toLowerCase().replace(" only", ""));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh leave list
        loadLeaves(spinnerFilter.getSelectedItem().toString().toLowerCase().replace(" only", ""));
    }
}