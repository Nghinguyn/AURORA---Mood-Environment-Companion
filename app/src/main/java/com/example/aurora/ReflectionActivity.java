package com.example.aurora;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ReflectionActivity extends AppCompatActivity {

    TextView reflectionMessage;
    Button shareButton, viewJourneyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reflection);

        reflectionMessage = findViewById(R.id.reflectionMessage);
        shareButton = findViewById(R.id.shareButton);
        viewJourneyButton = findViewById(R.id.viewJourneyButton);

        // Placeholder: generate reflection from mood input
        String mood = getIntent().getStringExtra("mood_text");
        reflectionMessage.setText("You feel: " + mood + "\nKeep shining! 🌟");

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, reflectionMessage.getText());
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });

        viewJourneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReflectionActivity.this, JourneyActivity.class));
            }
        });
    }
}
