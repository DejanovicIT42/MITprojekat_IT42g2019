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



import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class ModifyProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    EditText etProductName, etProductDescription;
    Button btnUpdateProduct, btnDeleteProduct, btnViewRatings, btnChooseImage;
    ImageView ivProductImage;
    ListView lvRatings;
    DatabaseHelper db;
    long productId;
    Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_product);

        etProductName = findViewById(R.id.etProductName);
        etProductDescription = findViewById(R.id.etProductDescription);
        btnUpdateProduct = findViewById(R.id.btnUpdateProduct);
        btnDeleteProduct = findViewById(R.id.btnDeleteProduct);
        btnViewRatings = findViewById(R.id.btnViewRatings);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        ivProductImage = findViewById(R.id.ivProductImage);
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
                double averageRating = db.getAverageRating(productId);

                byte[] image = null;
                if (selectedImage != null) {
                    image = getBytesFromBitmap(selectedImage);
                }

                boolean isUpdated = db.updateProduct(productId, name, description, averageRating, image);
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
                    finish();
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

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(inputStream);
                ivProductImage.setImageBitmap(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadProductDetails(long productId) {
        Cursor cursor = db.getProductById(productId);
        if (cursor.moveToFirst()) {
            etProductName.setText(cursor.getString(1));
            etProductDescription.setText(cursor.getString(2));
            byte[] image = cursor.getBlob(4);
            if (image != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                ivProductImage.setImageBitmap(bitmap);
            }
        }
    }

    private void displayRatings() {
        Cursor cursor = db.getRatingsForProduct(productId);
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No ratings found", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> ratingList = new ArrayList<>();
        while (cursor.moveToNext()) {
            long userId = cursor.getLong(2);
            double rating = cursor.getDouble(3);
            ratingList.add("User " + userId + ": " + rating);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ratingList);
        lvRatings.setAdapter(adapter);

        lvRatings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor ratingCursor = db.getRatingsForProduct(productId);
                if (ratingCursor.moveToPosition(position)) {
                    long ratingId = ratingCursor.getLong(0);
                    boolean isDeleted = db.deleteRating(ratingId);
                    if (isDeleted) {
                        Toast.makeText(ModifyProductActivity.this, "Rating deleted successfully!", Toast.LENGTH_SHORT).show();
                        displayRatings();
                    } else {
                        Toast.makeText(ModifyProductActivity.this, "Failed to delete rating", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }
}