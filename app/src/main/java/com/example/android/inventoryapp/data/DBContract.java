package com.example.android.inventoryapp.data;


import android.provider.BaseColumns;

public class DBContract {

    public DBContract() {
    }

    public static final class ItemEntry implements BaseColumns {

        public static final String TABLE_NAME = "stock";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";
        public static final String COLUMN_SUPPLIER_EMAIL = "supplier_email";
        public static final String COLUMN_IMAGE = "image";

        public static final String CREATE_TABLE_STOCK = "CREATE TABLE " +
                DBContract.ItemEntry.TABLE_NAME + "(" +
                DBContract.ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DBContract.ItemEntry.COLUMN_NAME + " TEXT NOT NULL," +
                DBContract.ItemEntry.COLUMN_PRICE + " TEXT NOT NULL," +
                DBContract.ItemEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0," +
                DBContract.ItemEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL," +
                DBContract.ItemEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL," +
                DBContract.ItemEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL," +
                DBContract.ItemEntry.COLUMN_IMAGE + " TEXT NOT NULL" + ");";
    }
}
