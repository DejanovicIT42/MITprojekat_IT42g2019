package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Lilly.db";
    private static final String TABLE_NAME = "users";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "USERNAME";
    private static final String COL_3 = "PASSWORD";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    // Adding ratings table
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (ID INTEGER PRIMARY KEY AUTOINCREMENT, USERNAME TEXT, PASSWORD TEXT)");
        db.execSQL("CREATE TABLE products (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, DESCRIPTION TEXT, AVERAGE_RATING REAL)");
        db.execSQL("CREATE TABLE ratings (ID INTEGER PRIMARY KEY AUTOINCREMENT, PRODUCT_ID INTEGER, USER_ID INTEGER, RATING REAL, FOREIGN KEY(PRODUCT_ID) REFERENCES products(ID), FOREIGN KEY(USER_ID) REFERENCES users(ID))");
    }

    // Method to insert a rating into the database
    public boolean insertRating(long productId, long userId, double rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("PRODUCT_ID", productId);
        contentValues.put("USER_ID", userId);
        contentValues.put("RATING", rating);
        long result = db.insert("ratings", null, contentValues);
        return result != -1;
    }

    // Method to retrieve all ratings for a product
    public Cursor getRatingsForProduct(long productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM ratings WHERE PRODUCT_ID=?", new String[]{String.valueOf(productId)});
    }

    // Method to calculate average rating for a product
    public double getAverageRating(long productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT AVG(RATING) FROM ratings WHERE PRODUCT_ID=?", new String[]{String.valueOf(productId)});
        if (cursor.moveToFirst()) {
            return cursor.getDouble(0);
        }
        return 0;
    }

    // Method to update the average rating of a product
    public boolean updateProductAverageRating(long productId, double averageRating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("AVERAGE_RATING", averageRating);
        int result = db.update("products", contentValues, "ID=?", new String[]{String.valueOf(productId)});
        return result > 0;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, username);
        contentValues.put(COL_3, password);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE USERNAME=? AND PASSWORD=?", new String[]{username, password});
        return cursor.getCount() > 0;
    }
    // Method to update product rating in the database
    public boolean updateProductRating(long productId, double newRating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("RATING", newRating);
        int result = db.update("products", contentValues, "ID=?", new String[]{String.valueOf(productId)});
        return result > 0;
    }
    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM products", null);
    }

    // Method to update product details
    public boolean updateProduct(long productId, String name, String description, double averageRating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("DESCRIPTION", description);
        contentValues.put("AVERAGE_RATING", averageRating);

        int result = db.update("products", contentValues, "ID=?", new String[]{String.valueOf(productId)});
        return result > 0;
    }

    // Method to delete a product
    public boolean deleteProduct(long productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete all ratings associated with this product
        db.delete("ratings", "PRODUCT_ID=?", new String[]{String.valueOf(productId)});
        // Delete the product itself
        int result = db.delete("products", "ID=?", new String[]{String.valueOf(productId)});
        return result > 0;
    }

    // Method to get a product by its ID
    public Cursor getProductById(long productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM products WHERE ID=?", new String[]{String.valueOf(productId)});
    }

    // Method to delete a rating by its ID
    public boolean deleteRating(long ratingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("ratings", "ID=?", new String[]{String.valueOf(ratingId)});
        return result > 0;
    }

    // Method to insert a new product into the database
    public boolean insertProduct(String name, String description, double averageRating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("DESCRIPTION", description);
        contentValues.put("AVERAGE_RATING", averageRating);

        long result = db.insert("products", null, contentValues);
        return result != -1;
    }



}
