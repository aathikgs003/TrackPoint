package com.example.trackpoint;

import android.content.ContentValues;
import android.database.Cursor;

public class DailyActivityModel {

    private long id;
    private String studentRegNo;
    private String dateKey;
    private String fileName;
    private String notes;
    private String submitTime;
    private String status;
    private String staffRemark;
    private String updatedAt;

    public DailyActivityModel() {
        this.status = "Submitted";
        this.staffRemark = "";
        this.updatedAt = "";
    }

    public DailyActivityModel(String studentRegNo, String dateKey, String fileName, String notes, String submitTime) {
        this.studentRegNo = studentRegNo;
        this.dateKey = dateKey;
        this.fileName = fileName;
        this.notes = notes;
        this.submitTime = submitTime;
        this.status = "Submitted";
        this.staffRemark = "";
        this.updatedAt = "";
    }

    public DailyActivityModel(long id, String studentRegNo, String dateKey, String fileName, String notes, String submitTime, String status, String staffRemark, String updatedAt) {
        this.id = id;
        this.studentRegNo = studentRegNo;
        this.dateKey = dateKey;
        this.fileName = fileName;
        this.notes = notes;
        this.submitTime = submitTime;
        this.status = status;
        this.staffRemark = staffRemark;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStudentRegNo() {
        return studentRegNo;
    }

    public void setStudentRegNo(String studentRegNo) {
        this.studentRegNo = studentRegNo;
    }

    public String getDateKey() {
        return dateKey;
    }

    public void setDateKey(String dateKey) {
        this.dateKey = dateKey;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStaffRemark() {
        return staffRemark;
    }

    public void setStaffRemark(String staffRemark) {
        this.staffRemark = staffRemark;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_REG_NO, studentRegNo);
        values.put(DatabaseHelper.COLUMN_DATE_KEY, dateKey);
        values.put(DatabaseHelper.COLUMN_FILE_NAME, fileName != null ? fileName : "");
        values.put(DatabaseHelper.COLUMN_NOTES, notes != null ? notes : "");
        values.put(DatabaseHelper.COLUMN_SUBMIT_TIME, submitTime != null ? submitTime : "");
        values.put(DatabaseHelper.COLUMN_STATUS, status != null ? status : "Submitted");
        values.put(DatabaseHelper.COLUMN_STAFF_REMARK, staffRemark != null ? staffRemark : "");
        values.put(DatabaseHelper.COLUMN_UPDATED_AT, updatedAt != null ? updatedAt : "");
        return values;
    }

    public static DailyActivityModel fromCursor(Cursor cursor) {
        if (cursor == null) return null;

        long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        String regNo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REG_NO));
        String dateKey = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE_KEY));

        int fileIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_FILE_NAME);
        String fileName = fileIdx != -1 ? cursor.getString(fileIdx) : "";

        int notesIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTES);
        String notes = notesIdx != -1 ? cursor.getString(notesIdx) : "";

        int timeIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_SUBMIT_TIME);
        String submitTime = timeIdx != -1 ? cursor.getString(timeIdx) : "";

        int statusIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS);
        String status = statusIdx != -1 ? cursor.getString(statusIdx) : "Submitted";

        int remarkIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_STAFF_REMARK);
        String staffRemark = remarkIdx != -1 ? cursor.getString(remarkIdx) : "";

        int updatedIdx = cursor.getColumnIndex(DatabaseHelper.COLUMN_UPDATED_AT);
        String updatedAt = updatedIdx != -1 ? cursor.getString(updatedIdx) : "";

        return new DailyActivityModel(id, regNo, dateKey, fileName, notes, submitTime, status, staffRemark, updatedAt);
    }
}
