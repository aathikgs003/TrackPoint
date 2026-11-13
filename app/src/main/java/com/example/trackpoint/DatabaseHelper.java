package com.example.trackpoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "trackpoint.db";
    public static final int DATABASE_VERSION = 7;

    public static final String TABLE_STAFF_ADMINS = "staff_admins";
    public static final String TABLE_STUDENT_ADMINS = "student_admins";
    public static final String TABLE_STUDENTS = "students";
    public static final String TABLE_ATTENDANCE = "attendance";
    public static final String TABLE_DAILY_ACTIVITIES = "daily_activities";

    // Common Columns
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_REG_NO = "register_number";
    public static final String COLUMN_ADMIN_ID = "admin_id";
    public static final String COLUMN_STAFF_ID = "staff_id";
    public static final String COLUMN_DEPT = "department";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ASSIGNED_ADMIN = "assigned_admin";
    public static final String COLUMN_SERVER_ID = "server_id";
    public static final String COLUMN_IS_SYNCED = "is_synced";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    // Student Admins specific
    public static final String COLUMN_STUDENTS_ASSIGNED_COUNT = "students_assigned_count";

    // Attendance specific
    public static final String COLUMN_DATE_KEY = "date_key";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_MARKED_BY = "marked_by";

    // Daily Activities specific
    public static final String COLUMN_FILE_NAME = "file_name";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_SUBMIT_TIME = "submit_time";
    public static final String COLUMN_STAFF_REMARK = "staff_remark";

    private static final String CREATE_TABLE_STAFF_ADMINS =
            "CREATE TABLE " + TABLE_STAFF_ADMINS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_STAFF_ID + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_DEPT + " TEXT NOT NULL, " +
                    COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_PHONE + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT DEFAULT '123', " +
                    COLUMN_UPDATED_AT + " TEXT DEFAULT ''" +
                    ");";

    private static final String CREATE_TABLE_STUDENT_ADMINS =
            "CREATE TABLE " + TABLE_STUDENT_ADMINS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_ADMIN_ID + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_DEPT + " TEXT NOT NULL, " +
                    COLUMN_YEAR + " TEXT NOT NULL, " +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_PHONE + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT DEFAULT '123', " +
                    COLUMN_STUDENTS_ASSIGNED_COUNT + " INTEGER DEFAULT 0, " +
                    COLUMN_UPDATED_AT + " TEXT DEFAULT ''" +
                    ");";

    private static final String CREATE_TABLE_STUDENTS =
            "CREATE TABLE " + TABLE_STUDENTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_REG_NO + " TEXT UNIQUE NOT NULL, " +
                    COLUMN_DEPT + " TEXT NOT NULL, " +
                    COLUMN_YEAR + " TEXT NOT NULL, " +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_PHONE + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT DEFAULT '123', " +
                    COLUMN_ASSIGNED_ADMIN + " TEXT DEFAULT 'Aathi', " +
                    COLUMN_SERVER_ID + " TEXT DEFAULT '', " +
                    COLUMN_IS_SYNCED + " INTEGER DEFAULT 0, " +
                    COLUMN_UPDATED_AT + " TEXT DEFAULT ''" +
                    ");";

    private static final String CREATE_TABLE_ATTENDANCE =
            "CREATE TABLE " + TABLE_ATTENDANCE + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_REG_NO + " TEXT NOT NULL, " +
                    COLUMN_DATE_KEY + " TEXT NOT NULL, " +
                    COLUMN_STATUS + " TEXT NOT NULL, " +
                    COLUMN_MARKED_BY + " TEXT DEFAULT 'Admin', " +
                    COLUMN_UPDATED_AT + " TEXT DEFAULT ''" +
                    ");";

    private static final String CREATE_TABLE_DAILY_ACTIVITIES =
            "CREATE TABLE " + TABLE_DAILY_ACTIVITIES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_REG_NO + " TEXT NOT NULL, " +
                    COLUMN_DATE_KEY + " TEXT NOT NULL, " +
                    COLUMN_FILE_NAME + " TEXT, " +
                    COLUMN_NOTES + " TEXT, " +
                    COLUMN_SUBMIT_TIME + " TEXT, " +
                    COLUMN_STATUS + " TEXT DEFAULT 'Submitted', " +
                    COLUMN_STAFF_REMARK + " TEXT DEFAULT '', " +
                    COLUMN_UPDATED_AT + " TEXT DEFAULT ''" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_STAFF_ADMINS);
        db.execSQL(CREATE_TABLE_STUDENT_ADMINS);
        db.execSQL(CREATE_TABLE_STUDENTS);
        db.execSQL(CREATE_TABLE_ATTENDANCE);
        db.execSQL(CREATE_TABLE_DAILY_ACTIVITIES);

        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STAFF_ADMINS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT_ADMINS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAILY_ACTIVITIES);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("DELETE FROM " + TABLE_STUDENTS + " WHERE register_number IN ('CSE22CS002', 'CSE22CS003', 'CSE22CS004', 'CSE22CS005') OR name IN ('Divya S', 'Karthik R', 'Nithya P', 'Praveen M');");
    }

    private void seedInitialData(SQLiteDatabase db) {
        // 1. Seed Staff Admin
        ContentValues stfVal = new ContentValues();
        stfVal.put(COLUMN_NAME, "Suruthika S");
        stfVal.put(COLUMN_STAFF_ID, "STF001");
        stfVal.put(COLUMN_DEPT, "Computer Science & Engineering");
        stfVal.put(COLUMN_EMAIL, "suruthika@gmail.com");
        stfVal.put(COLUMN_PHONE, "+91 98765 43200");
        stfVal.put(COLUMN_PASSWORD, "123");
        stfVal.put(COLUMN_UPDATED_AT, String.valueOf(System.currentTimeMillis()));
        db.insertWithOnConflict(TABLE_STAFF_ADMINS, null, stfVal, SQLiteDatabase.CONFLICT_REPLACE);

        // 2. Seed Student Admin
        ContentValues saVal = new ContentValues();
        saVal.put(COLUMN_NAME, "Aathi");
        saVal.put(COLUMN_ADMIN_ID, "SADM001");
        saVal.put(COLUMN_DEPT, "CSE");
        saVal.put(COLUMN_YEAR, "III Year");
        saVal.put(COLUMN_EMAIL, "aathi@gmail.com");
        saVal.put(COLUMN_PHONE, "98765 43201");
        saVal.put(COLUMN_PASSWORD, "123");
        saVal.put(COLUMN_STUDENTS_ASSIGNED_COUNT, 1);
        saVal.put(COLUMN_UPDATED_AT, String.valueOf(System.currentTimeMillis()));
        db.insertWithOnConflict(TABLE_STUDENT_ADMINS, null, saVal, SQLiteDatabase.CONFLICT_REPLACE);

        // 3. Seed Student
        ContentValues sVal = new ContentValues();
        sVal.put(COLUMN_NAME, "Arun Kumar");
        sVal.put(COLUMN_REG_NO, "CSE22CS001");
        sVal.put(COLUMN_DEPT, "CSE");
        sVal.put(COLUMN_YEAR, "III Year");
        sVal.put(COLUMN_EMAIL, "arun@gmail.com");
        sVal.put(COLUMN_PHONE, "98765 43210");
        sVal.put(COLUMN_PASSWORD, "123");
        sVal.put(COLUMN_ASSIGNED_ADMIN, "Aathi");
        sVal.put(COLUMN_SERVER_ID, "seed_cse22cs001");
        sVal.put(COLUMN_IS_SYNCED, 1);
        sVal.put(COLUMN_UPDATED_AT, String.valueOf(System.currentTimeMillis()));
        db.insertWithOnConflict(TABLE_STUDENTS, null, sVal, SQLiteDatabase.CONFLICT_REPLACE);
    }
}
