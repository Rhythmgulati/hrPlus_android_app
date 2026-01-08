package com.example.hrplus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;

public class EmployeeListActivity extends AppCompatActivity {

    private ListView listViewEmployees;
    private Button buttonAddEmployee;
    private TextView textEmpty;
    private DatabaseHelper dbHelper;
    private ArrayList<HashMap<String, String>> employeeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        listViewEmployees = findViewById(R.id.listViewEmployees);
        buttonAddEmployee = findViewById(R.id.buttonAddEmployee);
        textEmpty = findViewById(R.id.textEmpty);

        // Load employees
        loadEmployees();

        // Set click listeners
        buttonAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployeeListActivity.this, AddEmployeeActivity.class);
                startActivity(intent);
            }
        });

        // List item click - Go to Employee Detail
        listViewEmployees.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> employee = employeeList.get(position);

                // Go to EmployeeDetailActivity
                Intent intent = new Intent(EmployeeListActivity.this, EmployeeDetailActivity.class);
                intent.putExtra("emp_id", employee.get("id"));
                intent.putExtra("emp_name", employee.get("name"));
                intent.putExtra("emp_email", employee.get("email"));
                intent.putExtra("emp_phone", employee.get("phone"));
                intent.putExtra("emp_dept", employee.get("dept"));
                startActivity(intent);
            }
        });
    }

    private void loadEmployees() {
        // Get employees from database
        employeeList = dbHelper.getAllEmployees();

        if (employeeList.size() > 0) {
            textEmpty.setVisibility(View.GONE);
            listViewEmployees.setVisibility(View.VISIBLE);

            // Create a simple array of employee names
            ArrayList<String> employeeNames = new ArrayList<>();
            for (HashMap<String, String> employee : employeeList) {
                String name = employee.get("name") + " - " + employee.get("dept");
                employeeNames.add(name);
            }

            // Set adapter
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    employeeNames
            );
            listViewEmployees.setAdapter(adapter);
        } else {
            textEmpty.setVisibility(View.VISIBLE);
            listViewEmployees.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh employee list
        loadEmployees();
    }
}