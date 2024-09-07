package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RateProductActivity extends AppCompatActivity {

    EditText etRating;
    Button btnSubmitRating;
    DatabaseHelper db;
    long productId;
    long userId; // Retrieve this from user session or login data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_product);

        etRating = findViewById(R.id.etRating);
        btnSubmitRating = findViewById(R.id.btnSubmitRating);
        db = new DatabaseHelper(this);

        // Retrieve product ID and user ID from intent
        productId = getIntent().getLongExtra("productId", -1);
        userId = getIntent().getLongExtra("userId", -1); // Assuming userId is passed or retrieved from session

        btnSubmitRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ratingText = etRating.getText().toString();

                // Validate rating input
                if (ratingText.isEmpty()) {
                    Toast.makeText(RateProductActivity.this, "Please enter a rating", Toast.LENGTH_SHORT).show();
                    return;
                }

                int rating;
                try {
                    rating = Integer.parseInt(ratingText);
                } catch (NumberFormatException e) {
                    Toast.makeText(RateProductActivity.this, "Rating must be an integer", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if rating is within the valid range
                if (rating < 0 || rating > 5) {
                    Toast.makeText(RateProductActivity.this, "Rating must be between 0 and 5", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isInserted = db.insertRating(productId, userId, rating);
                if (isInserted) {
                    double averageRating = db.getAverageRating(productId);
                    db.updateProductAverageRating(productId, averageRating);
                    Toast.makeText(RateProductActivity.this, "Rating submitted successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity and return to main screen
                } else {
                    Toast.makeText(RateProductActivity.this, "Failed to submit rating", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
