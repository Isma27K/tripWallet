package com.example.tripwallet;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ExpenseActivity extends AppCompatActivity {
    private TextInputEditText amountInput, dateInput, descriptionInput;
    private AutoCompleteTextView categoryInput;
    private DatabaseHelper dbHelper;
    private long tripId;
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        tripId = getIntent().getLongExtra("trip_id", -1);
        if (tripId == -1) {
            Toast.makeText(this, "Error loading trip", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.add_expense);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DatabaseHelper(this);
        initializeViews();
        setupDatePicker();
        setupCategoryInput();

        MaterialButton saveButton = findViewById(R.id.saveButton);
        MaterialButton backButton = findViewById(R.id.backButton);

        saveButton.setOnClickListener(v -> saveExpense());
        backButton.setOnClickListener(v -> finish());
    }

    private void initializeViews() {
        amountInput = findViewById(R.id.amountInput);
        dateInput = findViewById(R.id.dateInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        categoryInput = findViewById(R.id.categorySpinner);
    }

    private void setupDatePicker() {
        dateInput.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, day) -> {
                calendar.set(year, month, day);
                dateInput.setText(dateFormat.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 
               calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void setupCategoryInput() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.expense_categories, android.R.layout.simple_dropdown_item_1line);
        categoryInput.setAdapter(adapter);
    }

    private void saveExpense() {
        String amountStr = String.valueOf(amountInput.getText());
        String date = String.valueOf(dateInput.getText());
        String description = String.valueOf(descriptionInput.getText());
        String category = String.valueOf(categoryInput.getText());

        if (amountStr.isEmpty() || date.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, R.string.fill_required_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            long result = dbHelper.addExpense(tripId, amount, category, date, description);
            if (result != -1) {
                Toast.makeText(this, R.string.expense_saved, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.expense_save_error, Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.invalid_amount, Toast.LENGTH_SHORT).show();
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