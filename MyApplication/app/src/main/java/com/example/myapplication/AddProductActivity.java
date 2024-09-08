package com.example.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

    private static final int PICK_IMAGE_REQUEST = 1;

    EditText etProductName, etProductDescription, etProductRating;
    ImageView ivProductImage;
    Button btnAddProduct, btnSelectImage;
    DatabaseHelper db;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        etProductName = findViewById(R.id.etProductName);
        etProductDescription = findViewById(R.id.etProductDescription);
        etProductRating = findViewById(R.id.etProductRating);
        ivProductImage = findViewById(R.id.ivProductImage);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        db = new DatabaseHelper(this);

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

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

                if (imageUri == null) {
                    Toast.makeText(AddProductActivity.this, "Please select an image.", Toast.LENGTH_SHORT).show();
                    return;
                }

                byte[] image = getImageBytes(imageUri);
                if (image == null) {
                    Toast.makeText(AddProductActivity.this, "Failed to load image.", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isInserted = db.insertProduct(name, description, rating, image);
                if (isInserted) {
                    Toast.makeText(AddProductActivity.this, "Product added successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity and return to main screen
                } else {
                    Toast.makeText(AddProductActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*"); // Filter for image files
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ivProductImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] getImageBytes(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}



