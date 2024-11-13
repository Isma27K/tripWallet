package com.example.tripwallet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.AlphaAnimation;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 2500; // 2.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize views
        ImageView logoImage = findViewById(R.id.splashLogo);
        TextView appNameText = findViewById(R.id.appNameText);

        // Create animation set for logo
        AnimationSet logoAnimSet = new AnimationSet(true);
        
        // Scale animation for logo
        ScaleAnimation scaleAnim = new ScaleAnimation(
            0.5f, 1.0f, // Start and end X scale
            0.5f, 1.0f, // Start and end Y scale
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f, // Pivot X
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f  // Pivot Y
        );
        scaleAnim.setDuration(1000);
        
        // Fade animation for logo
        AlphaAnimation fadeAnim = new AlphaAnimation(0.0f, 1.0f);
        fadeAnim.setDuration(1000);
        
        // Add animations to set
        logoAnimSet.addAnimation(scaleAnim);
        logoAnimSet.addAnimation(fadeAnim);
        logoAnimSet.setInterpolator(new AccelerateDecelerateInterpolator());
        
        // Start logo animation
        logoImage.startAnimation(logoAnimSet);

        // Animate app name with delay
        appNameText.setAlpha(0f);
        appNameText.animate()
                .alpha(1f)
                .translationYBy(-30f)
                .setDuration(800)
                .setStartDelay(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Navigate to LoginActivity after splash duration
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            
            // Add activity transition animation
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DURATION);
    }
} 