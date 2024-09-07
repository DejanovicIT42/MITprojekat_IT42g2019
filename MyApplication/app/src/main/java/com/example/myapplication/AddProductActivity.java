package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class AddProductActivity extends AppCompatActivity {

    EditText etProductName, etProductDescription, etProductRating;
    ImageView ivProductImage;
    Button btnAddProduct;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        etProductName = findViewById(R.id.etProductName);
        etProductDescription = findViewById(R.id.etProductDescription);
        etProductRating = findViewById(R.id.etProductRating);
        ivProductImage = findViewById(R.id.ivProductImage);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        db = new DatabaseHelper(this);

        // Set static drawable image
        ivProductImage.setImageResource(R.drawable.ic_launcher_background); // Replace with your static image resource

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etProductName.getText().toString();
                String description = etProductDescription.getText().toString();
                double rating;

                try {
                    rating = Double.parseDouble(etProductRating.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(AddProductActivity.this, "Invalid rating. Must be a number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convert static drawable image to byte array
                byte[] image = getImageBytes(R.drawable.ic_launcher_background); // Replace with your static image resource

                boolean isInserted = db.insertProduct(name, description, rating, image);
                if (isInserted) {
                    Toast.makeText(AddProductActivity.this, "Product added successfully!", Toast.LENGTH_SHORT).show();

                    finish(); // Close the current activity
                } else {
                    Toast.makeText(AddProductActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private byte[] getImageBytes(int drawableId) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawableId);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

