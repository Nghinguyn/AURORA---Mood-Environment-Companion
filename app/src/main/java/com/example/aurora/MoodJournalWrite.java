package com.example.aurora;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MoodJournalWrite extends Activity {

    private Spinner moodSpinner;
    private EditText descriptionInput;
    private Button saveMoodButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_journal_write);

        // Initialize views
        moodSpinner = findViewById(R.id.moodSpinner);
        descriptionInput = findViewById(R.id.descriptionInput);
        saveMoodButton = findViewById(R.id.saveMoodButton);

        // Set up the Spinner with mood options from the string-array in resources
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.mood_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moodSpinner.setAdapter(adapter);

        // Set button click listener to save the mood entry
        saveMoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected mood and description
                String selectedMood = moodSpinner.getSelectedItem().toString();
                String description = descriptionInput.getText().toString();

                // Check if description is empty (optional validation)
                if (selectedMood.isEmpty() || description.isEmpty()) {
                    Toast.makeText(MoodJournalWrite.this, "Please select mood and add a description", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new MoodEntry object
                MoodEntry newEntry = new MoodEntry(selectedMood, description);

                // Prepare result intent with the new mood entry
                Intent resultIntent = new Intent();
                resultIntent.putExtra("newMoodEntry", newEntry);

                // Return the data to JourneyActivity
                setResult(RESULT_OK, resultIntent);

                // Close the current activity
                finish();
            }
        });
    }
}
