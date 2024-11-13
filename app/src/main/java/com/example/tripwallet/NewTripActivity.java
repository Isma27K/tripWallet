package com.example.tripwallet;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewTripActivity extends AppCompatActivity {
    private TextInputEditText destinationInput, startDateInput, endDateInput, 
                             budgetInput, notesInput;
    private DatabaseHelper dbHelper;
    private final Calendar startCalendar = Calendar.getInstance();
    private final Calendar endCalendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.new_trip);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DatabaseHelper(this);
        initializeViews();
        setupDatePickers();
        setupButtons();
    }

    private void initializeViews() {
        destinationInput = findViewById(R.id.destinationInput);
        startDateInput = findViewById(R.id.startDateInput);
        endDateInput = findViewById(R.id.endDateInput);
        budgetInput = findViewById(R.id.budgetInput);
        notesInput = findViewById(R.id.notesInput);
    }

    private void setupDatePickers() {
        startDateInput.setOnClickListener(v -> showDatePicker(true));
        endDateInput.setOnClickListener(v -> showDatePicker(false));
    }

    private void setupButtons() {
        MaterialButton saveButton = findViewById(R.id.saveButton);
        MaterialButton cancelButton = findViewById(R.id.cancelButton);

        saveButton.setOnClickListener(v -> saveTrip());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = isStartDate ? startCalendar : endCalendar;
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, day) -> {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, day);
                
                if (isStartDate) {
                    if (endDateInput.getText().length() > 0 && 
                        selectedDate.after(endCalendar)) {
                        Toast.makeText(this, R.string.start_date_after_end, 
                                     Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startCalendar.set(year, month, day);
                    startDateInput.setText(dateFormat.format(selectedDate.getTime()));
                } else {
                    if (startDateInput.getText().length() > 0 && 
                        selectedDate.before(startCalendar)) {
                        Toast.makeText(this, R.string.end_date_before_start, 
                                     Toast.LENGTH_SHORT).show();
                        return;
                    }
                    endCalendar.set(year, month, day);
                    endDateInput.setText(dateFormat.format(selectedDate.getTime()));
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void saveTrip() {
        String destination = String.valueOf(destinationInput.getText());
        String startDate = String.valueOf(startDateInput.getText());
        String endDate = String.valueOf(endDateInput.getText());
        String budgetStr = String.valueOf(budgetInput.getText());
        String notes = String.valueOf(notesInput.getText());

        if (destination.isEmpty() || startDate.isEmpty() || 
            endDate.isEmpty() || budgetStr.isEmpty()) {
            Toast.makeText(this, R.string.fill_required_fields, 
                         Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double budget = Double.parseDouble(budgetStr);
            
            SharedPreferences prefs = getSharedPreferences("TripWalletPrefs", MODE_PRIVATE);
            String username = prefs.getString("username", "");
            int userId = dbHelper.getUserId(username);

            long result = dbHelper.createTrip(userId, destination, startDate, 
                                           endDate, budget, notes);
            if (result != -1) {
                Toast.makeText(this, R.string.trip_saved, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.save_error, Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.invalid_budget, Toast.LENGTH_SHORT).show();
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