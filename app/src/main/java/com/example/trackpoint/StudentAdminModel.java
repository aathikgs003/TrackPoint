package com.example.trackpoint;

import android.content.ContentValues;
import android.database.Cursor;

public class StudentAdminModel {

    private long id;
    private String name;
    private String adminId;
    private String department;
    private String year;
    private String email;
    private String phone;
    private String password;
    private int studentsAssignedCount;
    private String updatedAt;

    public StudentAdminModel() {
        this.password = "123";
        this.studentsAssignedCount = 0;
        this.updatedAt = "";
    }

    public StudentAdminModel(String name, String adminId, String department, String year, String email, String phone) {
        this.name = name;
        this.adminId = adminId;
        this.department = department;
        this.year = year;
        this.email = email;
        this.phone = phone;
        this.password = "123";
        this.studentsAssignedCount = 0;
        this.updatedAt = "";
    }

    public StudentAdminModel(long id, String name, String adminId, String department, String year, String email, String phone, String password, int studentsAssignedCount, String updatedAt) {
        this.id = id;
        this.name = name;
        this.adminId = adminId;
        this.department = department;
        this.year = year;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.studentsAssignedCount = studentsAssignedCount;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStudentsAssignedCount() {
        return studentsAssignedCount;
    }

    public void setStudentsAssignedCount(int studentsAssignedCount) {
        this.studentsAssignedCount = studentsAssignedCount;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_ADMIN_ID, adminId);
        values.put(DatabaseHelper.COLUMN_DEPT, department);
        values.put(DatabaseHelper.COLUMN_YEAR, year);
        values.put(DatabaseHelper.COLUMN_EMAIL, email);
        values.put(DatabaseHelper.COLUMN_PHONE, phone);
        values.put(DatabaseHelper.COLUMN_PASSWORD, password != null ? password : "123");
        values.put(DatabaseHelper.COLUMN_STUDENTS_ASSIGNED_COUNT, studentsAssignedCount);
        values.put(DatabaseHelper.COLUMN_UPDATED_AT, updatedAt != null ? updatedAt : "");
        return values;
    }

    public static StudentAdminModel fromCursor(Cursor cursor) {
        if (cursor == null) return null;

        long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
        String adminId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADMIN_ID));
        String department = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DEPT));
        String year = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_YEAR));

        int emailIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL);
        String email = emailIdx != -1 ? cursor.getString(emailIdx) : "";

        int phoneIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE);
        String phone = phoneIdx != -1 ? cursor.getString(phoneIdx) : "";

        int passIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD);
        String password = passIdx != -1 ? cursor.getString(passIdx) : "123";

        int countIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_STUDENTS_ASSIGNED_COUNT);
        int count = countIdx != -1 ? cursor.getInt(countIdx) : 0;

        int updatedIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_UPDATED_AT);
        String updatedAt = updatedIdx != -1 ? cursor.getString(updatedIdx) : "";

        return new StudentAdminModel(id, name, adminId, department, year, email, phone, password, count, updatedAt);
    }
}
