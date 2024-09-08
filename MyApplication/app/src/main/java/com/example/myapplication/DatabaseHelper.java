package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Lilly4.db";
    private static final String TABLE_NAME = "users";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "USERNAME";
    private static final String COL_3 = "PASSWORD";
    private static final String COL_4 = "IS_ADMIN"; // For admin status

    // Product table columns
    private static final String PROD_TABLE = "products";
    private static final String PROD_ID = "ID";
    private static final String PROD_NAME = "NAME";
    private static final String PROD_DESC = "DESCRIPTION";
    private static final String PROD_AVG_RATING = "AVERAGE_RATING";
    private static final String PROD_IMAGE = "IMAGE"; // New column for image

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        db.execSQL("CREATE TABLE users (ID INTEGER PRIMARY KEY AUTOINCREMENT, USERNAME TEXT, PASSWORD TEXT, IS_ADMIN INTEGER DEFAULT 1)");

        // Create products table with an additional column for images
        db.execSQL("CREATE TABLE products (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, DESCRIPTION TEXT, AVERAGE_RATING REAL, IMAGE BLOB)");

        // Create ratings table
        db.execSQL("CREATE TABLE ratings (ID INTEGER PRIMARY KEY AUTOINCREMENT, PRODUCT_ID INTEGER, USER_ID INTEGER, RATING REAL, FOREIGN KEY(PRODUCT_ID) REFERENCES products(ID), FOREIGN KEY(USER_ID) REFERENCES users(ID))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PROD_TABLE);
        db.execSQL("DROP TABLE IF EXISTS ratings");
        onCreate(db);
    }

    // Insert user with admin status
    public boolean insertUser(String username, String password, boolean isAdmin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, username);
        contentValues.put("PASSWORD", password);
        contentValues.put(COL_4, isAdmin ? 1 : 0);
        long result = db.insert("users", null, contentValues);
        return result != -1;
    }

    // Check if user exists
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE USERNAME=? AND PASSWORD=?", new String[]{username, password});
        return cursor.getCount() > 0;
    }

    // Insert a rating into the database
    public boolean insertRating(long productId, long userId, double rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("PRODUCT_ID", productId);
        contentValues.put("USER_ID", userId);
        contentValues.put("RATING", rating);
        long result = db.insert("ratings", null, contentValues);
        return result != -1;
    }

    // Get ratings for a product
    public Cursor getRatingsForProduct(long productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM ratings WHERE PRODUCT_ID=?", new String[]{String.valueOf(productId)});
    }

    // Get average rating for a product
    public double getAverageRating(long productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT AVG(RATING) FROM ratings WHERE PRODUCT_ID=?", new String[]{String.valueOf(productId)});
        if (cursor.moveToFirst()) {
            return cursor.getDouble(0);
        }
        return 0;
    }

    // Update average rating of a product
    public boolean updateProductAverageRating(long productId, double averageRating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("AVERAGE_RATING", averageRating);
        int result = db.update("products", contentValues, "ID=?", new String[]{String.valueOf(productId)});
        return result > 0;
    }

    // Insert a new product into the database with an image
    public boolean insertProduct(String name, String description, double averageRating, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROD_NAME, name);
        contentValues.put(PROD_DESC, description);
        contentValues.put(PROD_AVG_RATING, averageRating);
        contentValues.put(PROD_IMAGE, image); // Insert image as BLOB

        long result = db.insert(PROD_TABLE, null, contentValues);
        return result != -1;
    }

    // Get all products
    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + PROD_TABLE, null);
    }

    // Update product details including image
    public boolean updateProduct(long productId, String name, String description, double averageRating, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROD_NAME, name);
        contentValues.put(PROD_DESC, description);
        contentValues.put(PROD_AVG_RATING, averageRating);
        contentValues.put(PROD_IMAGE, image); // Update image

        int result = db.update(PROD_TABLE, contentValues, "ID=?", new String[]{String.valueOf(productId)});
        return result > 0;
    }

    // Get product by ID
    public Cursor getProductById(long productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + PROD_TABLE + " WHERE ID=?", new String[]{String.valueOf(productId)});
    }

    // Delete a product
    public boolean deleteProduct(long productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete all ratings associated with this product
        db.delete("ratings", "PRODUCT_ID=?", new String[]{String.valueOf(productId)});
        // Delete the product itself
        int result = db.delete(PROD_TABLE, "ID=?", new String[]{String.valueOf(productId)});
        return result > 0;
    }

    // Delete a rating by ID
    public boolean deleteRating(long ratingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("ratings", "ID=?", new String[]{String.valueOf(ratingId)});
        return result > 0;
    }

    // Check if user is admin
    public boolean isAdmin(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT IS_ADMIN FROM users WHERE USERNAME=?", new String[]{username});
        if (cursor.moveToFirst()) {
            int isAdmin = cursor.getInt(0);
            cursor.close();
            return isAdmin == 1;
        }
        cursor.close();
        return false;
    }
}

