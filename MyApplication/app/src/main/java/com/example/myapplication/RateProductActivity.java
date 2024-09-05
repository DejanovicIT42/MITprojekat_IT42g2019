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
                double rating = Double.parseDouble(etRating.getText().toString());

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
