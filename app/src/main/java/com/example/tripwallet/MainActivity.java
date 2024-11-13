package com.example.tripwallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripwallet.adapters.TripAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "TripWalletPrefs";
    private static final String KEY_USERNAME = "username";

    private TextView welcomeText;
    private RecyclerView recentTripsRecyclerView;
    private TextView noTripsText;
    private DatabaseHelper dbHelper;
    private ExtendedFloatingActionButton quickAddTripFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        welcomeText = findViewById(R.id.welcomeText);
        recentTripsRecyclerView = findViewById(R.id.recentTripsRecyclerView);
        noTripsText = findViewById(R.id.noTripsText);
        quickAddTripFab = findViewById(R.id.quickAddTripFab);
        
        MaterialButton newTripButton = findViewById(R.id.newTripButton);
        MaterialButton viewTripsButton = findViewById(R.id.viewTripsButton);
        MaterialButton manageProfileButton = findViewById(R.id.manageProfileButton);
        MaterialButton logoutButton = findViewById(R.id.logoutButton);

        dbHelper = new DatabaseHelper(this);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Set welcome message
        String username = getUsername();
        welcomeText.setText(getString(R.string.welcome_user, username));

        // Setup RecyclerView
        recentTripsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadTrips();

        // Handle FAB visibility on scroll
        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            if (Math.abs(verticalOffset) == appBarLayout1.getTotalScrollRange()) {
                quickAddTripFab.shrink(); // Collapsed
            } else {
                quickAddTripFab.extend(); // Expanded
            }
        });

        // Set click listeners
        View.OnClickListener newTripListener = v -> 
            startActivity(new Intent(MainActivity.this, NewTripActivity.class));
        
        newTripButton.setOnClickListener(newTripListener);
        quickAddTripFab.setOnClickListener(newTripListener);

        viewTripsButton.setOnClickListener(v -> 
            startActivity(new Intent(MainActivity.this, TripsActivity.class)));

        manageProfileButton.setOnClickListener(v -> 
            startActivity(new Intent(MainActivity.this, ProfileActivity.class)));

        logoutButton.setOnClickListener(v -> logout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTrips();
    }

    private String getUsername() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(KEY_USERNAME, "User");
    }

    private void logout() {
        // Clear stored username
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.remove(KEY_USERNAME);
        editor.apply();

        // Return to login screen
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadTrips() {
        String username = getUsername();
        int userId = dbHelper.getUserId(username);
        Cursor cursor = dbHelper.getAllTrips(userId);

        if (cursor.getCount() == 0) {
            recentTripsRecyclerView.setVisibility(View.GONE);
            noTripsText.setVisibility(View.VISIBLE);
        } else {
            recentTripsRecyclerView.setVisibility(View.VISIBLE);
            noTripsText.setVisibility(View.GONE);
            TripAdapter adapter = new TripAdapter(cursor, dbHelper);
            recentTripsRecyclerView.setAdapter(adapter);
        }
    }
}