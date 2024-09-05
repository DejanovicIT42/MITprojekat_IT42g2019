package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ModifyProductActivity extends AppCompatActivity {

    EditText etProductName, etProductDescription;
    Button btnUpdateProduct, btnDeleteProduct, btnViewRatings;
    ListView lvRatings;
    DatabaseHelper db;
    long productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_product);

        etProductName = findViewById(R.id.etProductName);
        etProductDescription = findViewById(R.id.etProductDescription);
        btnUpdateProduct = findViewById(R.id.btnUpdateProduct);
        btnDeleteProduct = findViewById(R.id.btnDeleteProduct);
        btnViewRatings = findViewById(R.id.btnViewRatings);
        lvRatings = findViewById(R.id.lvRatings);
        db = new DatabaseHelper(this);

        // Retrieve product ID from intent
        productId = getIntent().getLongExtra("productId", -1);
        loadProductDetails(productId);

        btnUpdateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etProductName.getText().toString();
                String description = etProductDescription.getText().toString();
                double averageRating = db.getAverageRating(productId); // Get current average rating

                boolean isUpdated = db.updateProduct(productId, name, description, averageRating);
                if (isUpdated) {
                    Toast.makeText(ModifyProductActivity.this, "Product updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ModifyProductActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isDeleted = db.deleteProduct(productId);
                if (isDeleted) {
                    Toast.makeText(ModifyProductActivity.this, "Product deleted successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity and return to main screen
                } else {
                    Toast.makeText(ModifyProductActivity.this, "Failed to delete product", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnViewRatings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayRatings();
            }
        });
    }

    // Load product details into fields
    private void loadProductDetails(long productId) {
        Cursor cursor = db.getProductById(productId);
        if (cursor.moveToFirst()) {
            etProductName.setText(cursor.getString(1)); // NAME
            etProductDescription.setText(cursor.getString(2)); // DESCRIPTION
        }
    }

    // Display ratings in ListView
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

        // Set item click listener to handle rating deletions
        lvRatings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve rating ID from cursor
                Cursor ratingCursor = db.getRatingsForProduct(productId);
                if (ratingCursor.moveToPosition(position)) {
                    long ratingId = ratingCursor.getLong(0); // Column index 0 is for ID
                    boolean isDeleted = db.deleteRating(ratingId);
                    if (isDeleted) {
                        Toast.makeText(ModifyProductActivity.this, "Rating deleted successfully!", Toast.LENGTH_SHORT).show();
                        displayRatings(); // Refresh the ratings list
                    } else {
                        Toast.makeText(ModifyProductActivity.this, "Failed to delete rating", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
