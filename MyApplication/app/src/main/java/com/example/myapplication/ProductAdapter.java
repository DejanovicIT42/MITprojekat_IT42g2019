package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ProductAdapter extends BaseAdapter {

    private ArrayList<Product> products;
    private LayoutInflater inflater;

    public ProductAdapter(Context context, ArrayList<Product> products) {
        this.products = products;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return products.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_product, parent, false);
        }

        ImageView ivProductImage = convertView.findViewById(R.id.ivProductImage);
        TextView tvProductName = convertView.findViewById(R.id.tvProductName);
        TextView tvProductRating = convertView.findViewById(R.id.tvProductRating);

        Product product = products.get(position);

        ivProductImage.setImageBitmap(product.image);
        tvProductName.setText(product.name);
        tvProductRating.setText("Avg Rating: " + product.rating);

        return convertView;
    }

    public static class Product {
        long id;
        String name;
        double rating;
        Bitmap image;

        public Product(long id, String name, double rating, Bitmap image) {
            this.id = id;
            this.name = name;
            this.rating = rating;
            this.image = image;
        }
    }
}