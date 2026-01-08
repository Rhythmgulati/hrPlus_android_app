package com.example.hrplus;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hrsmart.db";
    private static final int DATABASE_VERSION = 1;

    // Employees table
    public static final String TABLE_EMPLOYEES = "employees";
    public static final String COL_EMP_ID = "emp_id";
    public static final String COL_EMP_NAME = "emp_name";
    public static final String COL_EMP_EMAIL = "emp_email";
    public static final String COL_EMP_PHONE = "emp_phone";
    public static final String COL_EMP_DEPT = "emp_dept";
    public static final String COL_EMP_JOIN_DATE = "join_date";

    // Leaves table
    public static final String TABLE_LEAVES = "leaves";
    public static final String COL_LEAVE_ID = "leave_id";
    public static final String COL_LEAVE_EMP_ID = "emp_id";
    public static final String COL_LEAVE_START = "start_date";
    public static final String COL_LEAVE_END = "end_date";
    public static final String COL_LEAVE_REASON = "reason";
    public static final String COL_LEAVE_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create employees table
        String CREATE_EMPLOYEES_TABLE = "CREATE TABLE " + TABLE_EMPLOYEES + "("
                + COL_EMP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_EMP_NAME + " TEXT,"
                + COL_EMP_EMAIL + " TEXT,"
                + COL_EMP_PHONE + " TEXT,"
                + COL_EMP_DEPT + " TEXT,"
                + COL_EMP_JOIN_DATE + " TEXT DEFAULT CURRENT_DATE)";

        // Create leaves table
        String CREATE_LEAVES_TABLE = "CREATE TABLE " + TABLE_LEAVES + "("
                + COL_LEAVE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_LEAVE_EMP_ID + " INTEGER,"
                + COL_LEAVE_START + " TEXT,"
                + COL_LEAVE_END + " TEXT,"
                + COL_LEAVE_REASON + " TEXT,"
                + COL_LEAVE_STATUS + " TEXT DEFAULT 'pending')";

        db.execSQL(CREATE_EMPLOYEES_TABLE);
        db.execSQL(CREATE_LEAVES_TABLE);

        // Add sample data
        addSampleEmployees(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEAVES);
        onCreate(db);
    }

    private void addSampleEmployees(SQLiteDatabase db) {
        // Add 3 sample employees
        String[] employees = {
                "INSERT INTO employees (emp_name, emp_email, emp_phone, emp_dept) VALUES ('Rhythm Gulati', 'rhythm@company.com', '1234567890', 'IT')",
                "INSERT INTO employees (emp_name, emp_email, emp_phone, emp_dept) VALUES ('Rajeev', 'rajeev@company.com', '0987654321', 'HR')",
        };

        for (String query : employees) {
            db.execSQL(query);
        }

        // Add sample leaves
        String[] leaves = {
                "INSERT INTO leaves (emp_id, start_date, end_date, reason, status) VALUES (1, '2023-10-10', '2023-10-12', 'Sick leave', 'approved')",
                "INSERT INTO leaves (emp_id, start_date, end_date, reason, status) VALUES (2, '2023-10-15', '2023-10-16', 'Personal work', 'pending')",
        };

        for (String query : leaves) {
            db.execSQL(query);
        }
    }

    // ================= EMPLOYEE METHODS =================

    public long addEmployee(String name, String email, String phone, String dept) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EMP_NAME, name);
        values.put(COL_EMP_EMAIL, email);
        values.put(COL_EMP_PHONE, phone);
        values.put(COL_EMP_DEPT, dept);

        long id = db.insert(TABLE_EMPLOYEES, null, values);
        db.close();
        return id;
    }

    public ArrayList<HashMap<String, String>> getAllEmployees() {
        ArrayList<HashMap<String, String>> employeeList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EMPLOYEES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> employee = new HashMap<>();
                employee.put("id", cursor.getString(0));
                employee.put("name", cursor.getString(1));
                employee.put("email", cursor.getString(2));
                employee.put("phone", cursor.getString(3));
                employee.put("dept", cursor.getString(4));
                employee.put("join_date", cursor.getString(5));
                employeeList.add(employee);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return employeeList;
    }

    public int getTotalEmployees() {
        String countQuery = "SELECT * FROM " + TABLE_EMPLOYEES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    // ================= LEAVE METHODS =================

    public long addLeave(int empId, String startDate, String endDate, String reason) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LEAVE_EMP_ID, empId);
        values.put(COL_LEAVE_START, startDate);
        values.put(COL_LEAVE_END, endDate);
        values.put(COL_LEAVE_REASON, reason);

        long id = db.insert(TABLE_LEAVES, null, values);
        db.close();
        return id;
    }

    public ArrayList<HashMap<String, String>> getAllLeaves() {
        ArrayList<HashMap<String, String>> leaveList = new ArrayList<>();
        String selectQuery = "SELECT l.*, e.emp_name FROM " + TABLE_LEAVES + " l " +
                "LEFT JOIN " + TABLE_EMPLOYEES + " e ON l.emp_id = e.emp_id " +
                "ORDER BY l.start_date DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> leave = new HashMap<>();
                leave.put("leave_id", cursor.getString(0));
                leave.put("emp_id", cursor.getString(1));
                leave.put("start_date", cursor.getString(2));
                leave.put("end_date", cursor.getString(3));
                leave.put("reason", cursor.getString(4));
                leave.put("status", cursor.getString(5));
                leave.put("emp_name", cursor.getString(6));
                leaveList.add(leave);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return leaveList;
    }

    public int getPendingLeavesCount() {
        String countQuery = "SELECT * FROM " + TABLE_LEAVES + " WHERE status = 'pending'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void updateLeaveStatus(int leaveId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LEAVE_STATUS, status);

        db.update(TABLE_LEAVES, values, COL_LEAVE_ID + " = ?",
                new String[]{String.valueOf(leaveId)});
        db.close();
    }

    public int getLeavesCountByStatus(String status) {
        String countQuery = "SELECT * FROM " + TABLE_LEAVES + " WHERE status = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, new String[]{status});
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    // Get leaves by specific employee
    public ArrayList<HashMap<String, String>> getLeavesByEmployee(int employeeId) {
        ArrayList<HashMap<String, String>> leaveList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_LEAVES +
                " WHERE emp_id = " + employeeId +
                " ORDER BY start_date DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> leave = new HashMap<>();
                leave.put("leave_id", cursor.getString(0));
                leave.put("emp_id", cursor.getString(1));
                leave.put("start_date", cursor.getString(2));
                leave.put("end_date", cursor.getString(3));
                leave.put("reason", cursor.getString(4));
                leave.put("status", cursor.getString(5));
                leaveList.add(leave);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return leaveList;
    }

    // Get leave statistics for a specific employee
    public HashMap<String, Integer> getEmployeeLeaveStats(int employeeId) {
        HashMap<String, Integer> stats = new HashMap<>();

        String totalQuery = "SELECT COUNT(*) FROM " + TABLE_LEAVES + " WHERE emp_id = " + employeeId;
        String pendingQuery = "SELECT COUNT(*) FROM " + TABLE_LEAVES + " WHERE emp_id = " + employeeId + " AND status = 'pending'";
        String approvedQuery = "SELECT COUNT(*) FROM " + TABLE_LEAVES + " WHERE emp_id = " + employeeId + " AND status = 'approved'";
        String rejectedQuery = "SELECT COUNT(*) FROM " + TABLE_LEAVES + " WHERE emp_id = " + employeeId + " AND status = 'rejected'";

        SQLiteDatabase db = this.getReadableDatabase();

        // Get total leaves
        Cursor cursor = db.rawQuery(totalQuery, null);
        if (cursor.moveToFirst()) {
            stats.put("total", cursor.getInt(0));
        }
        cursor.close();

        // Get pending leaves
        cursor = db.rawQuery(pendingQuery, null);
        if (cursor.moveToFirst()) {
            stats.put("pending", cursor.getInt(0));
        }
        cursor.close();

        // Get approved leaves
        cursor = db.rawQuery(approvedQuery, null);
        if (cursor.moveToFirst()) {
            stats.put("approved", cursor.getInt(0));
        }
        cursor.close();

        // Get rejected leaves
        cursor = db.rawQuery(rejectedQuery, null);
        if (cursor.moveToFirst()) {
            stats.put("rejected", cursor.getInt(0));
        }
        cursor.close();

        db.close();
        return stats;
    }
}