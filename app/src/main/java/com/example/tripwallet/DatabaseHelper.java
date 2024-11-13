package com.example.tripwallet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TripWallet.db";
    private static final int DATABASE_VERSION = 1;

    // Users table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    // Trips table
    public static final String TABLE_TRIPS = "trips";
    public static final String COLUMN_TRIP_ID = "trip_id";
    public static final String COLUMN_TRIP_USER_ID = "user_id";
    public static final String COLUMN_DESTINATION = "destination";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";
    public static final String COLUMN_BUDGET = "budget";
    public static final String COLUMN_NOTES = "notes";

    // Expenses table
    public static final String TABLE_EXPENSES = "expenses";
    public static final String COLUMN_EXPENSE_ID = "expense_id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DESCRIPTION = "description";

    private static final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USERNAME + " TEXT UNIQUE, "
            + COLUMN_PASSWORD + " TEXT)";

    private static final String CREATE_TRIPS_TABLE = "CREATE TABLE " + TABLE_TRIPS + "("
            + COLUMN_TRIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TRIP_USER_ID + " INTEGER, "
            + COLUMN_DESTINATION + " TEXT, "
            + COLUMN_START_DATE + " TEXT, "
            + COLUMN_END_DATE + " TEXT, "
            + COLUMN_BUDGET + " REAL, "
            + COLUMN_NOTES + " TEXT, "
            + "FOREIGN KEY(" + COLUMN_TRIP_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";

    private static final String CREATE_EXPENSES_TABLE = "CREATE TABLE " + TABLE_EXPENSES + "("
            + COLUMN_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TRIP_ID + " INTEGER, "
            + COLUMN_AMOUNT + " REAL, "
            + COLUMN_CATEGORY + " TEXT, "
            + COLUMN_DATE + " TEXT, "
            + COLUMN_DESCRIPTION + " TEXT, "
            + "FOREIGN KEY(" + COLUMN_TRIP_ID + ") REFERENCES " + TABLE_TRIPS + "(" + COLUMN_TRIP_ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_TRIPS_TABLE);
        db.execSQL(CREATE_EXPENSES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?";
        String[] selectionArgs = {username, password};
        
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        
        return count > 0;
    }

    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID}, 
                COLUMN_USERNAME + "=?", new String[]{username}, 
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Trip methods
    public long createTrip(int userId, String destination, String startDate, 
                         String endDate, double budget, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TRIP_USER_ID, userId);
        values.put(COLUMN_DESTINATION, destination);
        values.put(COLUMN_START_DATE, startDate);
        values.put(COLUMN_END_DATE, endDate);
        values.put(COLUMN_BUDGET, budget);
        values.put(COLUMN_NOTES, notes);
        return db.insert(TABLE_TRIPS, null, values);
    }

    public boolean deleteTrip(long tripId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // First delete all expenses for this trip
        db.delete(TABLE_EXPENSES, COLUMN_TRIP_ID + "=?", new String[]{String.valueOf(tripId)});
        // Then delete the trip
        return db.delete(TABLE_TRIPS, COLUMN_TRIP_ID + "=?", 
                new String[]{String.valueOf(tripId)}) > 0;
    }

    public Cursor getAllTrips(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_TRIPS, null, COLUMN_TRIP_USER_ID + "=?", 
                new String[]{String.valueOf(userId)}, null, null, 
                COLUMN_START_DATE + " DESC");
    }

    public Cursor getTripDetails(long tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_TRIPS, null, COLUMN_TRIP_ID + "=?", 
                new String[]{String.valueOf(tripId)}, null, null, null);
    }

    // Expense methods
    public long addExpense(long tripId, double amount, String category, 
                         String date, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TRIP_ID, tripId);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_DESCRIPTION, description);
        return db.insert(TABLE_EXPENSES, null, values);
    }

    public Cursor getTripExpenses(long tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_EXPENSES, null, COLUMN_TRIP_ID + "=?", 
                new String[]{String.valueOf(tripId)}, null, null, 
                COLUMN_DATE + " DESC");
    }

    public double getTotalExpenses(long tripId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_EXPENSES + 
                " WHERE " + COLUMN_TRIP_ID + "=?",
                new String[]{String.valueOf(tripId)});
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID}, 
                COLUMN_USERNAME + "=?", new String[]{username}, 
                null, null, null);
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
        }
        cursor.close();
        return userId;
    }

    // Add this method to the DatabaseHelper class
    public boolean updatePassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);
        
        int rowsAffected = db.update(TABLE_USERS, values, 
            COLUMN_USERNAME + "=?", new String[]{username});
        return rowsAffected > 0;
    }

    public boolean deleteExpense(long expenseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EXPENSES, COLUMN_EXPENSE_ID + "=?", 
                new String[]{String.valueOf(expenseId)}) > 0;
    }
} 