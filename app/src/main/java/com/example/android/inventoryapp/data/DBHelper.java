package com.example.android.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    public final static String DB_NAME = "inventory.db";
    public final static int DB_VERSION = 1;
    public final static String LOG_TAG = DBHelper.class.getCanonicalName();

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.ItemEntry.CREATE_TABLE_STOCK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertItem(Inventory item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.ItemEntry.COLUMN_NAME, item.getProductName());
        values.put(DBContract.ItemEntry.COLUMN_PRICE, item.getPrice());
        values.put(DBContract.ItemEntry.COLUMN_QUANTITY, item.getQuantity());
        values.put(DBContract.ItemEntry.COLUMN_SUPPLIER_NAME, item.getSupplierName());
        values.put(DBContract.ItemEntry.COLUMN_SUPPLIER_PHONE, item.getSupplierPhone());
        values.put(DBContract.ItemEntry.COLUMN_SUPPLIER_EMAIL, item.getSupplierEmail());
        values.put(DBContract.ItemEntry.COLUMN_IMAGE, item.getImage());
        long id = db.insert(DBContract.ItemEntry.TABLE_NAME, null, values);
    }

    public Cursor readStock() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                DBContract.ItemEntry._ID,
                DBContract.ItemEntry.COLUMN_NAME,
                DBContract.ItemEntry.COLUMN_PRICE,
                DBContract.ItemEntry.COLUMN_QUANTITY,
                DBContract.ItemEntry.COLUMN_SUPPLIER_NAME,
                DBContract.ItemEntry.COLUMN_SUPPLIER_PHONE,
                DBContract.ItemEntry.COLUMN_SUPPLIER_EMAIL,
                DBContract.ItemEntry.COLUMN_IMAGE
        };
        Cursor cursor = db.query(
                DBContract.ItemEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    public Cursor readItem(long itemId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                DBContract.ItemEntry._ID,
                DBContract.ItemEntry.COLUMN_NAME,
                DBContract.ItemEntry.COLUMN_PRICE,
                DBContract.ItemEntry.COLUMN_QUANTITY,
                DBContract.ItemEntry.COLUMN_SUPPLIER_NAME,
                DBContract.ItemEntry.COLUMN_SUPPLIER_PHONE,
                DBContract.ItemEntry.COLUMN_SUPPLIER_EMAIL,
                DBContract.ItemEntry.COLUMN_IMAGE
        };
        String selection = DBContract.ItemEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(itemId)};

        Cursor cursor = db.query(
                DBContract.ItemEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        return cursor;
    }

    public void updateItem(long currentItemId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.ItemEntry.COLUMN_QUANTITY, quantity);
        String selection = DBContract.ItemEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(currentItemId)};
        db.update(DBContract.ItemEntry.TABLE_NAME,
                values, selection, selectionArgs);
    }

    public void sellOneItem(long itemId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        int newQuantity = 0;
        if (quantity > 0) {
            newQuantity = quantity - 1;
        }
        ContentValues values = new ContentValues();
        values.put(DBContract.ItemEntry.COLUMN_QUANTITY, newQuantity);
        String selection = DBContract.ItemEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(itemId)};
        db.update(DBContract.ItemEntry.TABLE_NAME,
                values, selection, selectionArgs);
    }
}