package com.example.aurora;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class HomeActivity extends AppCompatActivity {
    private SwitchMaterial themeSwitch;
    private SharedPreferences prefs;
    private Button nextButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load saved preference before setting theme
        prefs = getSharedPreferences("AURA_PREFS", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);
        // Set default night mode based on the saved preference
        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        themeSwitch = findViewById(R.id.themeSwitch);
        nextButton = findViewById(R.id.nextButton);
        themeSwitch.setChecked(isDark);  // Reflect saved state
        // Update UI based on the current theme
        updateUI(isDark);
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Change the theme and save the preference
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            // Save the theme preference
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            // Update UI after theme change
            updateUI(isChecked);
        });
    }
    private void updateUI(boolean isDark) {
        // Set button background color based on theme
        if (isDark) {
            nextButton.setBackgroundColor(getResources().getColor(R.color.aurora_green));  // Dark mode button color
        } else {
            nextButton.setBackgroundColor(getResources().getColor(R.color.sky_blue));  // Light mode button color
        }
        // Update layout background color based on theme
        if (isDark) {
            findViewById(R.id.activity_home).setBackgroundResource(R.drawable.aurora_gradient_dark);  // Dark mode gradient
        } else {
            findViewById(R.id.homeLayout).setBackgroundResource(R.drawable.aurora_gradient_light);  // Light mode gradient
        }
    }
}
