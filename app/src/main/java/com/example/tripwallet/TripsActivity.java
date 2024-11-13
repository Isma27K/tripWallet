package com.example.tripwallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripwallet.adapters.TripAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class TripsActivity extends AppCompatActivity {
    private RecyclerView tripsRecyclerView;
    private TextView noTripsText;
    private DatabaseHelper dbHelper;
    private ExtendedFloatingActionButton addTripFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);

        dbHelper = new DatabaseHelper(this);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialize views
        tripsRecyclerView = findViewById(R.id.tripsRecyclerView);
        noTripsText = findViewById(R.id.noTripsText);
        addTripFab = findViewById(R.id.addTripFab);

        // Setup RecyclerView
        tripsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadTrips();

        // Handle FAB visibility on scroll
        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            if (Math.abs(verticalOffset) == appBarLayout1.getTotalScrollRange()) {
                addTripFab.shrink(); // Collapsed
            } else {
                addTripFab.extend(); // Expanded
            }
        });

        // Set FAB click listener
        addTripFab.setOnClickListener(v -> 
            startActivity(new Intent(TripsActivity.this, NewTripActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTrips();
    }

    private void loadTrips() {
        SharedPreferences prefs = getSharedPreferences("TripWalletPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "");
        int userId = dbHelper.getUserId(username);
        Cursor cursor = dbHelper.getAllTrips(userId);

        if (cursor.getCount() == 0) {
            tripsRecyclerView.setVisibility(View.GONE);
            noTripsText.setVisibility(View.VISIBLE);
        } else {
            tripsRecyclerView.setVisibility(View.VISIBLE);
            noTripsText.setVisibility(View.GONE);
            TripAdapter adapter = new TripAdapter(cursor, dbHelper);
            tripsRecyclerView.setAdapter(adapter);
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