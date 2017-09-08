package com.example.ifgan.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ifgan.inventoryapp.data.InventoryContract.InvEntry;

/**
 * Created by ifgan on 04/09/2017.
 */

public class InvDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = InvDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "invent.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link InvDbHelper}.
     *
     * @param context of the app
     */
    public InvDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the inventory table
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + InvEntry.TABLE_NAME + " ("
                + InvEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InvEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + InvEntry.COLUMN_PRODUCT_AMOUNT + " INTEGER NOT NULL DEFAULT 0, "
                + InvEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + InvEntry.COLUMN_PRODUCT_SOLD + " INTEGER NOT NULL DEFAULT 0, "
                + InvEntry.COLUMN_PRODUCT_PROVIDER + " TEXT NOT NULL, "
                + InvEntry.COLUMN_PRODUCT_PROVIDER_EMAIL + " TEXT NOT NULL, "
                + InvEntry.COLUMN_PRODUCT_IMAGE + " BLOB );";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
