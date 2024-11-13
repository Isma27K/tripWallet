package com.example.tripwallet;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tripwallet.adapters.ExpenseAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import java.text.NumberFormat;
import java.util.Locale;

public class TripDetailsActivity extends AppCompatActivity implements ExpenseAdapter.OnExpenseDeleteListener {
    private DatabaseHelper dbHelper;
    private long tripId;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private RecyclerView expensesRecyclerView;
    private TextView noExpensesText;
    private ExpenseAdapter expenseAdapter;
    private TextView budgetWarningText;
    private ExtendedFloatingActionButton quickAddExpenseFab;
    private MaterialCardView notesCard;
    private CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        dbHelper = new DatabaseHelper(this);
        tripId = getIntent().getLongExtra("trip_id", -1);

        if (tripId == -1) {
            Toast.makeText(this, "Error loading trip details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(""); // Clear title as we're using custom title in collapsing toolbar

        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white, null));
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.white, null));

        initializeViews();
        setupExpensesList();
        setupButtons();
        setupFabBehavior();
    }

    private void initializeViews() {
        expensesRecyclerView = findViewById(R.id.expensesRecyclerView);
        noExpensesText = findViewById(R.id.noExpensesText);
        budgetWarningText = findViewById(R.id.budgetWarningText);
        quickAddExpenseFab = findViewById(R.id.quickAddExpenseFab);
        notesCard = findViewById(R.id.notesCard);
    }

    private void setupExpensesList() {
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        expenseAdapter = new ExpenseAdapter(this, null, this);
        expensesRecyclerView.setAdapter(expenseAdapter);
        loadExpenses();
    }

    private void setupButtons() {
        MaterialButton addExpenseButton = findViewById(R.id.addExpenseButton);
        MaterialButton deleteButton = findViewById(R.id.deleteButton);

        View.OnClickListener addExpenseListener = v -> {
            Intent intent = new Intent(this, ExpenseActivity.class);
            intent.putExtra("trip_id", tripId);
            startActivity(intent);
        };

        addExpenseButton.setOnClickListener(addExpenseListener);
        quickAddExpenseFab.setOnClickListener(addExpenseListener);
        deleteButton.setOnClickListener(v -> confirmDelete());
    }

    private void setupFabBehavior() {
        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            if (Math.abs(verticalOffset) == appBarLayout1.getTotalScrollRange()) {
                quickAddExpenseFab.shrink();
            } else {
                quickAddExpenseFab.extend();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTripDetails();
        loadExpenses();
    }

    private void loadTripDetails() {
        Cursor cursor = dbHelper.getTripDetails(tripId);
        if (cursor.moveToFirst()) {
            String destination = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESTINATION));
            String startDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_START_DATE));
            String endDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_END_DATE));
            double budget = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_BUDGET));
            String notes = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTES));
            double totalExpenses = dbHelper.getTotalExpenses(tripId);

            // Set destination as title in collapsing toolbar
            collapsingToolbar.setTitle(destination);

            // Update budget info
            TextView budgetText = findViewById(R.id.budgetText);
            TextView expensesText = findViewById(R.id.expensesText);
            budgetText.setText(currencyFormat.format(budget));
            expensesText.setText(currencyFormat.format(totalExpenses));

            // Handle budget warning
            if (totalExpenses > budget) {
                budgetWarningText.setVisibility(View.VISIBLE);
                budgetWarningText.setText(getString(R.string.over_budget_warning, 
                    currencyFormat.format(totalExpenses - budget)));
            } else {
                budgetWarningText.setVisibility(View.GONE);
            }

            // Handle notes
            if (notes != null && !notes.isEmpty()) {
                TextView notesText = findViewById(R.id.notesText);
                notesText.setText(notes);
                notesCard.setVisibility(View.VISIBLE);
            } else {
                notesCard.setVisibility(View.GONE);
            }
        }
        cursor.close();
    }

    private void loadExpenses() {
        Cursor cursor = dbHelper.getTripExpenses(tripId);
        if (cursor.getCount() == 0) {
            expensesRecyclerView.setVisibility(View.GONE);
            noExpensesText.setVisibility(View.VISIBLE);
        } else {
            expensesRecyclerView.setVisibility(View.VISIBLE);
            noExpensesText.setVisibility(View.GONE);
            expenseAdapter.swapCursor(cursor);
        }
    }

    @Override
    public void onExpenseDelete(long expenseId) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_expense)
                .setMessage(R.string.delete_expense_confirmation)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    if (dbHelper.deleteExpense(expenseId)) {
                        Toast.makeText(this, R.string.expense_deleted, Toast.LENGTH_SHORT).show();
                        loadTripDetails();
                        loadExpenses();
                    } else {
                        Toast.makeText(this, R.string.expense_delete_error, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_trip)
                .setMessage(R.string.delete_confirmation)
                .setPositiveButton(R.string.yes, (dialog, which) -> deleteTrip())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void deleteTrip() {
        if (dbHelper.deleteTrip(tripId)) {
            Toast.makeText(this, R.string.trip_deleted, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, R.string.trip_delete_error, Toast.LENGTH_SHORT).show();
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