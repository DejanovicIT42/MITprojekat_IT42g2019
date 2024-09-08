package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView data;
    String url;
    TextView tvWelcome;
    ListView lvProducts;
    Button btnAddProduct, btnModifyProduct;
    DatabaseHelper db;
    boolean isAdmin = false; // You can pass this value during login based on the user type

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = findViewById(R.id.data);
        url = "https://worldtimeapi.org/api/timezone/Europe/Belgrade";
//        tvWelcome = findViewById(R.id.tvWelcome);
        lvProducts = findViewById(R.id.lvProducts);
        btnAddProduct = findViewById(R.id.btnAddProduct);

        db = new DatabaseHelper(this);

        fetchCurrentTime();

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
                intent.putExtra("isAdmin", isAdmin);
                startActivity(intent);
            }
        });



        // Load and display products
        DisplayProducts();

        if(!isAdmin) {
            lvProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Open activity to rate the product
                    Intent intent = new Intent(MainActivity.this, RateProductActivity.class);
                    intent.putExtra("productId", id); // Pass product ID to the rating activity
                    intent.putExtra("isAdmin", isAdmin);
                    startActivity(intent);
                }
            });
        } else {
            lvProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, ModifyProductActivity.class);
                    intent.putExtra("productId", id); // Pass the product ID
                    startActivity(intent);
                }
            });
        }


    }

    // Method to load products from the database and display them in the ListView
    private void DisplayProducts() {
        Cursor cursor = db.getAllProducts();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No products found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare a list to hold product details
        ArrayList<ProductAdapter.Product> productList = new ArrayList<>();
        while (cursor.moveToNext()) {
            long productId = cursor.getLong(0); // Column index 0 is for ID
            String productName = cursor.getString(1); // Column index 1 is for NAME
            double averageRating = cursor.getDouble(3); // Column index 3 is for AVERAGE_RATING
            byte[] image = cursor.getBlob(4); // Column index 4 is for IMAGE

            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

            productList.add(new ProductAdapter.Product(productId, productName, averageRating, bitmap));
        }

        // Set up adapter to display product details
        ProductAdapter adapter = new ProductAdapter(this, productList);
        lvProducts.setAdapter(adapter);
    }

    //external api
    private void fetchCurrentTime() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://worldtimeapi.org/api/timezone/Europe/Belgrade";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String dateTime = response.getString("datetime");
                            data.setText("Current Time: " + dateTime);
                        } catch (Exception e) {
                            e.printStackTrace();
                            data.setText("String");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        data.setText("Error fetching time");
                    }
                });

        queue.add(jsonObjectRequest);
    }
}

