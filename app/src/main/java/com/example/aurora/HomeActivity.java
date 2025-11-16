package com.example.aurora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView; // Kept for future use, but not used in updateUI

import com.google.android.material.switchmaterial.SwitchMaterial;

public class HomeActivity extends AppCompatActivity {

    private SwitchMaterial themeSwitch;
    private Button nextButton;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load saved preference before setting theme
        prefs = getSharedPreferences("AURA_PREFS", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);

        // Set default night mode based on saved preference
        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        themeSwitch = findViewById(R.id.themeSwitch);
        nextButton = findViewById(R.id.nextButton);

        // Set switch state and update UI
        themeSwitch.setChecked(isDark);
        updateUI(isDark);

        // Listen for theme changes
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Apply night mode
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );

            // Save preference
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            // The activity will likely restart to apply the new theme.
            // This call ensures colors update immediately if it doesn't.
            updateUI(isChecked);
        });

        // Next button listener
        nextButton.setOnClickListener(v -> {
            Log.d("HomeActivity", "Next button clicked!"); // Log for debugging
            Intent intent = new Intent(HomeActivity.this, JournalWriteActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Update UI colors based on current theme.
     */
    private void updateUI(boolean isDark) {
        // Update layout background
        int bgRes = isDark ? R.drawable.aurora_gradient_dark : R.drawable.aurora_gradient_light;
        findViewById(R.id.homeLayout).setBackgroundResource(bgRes);

        // Update button colors
        nextButton.setBackgroundColor(ContextCompat.getColor(this, isDark ? R.color.aurora_green : R.color.sky_blue));
        nextButton.setTextColor(ContextCompat.getColor(this, isDark ? R.color.soft_white : R.color.black));

        // Update switch text color
        themeSwitch.setTextColor(ContextCompat.getColor(this, isDark ? R.color.soft_white : R.color.black));
    }
}
