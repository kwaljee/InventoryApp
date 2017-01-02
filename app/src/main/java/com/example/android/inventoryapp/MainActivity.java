package com.example.android.inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.android.inventoryapp.data.DBHelper;
import com.example.android.inventoryapp.data.Inventory;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = MainActivity.class.getCanonicalName();
    DBHelper dbHelper;
    InventoryCursorAdapter adapter;
    int lastVisibleItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ItemDetailActivity.class);
                startActivity(intent);
            }
        });

        final ListView listView = (ListView) findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        Cursor cursor = dbHelper.readStock();

        adapter = new InventoryCursorAdapter(this, cursor);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.swapCursor(dbHelper.readStock());
    }

    public void clickOnViewItem(long id) {
        Intent intent = new Intent(this, ItemDetailActivity.class);
        intent.putExtra("itemId", id);
        startActivity(intent);
    }

    public void clickOnBuyNow(long id, int quantity) {
        dbHelper.sellOneItem(id, quantity);
        adapter.swapCursor(dbHelper.readStock());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_import:
                importData();
                adapter.swapCursor(dbHelper.readStock());
        }
        return super.onOptionsItemSelected(item);
    }

    private void importData() {
        Inventory apples = new Inventory(
                "Apples",
                "2.10",
                34,
                "Golden State",
                "+1 800 277 3577",
                "apples@apples.com",
                "android.resource://com.example.android.inventoryapp/drawable/apples");
        dbHelper.insertItem(apples);

        Inventory carrots = new Inventory(
                "Carrots",
                "1.59",
                45,
                "Southern Ontario Farmers",
                "+1 800 200 0000",
                "carrots@carrots.com",
                "android.resource://com.example.android.inventoryapp/drawable/carrots");
        dbHelper.insertItem(carrots);

        Inventory lettuce = new Inventory(
                "Lettuce",
                "1.50",
                65,
                "State Farms",
                "+1 800 370 2222",
                "lettuce@lettuce.com",
                "android.resource://com.example.android.inventoryapp/drawable/lettuce");
        dbHelper.insertItem(lettuce);

        Inventory oranges = new Inventory(
                "Oranges",
                "2.00",
                14,
                "California Grounds",
                "+1 800 903 2090",
                "oranges@oranges.com",
                "android.resource://com.example.android.inventoryapp/drawable/oranges");
        dbHelper.insertItem(oranges);

        Inventory tomatoes = new Inventory(
                "Tomatoes",
                "2.29",
                90,
                "Farmers Marketers",
                "+1 800 876 9343",
                "tomatoes@tomatoes.com",
                "android.resource://com.example.android.inventoryapp/drawable/tomatoes");
        dbHelper.insertItem(tomatoes);
    }
}