package com.example.tripwallet;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.NumberFormat;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private TextInputEditText currentPasswordInput, newPasswordInput, confirmNewPasswordInput;
    private String username;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Get current username
        SharedPreferences prefs = getSharedPreferences("TripWalletPrefs", MODE_PRIVATE);
        username = prefs.getString("username", "");

        // Initialize views
        TextView usernameDisplay = findViewById(R.id.usernameDisplay);
        currentPasswordInput = findViewById(R.id.currentPasswordInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmNewPasswordInput = findViewById(R.id.confirmNewPasswordInput);
        MaterialButton updatePasswordButton = findViewById(R.id.updatePasswordButton);
        TextView totalTripsText = findViewById(R.id.totalTripsText);
        TextView totalExpensesText = findViewById(R.id.totalExpensesText);

        // Display current username
        usernameDisplay.setText(username);

        // Load statistics
        loadStatistics(totalTripsText, totalExpensesText);

        // Set click listener
        updatePasswordButton.setOnClickListener(v -> updatePassword());
    }

    private void loadStatistics(TextView totalTripsText, TextView totalExpensesText) {
        int userId = dbHelper.getUserId(username);
        
        // Get total trips
        Cursor tripsCursor = dbHelper.getAllTrips(userId);
        int totalTrips = tripsCursor.getCount();
        tripsCursor.close();
        
        // Calculate total expenses across all trips
        double totalExpenses = 0;
        tripsCursor = dbHelper.getAllTrips(userId);
        while(tripsCursor.moveToNext()) {
            long tripId = tripsCursor.getLong(tripsCursor.getColumnIndex(DatabaseHelper.COLUMN_TRIP_ID));
            totalExpenses += dbHelper.getTotalExpenses(tripId);
        }
        tripsCursor.close();

        // Update UI
        totalTripsText.setText(String.valueOf(totalTrips));
        totalExpensesText.setText(currencyFormat.format(totalExpenses));
    }

    private void updatePassword() {
        String currentPassword = String.valueOf(currentPasswordInput.getText());
        String newPassword = String.valueOf(newPasswordInput.getText());
        String confirmNewPassword = String.valueOf(confirmNewPasswordInput.getText());

        // Validate inputs
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, R.string.password_too_short, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if current password is correct
        if (!dbHelper.checkUser(username, currentPassword)) {
            Toast.makeText(this, R.string.current_password_incorrect, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if new passwords match
        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(this, R.string.passwords_not_matching, Toast.LENGTH_SHORT).show();
            return;
        }

        // Update password
        if (dbHelper.updatePassword(username, newPassword)) {
            Toast.makeText(this, R.string.password_updated, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, R.string.password_update_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 