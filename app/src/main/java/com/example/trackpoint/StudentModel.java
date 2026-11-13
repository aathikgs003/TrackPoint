package com.example.trackpoint;

import android.content.ContentValues;
import android.database.Cursor;

public class StudentModel {

    private long id;
    private String name;
    private String registerNumber;
    private String department;
    private String year;
    private String email;
    private String phone;
    private String password;
    private String assignedAdmin;
    private String serverId;
    private boolean isSynced;
    private String updatedAt;

    public StudentModel() {
        this.password = "123";
        this.assignedAdmin = "Aathi";
        this.serverId = "";
        this.isSynced = false;
        this.updatedAt = "";
    }

    public StudentModel(String name, String registerNumber, String department, String year, String email, String phone) {
        this.name = name;
        this.registerNumber = registerNumber;
        this.department = department;
        this.year = year;
        this.email = email;
        this.phone = phone;
        this.password = "123";
        this.assignedAdmin = "Aathi";
        this.serverId = "";
        this.isSynced = false;
        this.updatedAt = "";
    }

    public StudentModel(String name, String registerNumber, String department, String year, String email, String phone, String password) {
        this.name = name;
        this.registerNumber = registerNumber;
        this.department = department;
        this.year = year;
        this.email = email;
        this.phone = phone;
        this.password = password != null && !password.isEmpty() ? password : "123";
        this.assignedAdmin = "Aathi";
        this.serverId = "";
        this.isSynced = false;
        this.updatedAt = "";
    }

    public StudentModel(long id, String name, String registerNumber, String department, String year, String email, String phone, String password, String assignedAdmin, String serverId, boolean isSynced, String updatedAt) {
        this.id = id;
        this.name = name;
        this.registerNumber = registerNumber;
        this.department = department;
        this.year = year;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.assignedAdmin = assignedAdmin != null ? assignedAdmin : "Aathi";
        this.serverId = serverId;
        this.isSynced = isSynced;
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

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
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

    public String getAssignedAdmin() {
        return assignedAdmin;
    }

    public void setAssignedAdmin(String assignedAdmin) {
        this.assignedAdmin = assignedAdmin;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
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
        values.put(DatabaseHelper.COLUMN_REG_NO, registerNumber);
        values.put(DatabaseHelper.COLUMN_DEPT, department);
        values.put(DatabaseHelper.COLUMN_YEAR, year);
        values.put(DatabaseHelper.COLUMN_EMAIL, email);
        values.put(DatabaseHelper.COLUMN_PHONE, phone);
        values.put(DatabaseHelper.COLUMN_PASSWORD, password != null ? password : "123");
        values.put(DatabaseHelper.COLUMN_ASSIGNED_ADMIN, assignedAdmin != null ? assignedAdmin : "Aathi");
        values.put(DatabaseHelper.COLUMN_SERVER_ID, serverId != null ? serverId : "");
        values.put(DatabaseHelper.COLUMN_IS_SYNCED, isSynced ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_UPDATED_AT, updatedAt != null ? updatedAt : "");
        return values;
    }

    public static StudentModel fromCursor(Cursor cursor) {
        if (cursor == null) return null;

        long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
        String registerNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REG_NO));
        String department = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DEPT));
        String year = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_YEAR));

        int emailIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL);
        String email = emailIdx != -1 ? cursor.getString(emailIdx) : "";

        int phoneIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_PHONE);
        String phone = phoneIdx != -1 ? cursor.getString(phoneIdx) : "";

        int passIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD);
        String password = passIdx != -1 ? cursor.getString(passIdx) : "123";

        int adminIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_ASSIGNED_ADMIN);
        String assignedAdmin = adminIdx != -1 ? cursor.getString(adminIdx) : "Aathi";

        int serverIdIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_SERVER_ID);
        String serverId = serverIdIdx != -1 ? cursor.getString(serverIdIdx) : "";

        int syncIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_IS_SYNCED);
        boolean isSynced = syncIdx != -1 && cursor.getInt(syncIdx) == 1;

        int updatedIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_UPDATED_AT);
        String updatedAt = updatedIdx != -1 ? cursor.getString(updatedIdx) : "";

        return new StudentModel(id, name, registerNumber, department, year, email, phone, password, assignedAdmin, serverId, isSynced, updatedAt);
    }
}
