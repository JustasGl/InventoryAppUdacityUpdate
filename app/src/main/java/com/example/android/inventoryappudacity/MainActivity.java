package com.example.android.inventoryappudacity;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.example.android.inventoryappudacity.DataBase.DatabaseHelper;
import com.example.android.inventoryappudacity.DataBase.InventorContract;
import com.example.android.inventoryappudacity.DataBase.InventorContract.Inventor;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,LoaderManager.LoaderCallbacks<Cursor> {
    ListView listView;
    String Order = null;
    View emptyView;
    private static final int LOADER_ID = 0;
    CursorAdapter mCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLoaderManager().initLoader(LOADER_ID, null, this);

        emptyView = findViewById(R.id.emptyview);
        mCursorAdapter = new CursorAdaptor(this,null);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_nav);
        ArrayAdapter<CharSequence> adapt = ArrayAdapter.createFromResource(this,
                R.array.OrderArray, android.R.layout.simple_spinner_item);
        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapt);
        spinner.setOnItemSelectedListener(this);

        listView = (ListView) findViewById(R.id.listview);

        listView.setAdapter(mCursorAdapter);
        listView.setEmptyView(emptyView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public static void listItemClick(Context context, long id)
    {
        Intent addIntent = new Intent(context, Add.class);
        Uri currentItem = ContentUris.withAppendedId(InventorContract.Inventor.CONTENT_URI,id);
        addIntent.setData(currentItem);
        context.startActivity(addIntent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add) {
            Intent AddScreen = new Intent(this, Add.class);
            startActivity(AddScreen);
        } else if (id == R.id.clear) {
            showDeleteConfirmationDialog(-10);
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean delete(int pos) {
        if(pos>=0) {
            Uri uri =ContentUris.withAppendedId(InventorContract.Inventor.CONTENT_URI,pos);
            int rowsDeleted = getContentResolver().delete(uri, null, null);
          Log.v("CatalogActivity", rowsDeleted + getString(R.string.rownsDeleted));
          return true;
    }
    else if(pos<0)
        {
            int rowsDeleted = getContentResolver().delete(Inventor.CONTENT_URI, null, null);
            Log.v("CatalogActivity", rowsDeleted + getString(R.string.rownsDeleted));
            return true;
        }
        else return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i)
        {
            case 0:
                Order=Inventor.ID;
                break;
            case 1:
                Order=Inventor.Price;
                break;
            case 2:
                Order=Inventor.InStock;
                break;
        }
        getLoaderManager().restartLoader(LOADER_ID,null,this);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    Toast.makeText(getApplicationContext(), R.string.nothingSelected,Toast.LENGTH_SHORT).show();
    }
    private void showDeleteConfirmationDialog(final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (delete(pos)) {
                } else
                    Toast.makeText(getApplicationContext(), R.string.failledTOdelete, Toast.LENGTH_SHORT).show();
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String [] projection = {Inventor.ID, Inventor.Title,Inventor.Description,Inventor.InStock,Inventor.Price,Inventor.SupplierName,Inventor.SupplierPhone};
        return new CursorLoader(this,InventorContract.Inventor.CONTENT_URI,projection,null,null,Order);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
