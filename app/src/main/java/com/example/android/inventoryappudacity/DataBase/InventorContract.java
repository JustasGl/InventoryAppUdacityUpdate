package com.example.android.inventoryappudacity.DataBase;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Justas on 4/9/2018.
 */

public final class InventorContract {

    private InventorContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.items";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + "com.example.android.items");

    public static final String PATH_ITEMS = "items";



    public static final class Inventor implements BaseColumns {

        public final static String TableName = "INVENTORTABLE";
        public final static String ID = BaseColumns._ID;
        public final static String Description = "DESCRIPTION";
        public final static String Title = "TITLE";
        public final static String InStock = "INSTOCK";
        public final static String SupplierName = "SUPPLIERNAME";
        public final static String SupplierPhone = "SUPPLIERPHONE";
        public final static String Price = "PRICE";

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "items";

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "items";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

    }
}
