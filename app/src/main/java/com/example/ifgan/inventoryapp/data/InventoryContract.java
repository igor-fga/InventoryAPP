package com.example.ifgan.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ifgan on 04/09/2017.
 */

public final class InventoryContract {

    private InventoryContract() {
    }

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + InventoryProvider.CONTENT_AUTHORITY);

    public static final String PATH_INVENTORY = "inventoryapp";


    public static final class InvEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + InventoryProvider.CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + InventoryProvider.CONTENT_AUTHORITY + "/" + PATH_INVENTORY;


        public final static String TABLE_NAME = "inventory";

        /**
         * Unique ID number for the pet (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the product.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME = "name";

        /**
         * Amount of the product.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_AMOUNT = "amount";

        /**
         * Price of the product.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_PRICE = "price";

        /**
         * Sold products.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_SOLD = "sold";

        /**
         * Name of the provider.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_PROVIDER = "provider";

        /**
         * E-mail of the provider.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_PROVIDER_EMAIL = "email";

        /**
         * Image of the product.
         * <p>
         * Type: BLOB
         */
        public final static String COLUMN_PRODUCT_IMAGE = "image";


    }
}
