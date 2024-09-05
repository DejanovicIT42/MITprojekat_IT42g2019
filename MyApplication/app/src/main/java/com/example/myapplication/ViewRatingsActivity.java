package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ViewRatingsActivity extends AppCompatActivity {

    ListView lvRatings;
    DatabaseHelper db;
    long productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ratings);

        lvRatings = findViewById(R.id.lvRatings);
        db = new DatabaseHelper(this);

        // Retrieve product ID from intent
        productId = getIntent().getLongExtra("productId", -1);

        // Load and display ratings
        displayRatings();
    }

    private void displayRatings() {
        Cursor cursor = db.getRatingsForProduct(productId);
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No ratings found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare a list to hold rating details
        ArrayList<String> ratingList = new ArrayList<>();
        while (cursor.moveToNext()) {
            long userId = cursor.getLong(2); // Column index 2 is for USER_ID
            double rating = cursor.getDouble(3); // Column index 3 is for RATING
            ratingList.add("User " + userId + ": " + rating);
        }

        // Set up adapter to display ratings
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ratingList);
        lvRatings.setAdapter(adapter);
    }
}

