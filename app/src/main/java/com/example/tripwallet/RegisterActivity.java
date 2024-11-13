package com.example.tripwallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.card.MaterialCardView;

public class RegisterActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private TextInputEditText usernameInput, passwordInput, confirmPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        Button registerButton = findViewById(R.id.registerButton);
        TextView loginLink = findViewById(R.id.loginLink);

        registerButton.setOnClickListener(v -> attemptRegistration());
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        // Add animations
        TextView welcomeText = findViewById(R.id.welcomeText);
        TextView subtitleText = findViewById(R.id.subtitleText);
        MaterialCardView cardView = findViewById(R.id.registerCard);

        // Animate welcome text
        welcomeText.setAlpha(0f);
        welcomeText.setTranslationY(50);
        welcomeText.animate()
                .alpha(1f)
                .translationY(0)
                .setDuration(1000)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Animate subtitle with delay
        subtitleText.setAlpha(0f);
        subtitleText.setTranslationY(50);
        subtitleText.animate()
                .alpha(1f)
                .translationY(0)
                .setStartDelay(300)
                .setDuration(1000)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Animate card with delay
        cardView.setAlpha(0f);
        cardView.setTranslationY(100);
        cardView.animate()
                .alpha(1f)
                .translationY(0)
                .setStartDelay(500)
                .setDuration(1000)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void attemptRegistration() {
        String username = String.valueOf(usernameInput.getText());
        String password = String.valueOf(passwordInput.getText());
        String confirmPassword = String.valueOf(confirmPasswordInput.getText());

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, R.string.passwords_not_matching, Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6){
            Toast.makeText(this, R.string.password_too_short, Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.checkUsername(username)) {
            Toast.makeText(this, R.string.username_exists, Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.registerUser(username, password)) {
            Toast.makeText(this, R.string.registration_successful, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, R.string.registration_failed, Toast.LENGTH_SHORT).show();
        }
    }
} 