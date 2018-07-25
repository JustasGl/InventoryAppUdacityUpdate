package com.example.android.inventoryappudacity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryappudacity.DataBase.InventorContract;

/**
 * Created by Justas on 7/21/2018.
 */

public class CursorAdaptor extends CursorAdapter {

    public CursorAdaptor(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listitem, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final TextView Title,
                Phone,
                Name,
                Price,
                Quantity,
                Description;
        final CheckBox checkBox;

        Button plus,
                minus;
        LinearLayout mainLayout;

        Title = view.findViewById(R.id.Title);
        Phone = view.findViewById(R.id.SupplierPhone);
        Name = view.findViewById(R.id.SupplierName);
        Price = view.findViewById(R.id.Price);
        Quantity = view.findViewById(R.id.Quantity);
        checkBox = view.findViewById(R.id.checkbox);
        Description = view.findViewById(R.id.description);
              plus = view.findViewById(R.id.plus);
              minus = view.findViewById(R.id.minus);
        mainLayout = view.findViewById(R.id.mainitem);

            int idpos = cursor.getColumnIndex(InventorContract.Inventor._ID);
            final int titlepos = cursor.getColumnIndex(InventorContract.Inventor.Title);
            final int phonepos = cursor.getColumnIndex(InventorContract.Inventor.SupplierPhone);
            final int namepos = cursor.getColumnIndex(InventorContract.Inventor.SupplierName);
            final int pricepos = cursor.getColumnIndex(InventorContract.Inventor.Price);
        int quantitypos = cursor.getColumnIndex(InventorContract.Inventor.InStock);
        final int descriptionpos = cursor.getColumnIndex(InventorContract.Inventor.Description);
            final long id = cursor.getInt(idpos);

        if(cursor.getString(titlepos).isEmpty())
            Title.setText(R.string.Unknown_title);
        else Title.setText(cursor.getString(titlepos));

        if(cursor.getString(phonepos).isEmpty())
            Phone.setText(R.string.Unknown_phone);
        else Phone.setText(cursor.getString(phonepos));

        if(cursor.getString(namepos).isEmpty())
            Name.setText(R.string.Unknown_name);
        else Name.setText(cursor.getString(namepos));

        if(String.valueOf(cursor.getInt(phonepos)).isEmpty())
            Price.setText(R.string.Unknown_price);
        else Price.setText(context.getString(R.string.price)+" "+cursor.getInt(pricepos)+" "+context.getString(R.string.euro));
        final int quantity = cursor.getInt(quantitypos);

        if(String.valueOf(quantity).isEmpty())
            Quantity.setText(R.string.Unknown_quantity);
        else Quantity.setText(context.getString(R.string.Quantity)+" "+quantity);

        if (quantity<0)
            checkBox.setChecked(false);
        else checkBox.setChecked(true);

        if(cursor.getString(descriptionpos).isEmpty())
            Description.setText(R.string.no_des);
        else Description.setText(cursor.getString(descriptionpos));
        if (quantity==0)
            checkBox.setChecked(false);
        else checkBox.setChecked(true);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                int tempQuantity = quantity;
                tempQuantity++;
                values.put(InventorContract.Inventor.InStock,tempQuantity);
                Uri uri;
                uri = ContentUris.withAppendedId(InventorContract.Inventor.CONTENT_URI,id);
                context.getContentResolver().update(uri,values,null,null);
            }
        });
    minus.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int tempQuantity = quantity;
            if(tempQuantity==0)
                return;

            ContentValues values = new ContentValues();
            tempQuantity--;
            values.put(InventorContract.Inventor.InStock,tempQuantity);
            Uri uri;
            uri = ContentUris.withAppendedId(InventorContract.Inventor.CONTENT_URI,id);
            context.getContentResolver().update(uri,values,null,null);
        }
    });
    mainLayout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.listItemClick(context,id);
        }
    });
    }
    }

