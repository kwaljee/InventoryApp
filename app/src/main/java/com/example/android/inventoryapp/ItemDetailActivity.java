package com.example.android.inventoryapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.DBContract;
import com.example.android.inventoryapp.data.DBHelper;


public class ItemDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = ItemDetailActivity.class.getCanonicalName();
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PICK_IMAGE_REQUEST = 0;
    private DBHelper dbHelper;
    private TextView nameView;
    private EditText nameEdit;
    private TextView priceView;
    private EditText priceEdit;
    private EditText quantityEdit;
    private TextView supplierNameView;
    private EditText supplierNameEdit;
    private TextView supplierPhoneView;
    private EditText supplierPhoneEdit;
    private TextView supplierEmailView;
    private EditText supplierEmailEdit;
    private long currentItemId;
    private ImageButton decreaseQuantity;
    private ImageButton increaseQuantity;
    private Button selectImg;
    private ImageButton deleteBtn;
    private ImageButton saveBtn;
    private ImageButton orderBtn;
    private ImageView imageView;
    private Uri actualUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DBHelper(this);
        currentItemId = getIntent().getLongExtra("itemId", 0);

        if (currentItemId == 0) {
            setTitle(getString(R.string.new_item));
            setContentView(R.layout.activity_add_items);
            imageView = (ImageView) findViewById(R.id.image_view);

            selectImg = (Button) findViewById(R.id.select_image);
            selectImg.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    tryToOpenImageSelector();
                }
            });

            saveBtn = (ImageButton) findViewById(R.id.save_item);
            saveBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    addItem();
                }
            });

        } else {
            setTitle(getString(R.string.edit_item));
            setContentView(R.layout.activity_edit_items);
            displayItems(currentItemId);

            decreaseQuantity = (ImageButton) findViewById(R.id.decrease_quantity);
            decreaseQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeFromQty();
                }
            });

            increaseQuantity = (ImageButton) findViewById(R.id.increase_quantity);
            increaseQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToQty();
                }
            });

            deleteBtn = (ImageButton) findViewById(R.id.delete_item);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialogConfirmDelete();
                }
            });

            saveBtn = (ImageButton) findViewById(R.id.save_item);
            saveBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    updateQty();
                }
            });

            orderBtn = (ImageButton) findViewById(R.id.order_item);
            orderBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showOrderConfirmationDialog();
                }
            });
        }
    }


    private void showOrderConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.order_confirm_msg);
        builder.setPositiveButton(R.string.phone_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(getString(R.string.tel_confirm) + supplierPhoneView.getText().toString().trim()));
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.email_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
                intent.setType(getString(R.string.email_type));
                intent.setData(Uri.parse(getString(R.string.mailto_confim) + supplierEmailView.getText().toString().trim()));
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
                String bodyMessage = getString(R.string.email_body_text) +
                        nameView.getText().toString().trim() +
                        getString(R.string.exclamation);
                intent.putExtra(android.content.Intent.EXTRA_TEXT, bodyMessage);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void removeFromQty() {
        String previousValueString = quantityEdit.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            return;
        } else if (previousValueString.equals("0")) {
            return;
        } else {
            previousValue = Integer.parseInt(previousValueString);
            quantityEdit.setText(String.valueOf(previousValue - 1));
        }
    }

    private void addToQty() {
        String previousValueString = quantityEdit.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        quantityEdit.setText(String.valueOf(previousValue + 1));
    }

    private int deleteItem(final long itemId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = DBContract.ItemEntry._ID + "=?";
        String[] selectionArgs = {String.valueOf(itemId)};

        int rowsDeleted = database.delete(DBContract.ItemEntry.TABLE_NAME, selection, selectionArgs);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();

        return rowsDeleted;
    }

    private void dialogConfirmDelete() {

        new AlertDialog.Builder(this)
                .setTitle(R.string.warning_dialog_msg)
                .setMessage(R.string.confirm_delete_msg)
                .setPositiveButton(R.string.positive_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem(currentItemId);
                    }
                })
                .setNegativeButton(R.string.negative_dialog, null)
                .show();
    }

    private boolean updateQty() {
        int quantity = Integer.parseInt(quantityEdit.getText().toString().trim());
        Toast.makeText(this, R.string.quantity_updated_msg, Toast.LENGTH_SHORT).show();
        dbHelper.updateItem(currentItemId, quantity);
        return true;
    }

    private void addItem() {

        String name;
        String price;
        String quantity;
        String sName;
        String sPhone;
        String sEmail;
        String imageString;

        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        nameEdit = (EditText) findViewById(R.id.product_name_edit);
        priceEdit = (EditText) findViewById(R.id.price_edit);
        quantityEdit = (EditText) findViewById(R.id.quantity_edit);
        supplierNameEdit = (EditText) findViewById(R.id.supplier_name_edit);
        supplierPhoneEdit = (EditText) findViewById(R.id.supplier_phone_edit);
        supplierEmailEdit = (EditText) findViewById(R.id.supplier_email_edit);
        imageView = (ImageView) findViewById(R.id.image_view);

        name = nameEdit.getText().toString().trim();
        price = (priceEdit.getText().toString().trim());
        quantity = (quantityEdit.getText().toString().trim());
        sName = supplierNameEdit.getText().toString().trim();
        sPhone = supplierPhoneEdit.getText().toString().trim();
        sEmail = supplierEmailEdit.getText().toString().trim();

        if (actualUri == null) {
            Uri uri = Uri.parse(getString(R.string.uri_parse));
            imageString = uri.toString().trim();
        } else {
            imageString = actualUri.toString().trim();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.ItemEntry.COLUMN_NAME, name);
        contentValues.put(DBContract.ItemEntry.COLUMN_PRICE, price);
        contentValues.put(DBContract.ItemEntry.COLUMN_QUANTITY, quantity);
        contentValues.put(DBContract.ItemEntry.COLUMN_SUPPLIER_NAME, sName);
        contentValues.put(DBContract.ItemEntry.COLUMN_SUPPLIER_PHONE, sPhone);
        contentValues.put(DBContract.ItemEntry.COLUMN_SUPPLIER_EMAIL, sEmail);
        contentValues.put(DBContract.ItemEntry.COLUMN_IMAGE, imageString);

        if (nameEdit.getText().toString().length() == 0) {
            nameEdit.setError(getString(R.string.product_name_error_msg));
            return;
        }
        if (priceEdit.getText().toString().length() == 0) {
            priceEdit.setError(getString(R.string.price_error_msg));
            return;
        }
        if (quantityEdit.getText().toString().length() == 0) {
            quantityEdit.setError(getString(R.string.quantity_error_msg));
            return;
        }
        if (supplierNameEdit.getText().toString().length() == 0) {
            supplierNameEdit.setError(getString(R.string.supplier_name_error_msg));
            return;
        }
        if (supplierPhoneEdit.getText().toString().length() == 0) {
            supplierPhoneEdit.setError(getString(R.string.supplier_phone_error_msg));
            return;
        }
        if (supplierEmailEdit.getText().toString().length() == 0) {
            supplierEmailEdit.setError(getString(R.string.supplier_email_error_msg));
            return;
        }

        long newRowId = db.insert(DBContract.ItemEntry.TABLE_NAME, null, contentValues);

        if (newRowId == -1) {
            Toast.makeText(this, R.string.error_saving_item, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.item_saved), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void displayItems(long itemId) {
        Cursor cursor = dbHelper.readItem(itemId);
        cursor.moveToFirst();

        nameView = (TextView) findViewById(R.id.product_name_stored);
        priceView = (TextView) findViewById(R.id.price_stored);
        quantityEdit = (EditText) findViewById(R.id.quantity_stored);
        supplierNameView = (TextView) findViewById(R.id.supplier_name_stored);
        supplierPhoneView = (TextView) findViewById(R.id.supplier_phone_stored);
        supplierEmailView = (TextView) findViewById(R.id.supplier_email_stored);
        imageView = (ImageView) findViewById(R.id.image_view);

        nameView.setText(cursor.getString(cursor.getColumnIndex(DBContract.ItemEntry.COLUMN_NAME)));
        priceView.setText(cursor.getString(cursor.getColumnIndex(DBContract.ItemEntry.COLUMN_PRICE)));
        quantityEdit.setText(cursor.getString(cursor.getColumnIndex(DBContract.ItemEntry.COLUMN_QUANTITY)));
        supplierNameView.setText(cursor.getString(cursor.getColumnIndex(DBContract.ItemEntry.COLUMN_SUPPLIER_NAME)));
        supplierPhoneView.setText(cursor.getString(cursor.getColumnIndex(DBContract.ItemEntry.COLUMN_SUPPLIER_PHONE)));
        supplierEmailView.setText(cursor.getString(cursor.getColumnIndex(DBContract.ItemEntry.COLUMN_SUPPLIER_EMAIL)));
        imageView.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(DBContract.ItemEntry.COLUMN_IMAGE))));
    }

    public void tryToOpenImageSelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        openImageSelector();
    }

    private void openImageSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageSelector();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                actualUri = resultData.getData();
                imageView.setImageURI(actualUri);
                imageView.invalidate();
            }
        }
    }
}