package com.example.android.inventoryappudacity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryappudacity.DataBase.DatabaseHelper;
import com.example.android.inventoryappudacity.DataBase.InventorContract;
import com.example.android.inventoryappudacity.DataBase.InventorContract.Inventor;

import java.util.function.Supplier;

public class Add extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_LOADER = 0;

    private boolean itemChanged=false;

    EditText titleEdit,
            descriptionEdit,
            phoneEdit,
            supplierEdit,
            quantityEdit,
            priceEdit;
    String title,
            description,
            phone,
            supplier;
    int price,
            mQuantity;
    Uri mCurrentItemUri;

    Button minus,
    plus,
    call;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            itemChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        titleEdit = findViewById(R.id.itemNameEditText);
        descriptionEdit = findViewById(R.id.itemDesciptionEditText);
        phoneEdit = findViewById(R.id.SupplierPhone);
        supplierEdit = findViewById(R.id.SupplierName);
        quantityEdit = findViewById(R.id.QuantityEditText);
        priceEdit = findViewById(R.id.PriceEditText);
        minus = findViewById(R.id.minus);
        plus = findViewById(R.id.plus);
        call = findViewById(R.id.phone);

        plus.setOnTouchListener(mTouchListener);
        minus.setOnTouchListener(mTouchListener);


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phoneEdit.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.Enter_Supplier_phone_first, Toast.LENGTH_SHORT).show();
                return;
                }
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneEdit.getText().toString(), null));
                startActivity(intent);
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!quantityEdit.getText().toString().isEmpty())
                    mQuantity= Integer.parseInt(quantityEdit.getText().toString());
                mQuantity++;
                quantityEdit.setText(mQuantity+"");
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!quantityEdit.getText().toString().isEmpty())
                    mQuantity= Integer.parseInt(quantityEdit.getText().toString());
                if(mQuantity==0)
                    return;
                mQuantity--;
                quantityEdit.setText(mQuantity+"");
            }
        });


        mCurrentItemUri = getIntent().getData();

        FloatingActionButton fab = findViewById(R.id.fab);

        if (mCurrentItemUri == null) {
            setTitle("New Item");
            setTouchListeners();
            fab.setVisibility(View.GONE);
            invalidateOptionsMenu();
        } else {
            setTitle("Detailed view");
            disableEditText();
            getLoaderManager().initLoader(EXISTING_LOADER, null, this);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTitle("Edit Item");
                setTouchListeners();
                enableEdittext();
            }
        });
    }
    private void setTouchListeners()
    {
        titleEdit.setOnTouchListener(mTouchListener);
        descriptionEdit.setOnTouchListener(mTouchListener);
        phoneEdit.setOnTouchListener(mTouchListener);
        supplierEdit.setOnTouchListener(mTouchListener);
        quantityEdit.setOnTouchListener(mTouchListener);
        priceEdit.setOnTouchListener(mTouchListener);
    }
    private void disableEditText()
    {
        titleEdit.setFocusableInTouchMode(false);
        descriptionEdit.setFocusableInTouchMode(false);
        phoneEdit.setFocusableInTouchMode(false);
        supplierEdit.setFocusableInTouchMode(false);
        quantityEdit.setFocusableInTouchMode(false);
        priceEdit.setFocusableInTouchMode(false);
    }
    private void enableEdittext()
    {
        titleEdit.setFocusableInTouchMode(true);
        descriptionEdit.setFocusableInTouchMode(true);
        phoneEdit.setFocusableInTouchMode(true);
        supplierEdit.setFocusableInTouchMode(true);
        quantityEdit.setFocusableInTouchMode(true);
        priceEdit.setFocusableInTouchMode(true);
    }
    private void getInfo() {
        title = titleEdit.getText().toString().trim();
        description = descriptionEdit.getText().toString().trim();
        phone = phoneEdit.getText().toString().trim();
        supplier = supplierEdit.getText().toString().trim();

        if(!quantityEdit.getText().toString().trim().isEmpty())
        mQuantity = Integer.parseInt(quantityEdit.getText().toString().trim());

        if(!priceEdit.getText().toString().trim().isEmpty())
        price = Integer.parseInt(priceEdit.getText().toString().trim());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addAdd) {
            getInfo();
            if (title.isEmpty() || description.isEmpty() || price<0||phone.isEmpty()|| supplier.isEmpty()||mQuantity <0) {
                Checkinfo();
                return false;
            }
            else {
                saveData();
                finish();
            }
        } else if (id == R.id.clearAdd) {
            titleEdit.setText("");
            descriptionEdit.setText("");
            phoneEdit.setText("");
            supplierEdit.setText("");
            quantityEdit.setText("");
            priceEdit.setText("");
        }
        else if(id == R.id.del)
            showDeleteConfirmationDialog();
        return super.onOptionsItemSelected(item);
    }

    private void saveData() {

        if(mCurrentItemUri==null&&title.isEmpty()&&description.isEmpty()&& supplier.isEmpty()&& phone.isEmpty())
            return;

        ContentValues values = new ContentValues();
        values.put(Inventor.Description, description);
        values.put(Inventor.Title, title);
        values.put(Inventor.InStock, mQuantity);
        values.put(Inventor.SupplierPhone, phone);
        values.put(Inventor.SupplierName, supplier);
        values.put(Inventor.Price, price);

        if(mCurrentItemUri==null)
        {
            Uri newUri = getContentResolver().insert(InventorContract.Inventor.CONTENT_URI, values);
        if(newUri==null)
            Toast.makeText(this, R.string.failed_to_upload,Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, R.string.success_inserting_item,Toast.LENGTH_SHORT).show();
        }
        else
        {
            int updated = getContentResolver().update(mCurrentItemUri,values,null,null);
            if(updated==0)
                Toast.makeText(this, R.string.failed_updating_item,Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, R.string.success_updating,Toast.LENGTH_SHORT).show();

        }
    }
    @Override
    public void onBackPressed() {
        if (!itemChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteitem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteitem()
    {
        if(mCurrentItemUri!=null)
        {
            int deleted = getContentResolver().delete(mCurrentItemUri,null,null);

            if(deleted==0)
            {
                Toast.makeText(this, R.string.error_deleting_tem,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.item_deleted_succesfully,
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
    private void Checkinfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.checkinfo);
        builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void showUnsavedChangesDialog (DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String projection [] = { Inventor.ID,
                Inventor.Title,
                Inventor.Description,
                Inventor.InStock,
                Inventor.Price,
                Inventor.SupplierName,
                Inventor.SupplierPhone};
        return new CursorLoader(this,
                mCurrentItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor==null||cursor.getCount()<1)
            return;
        if(cursor.moveToFirst())
        {
            int nameIndex = cursor.getColumnIndex(Inventor.Title);
            int descriptionIndex = cursor.getColumnIndex(Inventor.Description);
            int inStockIndex = cursor.getColumnIndex(Inventor.InStock);
            int priceIndex = cursor.getColumnIndex(Inventor.Price);
            int SupplierNameIndex = cursor.getColumnIndex(Inventor.SupplierName);
            int SupplierPhoneIndex = cursor.getColumnIndex(Inventor.SupplierPhone);

            String name = cursor.getString(nameIndex);
            String description = cursor.getString(descriptionIndex);
            int quantity = cursor.getInt(inStockIndex);
            int price = cursor.getInt(priceIndex);
            String suplliername = cursor.getString(SupplierNameIndex);
            String suplierphone = cursor.getString(SupplierPhoneIndex);

            mQuantity = quantity;

            titleEdit.setText(name);
            supplierEdit.setText(suplliername);
            quantityEdit.setText(quantity+"");
            descriptionEdit.setText(description);
            priceEdit.setText(price+"");
            phoneEdit.setText(suplierphone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
