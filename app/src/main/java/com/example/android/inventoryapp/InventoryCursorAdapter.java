package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.DBContract;


public class InventoryCursorAdapter extends CursorAdapter {

    private final MainActivity activity;

    public InventoryCursorAdapter(MainActivity context, Cursor c) {
        super(context, c, 0);
        this.activity = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.product_name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.stock_qty);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        Button buy = (Button) view.findViewById(R.id.buy);
        ImageView image = (ImageView) view.findViewById(R.id.image_view);

        String name = cursor.getString(cursor.getColumnIndex(DBContract.ItemEntry.COLUMN_NAME));
        final int quantity = cursor.getInt(cursor.getColumnIndex(DBContract.ItemEntry.COLUMN_QUANTITY));
        String price = cursor.getString(cursor.getColumnIndex(DBContract.ItemEntry.COLUMN_PRICE));
        image.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(DBContract.ItemEntry.COLUMN_IMAGE))));

        nameTextView.setText(name);
        quantityTextView.setText(String.valueOf(quantity));
        priceTextView.setText(price);

        final long id = cursor.getLong(cursor.getColumnIndex(DBContract.ItemEntry._ID));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.clickOnViewItem(id);
            }
        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.clickOnBuyNow(id, quantity);
            }
        });
    }
}