package com.example.aurora;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class JournalWriteActivity extends AppCompatActivity {

    private static final String TAG = "JournalWriteActivity";

    // Declare UI elements
    private Spinner moodSpinner;
    private EditText titleInput;
    private EditText descriptionInput;
    private Button aiButton;
    private Button saveMoodButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_write); // Load layout

        // Initialize UI elements
        moodSpinner = findViewById(R.id.moodSpinner);
        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        aiButton = findViewById(R.id.aiButton);
        saveMoodButton = findViewById(R.id.saveMoodButton);

        // Check if the UI elements are initialized properly
        if (moodSpinner == null || titleInput == null || descriptionInput == null || aiButton == null || saveMoodButton == null) {
            Log.e(TAG, "Error: One or more UI components were not initialized properly.");
            Toast.makeText(this, "UI initialization failed!", Toast.LENGTH_SHORT).show();
            finish();  // Exit the activity if initialization fails
            return;
        }

        // Set up the mood spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.mood_array,  // Define this array in res/values/strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moodSpinner.setAdapter(adapter);

        // Set up the save button listener
        saveMoodButton.setOnClickListener(view -> saveJournalEntry());

        // Set up the AI analysis button (coming soon feature)
        aiButton.setOnClickListener(view -> {
            Toast.makeText(this, "AI Analysis is coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    // Save journal entry function
    private void saveJournalEntry() {
        // Get data from the UI elements
        String selectedMood = moodSpinner.getSelectedItem().toString();
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        // Validate inputs
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill out both title and description.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedMood.isEmpty()) {
            Toast.makeText(this, "Please select a mood.", Toast.LENGTH_SHORT).show();
            return;
        }

        // If everything is valid, show a confirmation toast
        Toast.makeText(this, "Entry saved: " + selectedMood, Toast.LENGTH_SHORT).show();

        // I will save the entry to a database or SharedPreferences here later
        // For now, reset the fields after saving
        resetFields();
    }

    // Reset fields after saving
    private void resetFields() {
        titleInput.setText("");
        descriptionInput.setText("");
        moodSpinner.setSelection(0);  // Reset the spinner to the first item
    }
}
