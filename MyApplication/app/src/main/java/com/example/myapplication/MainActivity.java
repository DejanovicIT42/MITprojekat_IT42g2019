package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView tvWelcome;
    ListView lvProducts;
    Button btnAddProduct, btnModifyProduct;
    DatabaseHelper db;
    boolean isAdmin = false; // You can pass this value during login based on the user type

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvWelcome = findViewById(R.id.tvWelcome);
        lvProducts = findViewById(R.id.lvProducts);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnModifyProduct = findViewById(R.id.btnModifyProduct);

        db = new DatabaseHelper(this);

        // Check if user is admin
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        if (isAdmin) {
            findViewById(R.id.adminButtons).setVisibility(View.VISIBLE);
        }

        // Set up buttons for admins
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });

        btnModifyProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Assuming we have an activity to modify products
                Intent intent = new Intent(MainActivity.this, ModifyProductActivity.class);
                startActivity(intent);
            }
        });

        // Load and display products
        DisplayProducts();

        // Handle item click to rate product
        lvProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Open activity to rate the product
                Intent intent = new Intent(MainActivity.this, RateProductActivity.class);
                intent.putExtra("productId", id); // Pass product ID to the rating activity
                startActivity(intent);
            }
        });
    }

    // Method to load products from the database and display them in the ListView
    private void DisplayProducts() {
        Cursor cursor = db.getAllProducts();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No products found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare a list to hold product details
        ArrayList<String> productList = new ArrayList<>();
        while (cursor.moveToNext()) {
            long productId = cursor.getLong(0); // Column index 0 is for ID
            String productName = cursor.getString(1); // Column index 1 is for NAME
            double averageRating = cursor.getDouble(3); // Column index 3 is for AVERAGE_RATING
            productList.add(productName + " - Avg Rating: " + averageRating);
        }

        // Set up adapter to display product names and average ratings
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productList);
        lvProducts.setAdapter(adapter);
    }
}

