package com.example.trackpoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class StudentRepository {

    private static DatabaseHelper dbHelper;
    private static SQLiteDatabase database;

    public StudentRepository(Context context) {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(context.getApplicationContext());
        }
        getDatabase();
    }

    private synchronized SQLiteDatabase getDatabase() {
        if (database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }
        return database;
    }

    // --- STAFF ADMIN OPERATIONS ---

    public long addStaffAdmin(StaffAdminModel staff) {
        if (staff == null) return -1;
        SQLiteDatabase db = getDatabase();
        try {
            ContentValues values = staff.toContentValues();
            if (staff.getUpdatedAt() == null || staff.getUpdatedAt().isEmpty()) {
                values.put(DatabaseHelper.COLUMN_UPDATED_AT, getCurrentTimestamp());
            }
            return db.insertWithOnConflict(DatabaseHelper.TABLE_STAFF_ADMINS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public StaffAdminModel getStaffAdminByEmail(String email) {
        if (email == null || email.isEmpty()) return null;
        SQLiteDatabase db = getDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DatabaseHelper.TABLE_STAFF_ADMINS, null,
                    DatabaseHelper.COLUMN_EMAIL + " = ? OR " + DatabaseHelper.COLUMN_STAFF_ID + " = ?",
                    new String[]{email, email},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return StaffAdminModel.fromCursor(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public StaffAdminModel authenticateStaffAdmin(String emailOrId, String password) {
        if (emailOrId == null || password == null) return null;
        SQLiteDatabase db = getDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DatabaseHelper.TABLE_STAFF_ADMINS, null,
                    "(" + DatabaseHelper.COLUMN_EMAIL + " = ? OR " + DatabaseHelper.COLUMN_STAFF_ID + " = ?) AND " + DatabaseHelper.COLUMN_PASSWORD + " = ?",
                    new String[]{emailOrId, emailOrId, password},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return StaffAdminModel.fromCursor(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    // --- STUDENT OPERATIONS ---

    public long addStudent(StudentModel student) {
        if (student == null) return -1;

        SQLiteDatabase db = getDatabase();
        try {
            ContentValues values = student.toContentValues();
            if (student.getUpdatedAt() == null || student.getUpdatedAt().isEmpty()) {
                values.put(DatabaseHelper.COLUMN_UPDATED_AT, getCurrentTimestamp());
            }
            return db.insert(DatabaseHelper.TABLE_STUDENTS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean updateStudent(StudentModel student) {
        if (student == null) return false;

        SQLiteDatabase db = getDatabase();
        try {
            ContentValues values = student.toContentValues();
            values.put(DatabaseHelper.COLUMN_UPDATED_AT, getCurrentTimestamp());
            values.put(DatabaseHelper.COLUMN_IS_SYNCED, 0);

            int rows;
            if (student.getId() > 0) {
                rows = db.update(DatabaseHelper.TABLE_STUDENTS, values,
                        DatabaseHelper.COLUMN_ID + " = ?",
                        new String[]{String.valueOf(student.getId())});
            } else {
                rows = db.update(DatabaseHelper.TABLE_STUDENTS, values,
                        DatabaseHelper.COLUMN_REG_NO + " = ?",
                        new String[]{student.getRegisterNumber()});
            }
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStudentByRegNo(String regNo) {
        if (regNo == null || regNo.isEmpty()) return false;

        SQLiteDatabase db = getDatabase();
        try {
            int rows = db.delete(DatabaseHelper.TABLE_STUDENTS,
                    DatabaseHelper.COLUMN_REG_NO + " = ?",
                    new String[]{regNo});
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public StudentModel getStudentByRegNo(String regNoOrEmail) {
        if (regNoOrEmail == null || regNoOrEmail.isEmpty()) return null;

        SQLiteDatabase db = getDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DatabaseHelper.TABLE_STUDENTS, null,
                    DatabaseHelper.COLUMN_REG_NO + " = ? OR " + DatabaseHelper.COLUMN_EMAIL + " = ?",
                    new String[]{regNoOrEmail, regNoOrEmail},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                return StudentModel.fromCursor(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public ArrayList<StudentModel> getAllStudents() {
        ArrayList<StudentModel> list = new ArrayList<>();
        SQLiteDatabase db = getDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(DatabaseHelper.TABLE_STUDENTS, null, null, null, null, null, DatabaseHelper.COLUMN_NAME + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    StudentModel s = StudentModel.fromCursor(cursor);
                    if (s != null) {
                        list.add(s);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }

        return list;
    }

    public ArrayList<StudentModel> searchAndFilterStudents(String query, String department, String year) {
        return searchAndFilterStudentsForAdmin(query, department, year, null);
    }

    public ArrayList<StudentModel> searchAndFilterStudentsForAdmin(String query, String department, String year, String adminName) {
        ArrayList<StudentModel> list = new ArrayList<>();
        SQLiteDatabase db = getDatabase();
        Cursor cursor = null;

        try {
            StringBuilder selection = new StringBuilder("1=1");
            ArrayList<String> selectionArgs = new ArrayList<>();

            if (query != null && !query.trim().isEmpty()) {
                String q = "%" + query.trim() + "%";
                selection.append(" AND (").append(DatabaseHelper.COLUMN_NAME).append(" LIKE ? OR ").append(DatabaseHelper.COLUMN_REG_NO).append(" LIKE ?)");
                selectionArgs.add(q);
                selectionArgs.add(q);
            }

            if (department != null && !department.equals("All Departments") && !department.equals("Select Department")) {
                selection.append(" AND ").append(DatabaseHelper.COLUMN_DEPT).append(" = ?");
                selectionArgs.add(department);
            }

            if (year != null && !year.equals("All Years") && !year.equals("Select Year")) {
                selection.append(" AND ").append(DatabaseHelper.COLUMN_YEAR).append(" = ?");
                selectionArgs.add(year);
            }

            if (adminName != null && !adminName.trim().isEmpty()) {
                selection.append(" AND (").append(DatabaseHelper.COLUMN_ASSIGNED_ADMIN).append(" = ? OR ")
                        .append(DatabaseHelper.COLUMN_ASSIGNED_ADMIN).append(" = ?)");
                selectionArgs.add(adminName);
                selectionArgs.add(adminName.toLowerCase());
            }

            String[] args = selectionArgs.toArray(new String[0]);
            cursor = db.query(DatabaseHelper.TABLE_STUDENTS, null, selection.toString(), args, null, null, DatabaseHelper.COLUMN_NAME + " ASC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    StudentModel s = StudentModel.fromCursor(cursor);
                    if (s != null) {
                        list.add(s);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }

        return list;
    }

    public ArrayList<StudentModel> searchAndFilterStudents(String query, String department, String year, String section) {
        return searchAndFilterStudents(query, department, year);
    }

    public StudentModel authenticateStudent(String emailOrRegNo, String password) {
        if (emailOrRegNo == null || password == null) return null;

        SQLiteDatabase db = getDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DatabaseHelper.TABLE_STUDENTS, null,
                    "(" + DatabaseHelper.COLUMN_REG_NO + " = ? OR " + DatabaseHelper.COLUMN_EMAIL + " = ?) AND " + DatabaseHelper.COLUMN_PASSWORD + " = ?",
                    new String[]{emailOrRegNo, emailOrRegNo, password},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                return StudentModel.fromCursor(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public boolean registerNumberExists(String regNo) {
        if (regNo == null || regNo.isEmpty()) return false;

        SQLiteDatabase db = getDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DatabaseHelper.TABLE_STUDENTS, new String[]{DatabaseHelper.COLUMN_ID},
                    DatabaseHelper.COLUMN_REG_NO + " = ?",
                    new String[]{regNo},
                    null, null, null);
            return cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // --- STUDENT ADMIN OPERATIONS ---

    public long addStudentAdmin(StudentAdminModel admin) {
        if (admin == null) return -1;

        SQLiteDatabase db = getDatabase();
        try {
            ContentValues values = admin.toContentValues();
            if (admin.getUpdatedAt() == null || admin.getUpdatedAt().isEmpty()) {
                values.put(DatabaseHelper.COLUMN_UPDATED_AT, getCurrentTimestamp());
            }
            return db.insert(DatabaseHelper.TABLE_STUDENT_ADMINS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getAssignedStudentsCountForAdmin(String adminName) {
        if (adminName == null || adminName.isEmpty()) return 0;
        SQLiteDatabase db = getDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DatabaseHelper.TABLE_STUDENTS, new String[]{DatabaseHelper.COLUMN_ID},
                    DatabaseHelper.COLUMN_ASSIGNED_ADMIN + " = ? OR " + DatabaseHelper.COLUMN_ASSIGNED_ADMIN + " = ?",
                    new String[]{adminName, adminName.toLowerCase()},
                    null, null, null);
            return cursor != null ? cursor.getCount() : 0;
        } catch (Exception e) {
            return 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public ArrayList<StudentAdminModel> getAllStudentAdmins() {
        ArrayList<StudentAdminModel> list = new ArrayList<>();
        SQLiteDatabase db = getDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(DatabaseHelper.TABLE_STUDENT_ADMINS, null, null, null, null, null, DatabaseHelper.COLUMN_NAME + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    StudentAdminModel sa = StudentAdminModel.fromCursor(cursor);
                    if (sa != null) {
                        sa.setStudentsAssignedCount(getAssignedStudentsCountForAdmin(sa.getName()));
                        list.add(sa);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }

        return list;
    }

    public StudentAdminModel getStudentAdminById(String adminIdOrEmail) {
        if (adminIdOrEmail == null || adminIdOrEmail.isEmpty()) return null;

        SQLiteDatabase db = getDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DatabaseHelper.TABLE_STUDENT_ADMINS, null,
                    DatabaseHelper.COLUMN_ADMIN_ID + " = ? OR " + DatabaseHelper.COLUMN_EMAIL + " = ?",
                    new String[]{adminIdOrEmail, adminIdOrEmail},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                StudentAdminModel sa = StudentAdminModel.fromCursor(cursor);
                if (sa != null) {
                    sa.setStudentsAssignedCount(getAssignedStudentsCountForAdmin(sa.getName()));
                }
                return sa;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public StudentAdminModel authenticateStudentAdmin(String emailOrAdminId, String password) {
        if (emailOrAdminId == null || password == null) return null;

        SQLiteDatabase db = getDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DatabaseHelper.TABLE_STUDENT_ADMINS, null,
                    "(" + DatabaseHelper.COLUMN_ADMIN_ID + " = ? OR " + DatabaseHelper.COLUMN_EMAIL + " = ?) AND " + DatabaseHelper.COLUMN_PASSWORD + " = ?",
                    new String[]{emailOrAdminId, emailOrAdminId, password},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                return StudentAdminModel.fromCursor(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public boolean adminIdExists(String adminId) {
        if (adminId == null || adminId.isEmpty()) return false;

        SQLiteDatabase db = getDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DatabaseHelper.TABLE_STUDENT_ADMINS, new String[]{DatabaseHelper.COLUMN_ID},
                    DatabaseHelper.COLUMN_ADMIN_ID + " = ?",
                    new String[]{adminId},
                    null, null, null);
            return cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // --- ATTENDANCE OPERATIONS IN SQLITE ---

    public boolean saveOrUpdateAttendance(String studentRegNo, String dateKey, String status, String markedBy) {
        if (studentRegNo == null || dateKey == null || status == null) return false;

        SQLiteDatabase db = getDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_REG_NO, studentRegNo);
            values.put(DatabaseHelper.COLUMN_DATE_KEY, dateKey);
            values.put(DatabaseHelper.COLUMN_STATUS, status);
            values.put(DatabaseHelper.COLUMN_MARKED_BY, markedBy != null ? markedBy : "Admin");
            values.put(DatabaseHelper.COLUMN_UPDATED_AT, getCurrentTimestamp());

            Cursor cursor = db.query(DatabaseHelper.TABLE_ATTENDANCE, new String[]{DatabaseHelper.COLUMN_ID},
                    DatabaseHelper.COLUMN_REG_NO + " = ? AND " + DatabaseHelper.COLUMN_DATE_KEY + " = ?",
                    new String[]{studentRegNo, dateKey}, null, null, null);

            boolean exists = cursor != null && cursor.getCount() > 0;
            if (cursor != null) cursor.close();

            if (exists) {
                db.update(DatabaseHelper.TABLE_ATTENDANCE, values,
                        DatabaseHelper.COLUMN_REG_NO + " = ? AND " + DatabaseHelper.COLUMN_DATE_KEY + " = ?",
                        new String[]{studentRegNo, dateKey});
            } else {
                db.insert(DatabaseHelper.TABLE_ATTENDANCE, null, values);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getAttendanceStatus(String studentRegNo, String dateKey) {
        if (studentRegNo == null || dateKey == null) return null;

        SQLiteDatabase db = getDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DatabaseHelper.TABLE_ATTENDANCE, new String[]{DatabaseHelper.COLUMN_STATUS},
                    DatabaseHelper.COLUMN_REG_NO + " = ? AND " + DatabaseHelper.COLUMN_DATE_KEY + " = ?",
                    new String[]{studentRegNo, dateKey}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public int[] getAttendanceStatsForStudent(String studentRegNo, String datePrefix) {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = null;
        int total = 0;
        int present = 0;
        int absent = 0;

        try {
            StringBuilder selection = new StringBuilder(DatabaseHelper.COLUMN_REG_NO + " = ?");
            ArrayList<String> args = new ArrayList<>();
            args.add(studentRegNo);

            if (datePrefix != null && !datePrefix.isEmpty()) {
                selection.append(" AND ").append(DatabaseHelper.COLUMN_DATE_KEY).append(" LIKE ?");
                args.add(datePrefix + "%");
            }

            cursor = db.query(DatabaseHelper.TABLE_ATTENDANCE, new String[]{DatabaseHelper.COLUMN_STATUS},
                    selection.toString(), args.toArray(new String[0]), null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    total++;
                    String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS));
                    if ("Present".equalsIgnoreCase(status)) {
                        present++;
                    } else if ("Absent".equalsIgnoreCase(status)) {
                        absent++;
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }

        return new int[]{total, present, absent};
    }

    public int[] getAttendanceStatsForAdmin(String adminName, String datePrefix) {
        ArrayList<StudentModel> assigned = searchAndFilterStudentsForAdmin("", "All Departments", "All Years", adminName);
        int total = 0;
        int present = 0;
        int absent = 0;

        for (StudentModel s : assigned) {
            int[] stats = getAttendanceStatsForStudent(s.getRegisterNumber(), datePrefix);
            total += stats[0];
            present += stats[1];
            absent += stats[2];
        }

        return new int[]{total, present, absent};
    }

    // --- DAILY ACTIVITIES OPERATIONS IN SQLITE ---

    public boolean saveOrUpdateDailyActivity(String studentRegNo, String dateKey, String fileName, String notes, String submitTime) {
        if (studentRegNo == null || dateKey == null) return false;

        SQLiteDatabase db = getDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_REG_NO, studentRegNo);
            values.put(DatabaseHelper.COLUMN_DATE_KEY, dateKey);
            values.put(DatabaseHelper.COLUMN_FILE_NAME, fileName != null ? fileName : "");
            values.put(DatabaseHelper.COLUMN_NOTES, notes != null ? notes : "");
            values.put(DatabaseHelper.COLUMN_SUBMIT_TIME, submitTime != null ? submitTime : "");
            values.put(DatabaseHelper.COLUMN_STATUS, "Submitted");
            values.put(DatabaseHelper.COLUMN_UPDATED_AT, getCurrentTimestamp());

            Cursor cursor = db.query(DatabaseHelper.TABLE_DAILY_ACTIVITIES, new String[]{DatabaseHelper.COLUMN_ID},
                    DatabaseHelper.COLUMN_REG_NO + " = ? AND " + DatabaseHelper.COLUMN_DATE_KEY + " = ?",
                    new String[]{studentRegNo, dateKey}, null, null, null);

            boolean exists = cursor != null && cursor.getCount() > 0;
            if (cursor != null) cursor.close();

            if (exists) {
                db.update(DatabaseHelper.TABLE_DAILY_ACTIVITIES, values,
                        DatabaseHelper.COLUMN_REG_NO + " = ? AND " + DatabaseHelper.COLUMN_DATE_KEY + " = ?",
                        new String[]{studentRegNo, dateKey});
            } else {
                db.insert(DatabaseHelper.TABLE_DAILY_ACTIVITIES, null, values);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDailyActivity(String studentRegNo, String dateKey) {
        if (studentRegNo == null || dateKey == null) return false;

        SQLiteDatabase db = getDatabase();
        try {
            int rows = db.delete(DatabaseHelper.TABLE_DAILY_ACTIVITIES,
                    DatabaseHelper.COLUMN_REG_NO + " = ? AND " + DatabaseHelper.COLUMN_DATE_KEY + " = ?",
                    new String[]{studentRegNo, dateKey});
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStaffRemark(String studentRegNo, String dateKey, String remark) {
        if (studentRegNo == null || dateKey == null) return false;

        SQLiteDatabase db = getDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_STAFF_REMARK, remark != null ? remark : "");
            values.put(DatabaseHelper.COLUMN_UPDATED_AT, getCurrentTimestamp());

            int rows = db.update(DatabaseHelper.TABLE_DAILY_ACTIVITIES, values,
                    DatabaseHelper.COLUMN_REG_NO + " = ? AND " + DatabaseHelper.COLUMN_DATE_KEY + " = ?",
                    new String[]{studentRegNo, dateKey});
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public DailyActivityModel getDailyActivity(String studentRegNo, String dateKey) {
        if (studentRegNo == null || dateKey == null) return null;

        SQLiteDatabase db = getDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DatabaseHelper.TABLE_DAILY_ACTIVITIES, null,
                    DatabaseHelper.COLUMN_REG_NO + " = ? AND " + DatabaseHelper.COLUMN_DATE_KEY + " = ?",
                    new String[]{studentRegNo, dateKey}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                return DailyActivityModel.fromCursor(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}
