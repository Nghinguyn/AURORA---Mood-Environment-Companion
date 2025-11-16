package com.example.aurora;

import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class JourneyActivity extends AppCompatActivity {

    ListView journeyList;  // The ListView to display mood entries
    List<MoodEntry> moodEntries;  // List to store MoodEntry objects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);

        journeyList = findViewById(R.id.mood_list_layout);
        // Sample data for now (this will be dynamic in the future, possibly saved to a database)
        moodEntries = new ArrayList<>();

        // Example mood entries with date and description
        moodEntries.add(new MoodEntry("Happy 🌞", "Had a great day at work, feeling positive and energetic!"));
        moodEntries.add(new MoodEntry("Calm 🌿", "Relaxed and peaceful day, spent time outdoors."));
        moodEntries.add(new MoodEntry("Excited ✨", "Excited about the upcoming weekend plans!"));

        // Create a custom adapter (MoodUpdate) to bind the data to the ListView
        MoodUpdate adapter = new MoodUpdate(this, moodEntries);

        // Set the adapter to the ListView
        journeyList.setAdapter(adapter);
    }
}
