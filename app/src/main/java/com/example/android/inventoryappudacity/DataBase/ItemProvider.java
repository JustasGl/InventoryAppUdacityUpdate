    package com.example.android.inventoryappudacity.DataBase;

    import android.content.ContentProvider;
    import android.content.ContentUris;
    import android.content.ContentValues;
    import android.content.UriMatcher;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.net.Uri;
    import android.support.annotation.NonNull;
    import android.support.annotation.Nullable;
    import android.util.Log;
    import android.widget.Toast;

    import com.example.android.inventoryappudacity.R;


    public class ItemProvider extends ContentProvider {

    private static final int Items = 100;

    private static final int Item_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventorContract.CONTENT_AUTHORITY, InventorContract.PATH_ITEMS, Items);
        sUriMatcher.addURI(InventorContract.CONTENT_AUTHORITY, InventorContract.PATH_ITEMS + "/#", Item_ID);
    }

    private DatabaseHelper mDataBase;

    @Override
    public boolean onCreate() {
        mDataBase = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDataBase.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case  Items:
                cursor = database.query(InventorContract.Inventor.TableName,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case Item_ID:
                selection = InventorContract.Inventor.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventorContract.Inventor.TableName, projection,selection,selectionArgs,null,null,sortOrder);
                break;
                default:
                    throw new IllegalArgumentException(getContext().getString(R.string.cannot_query_unknown_uri) + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType( Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case Items:
                return InventorContract.Inventor.CONTENT_LIST_TYPE;
            case Item_ID:
                return InventorContract.Inventor.CONTENT_ITEM_TYPE;
                default:
                    throw new IllegalStateException(getContext().getString(R.string.unknown_uri) + uri + getContext().getString(R.string.with_match) + match);
        }
    }

    @Override
    public Uri insert( Uri uri, ContentValues contentValues) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case Items:
                return insertItem(uri,contentValues);
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.insertion_not_supported) + uri);
        }
    }
    private Uri insertItem(Uri uri, ContentValues contentValues){
        String title = contentValues.getAsString(InventorContract.Inventor.Title);
        if(title.isEmpty())
            throw new IllegalArgumentException(getContext().getString(R.string.Title_required));
        int price = contentValues.getAsInteger(InventorContract.Inventor.Price);
        if(price<0)
        throw new IllegalArgumentException(getContext().getString(R.string.Price_positive));
        int amount = contentValues.getAsInteger(InventorContract.Inventor.InStock);
        if(amount<0)
            throw new IllegalArgumentException(getContext().getString(R.string.Amount_positive));
        SQLiteDatabase database = mDataBase.getWritableDatabase();
        long id = database.insert(InventorContract.Inventor.TableName,null, contentValues);
        if(id==-1) {
            Log.e("", getContext().getString(R.string.failed_to_insert_row) + uri);
        return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }
    @Override
    public int delete( Uri uri, String s, String[] strings) {
        int match = sUriMatcher.match(uri);
        int rowsdeleted;
        SQLiteDatabase database = mDataBase.getWritableDatabase();
        switch (match){
            case Items:
                rowsdeleted = database.delete(InventorContract.Inventor.TableName,s,strings);
                break;
            case Item_ID:
                s = InventorContract.Inventor._ID + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsdeleted = database.delete(InventorContract.Inventor.TableName,s,strings);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.deletion_not_supported) + uri);
        }
        if(rowsdeleted!=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsdeleted;
    }

    @Override
    public int update( Uri uri, ContentValues contentValues, String s, String[] strings) {
        int match = sUriMatcher.match(uri);
        switch (match)
        {
            case Items:
                return  updater(uri,contentValues,s,strings);
            case Item_ID:
                s = InventorContract.Inventor._ID + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updater(uri,contentValues,s,strings);
        }
        return 0;
    }
    private int updater(Uri uri, ContentValues values, String s, String [] strings)
    {
        SQLiteDatabase database = mDataBase.getWritableDatabase();

        if(values.containsKey(InventorContract.Inventor.InStock)) {
            int amount = values.getAsInteger(InventorContract.Inventor.InStock);
            if (amount < 0)
                throw new IllegalArgumentException(getContext().getString(R.string.Amount_cant_be_Less));
        }

        if(values.containsKey(InventorContract.Inventor.Price)) {
            int value = values.getAsInteger(InventorContract.Inventor.Price);
            if (value < 0)
                throw new IllegalArgumentException(getContext().getString(R.string.value_cannot_be_negative));
        }

        if(values.containsKey(InventorContract.Inventor.Title)) {
            String title = values.getAsString(InventorContract.Inventor.Title);
            if (title.isEmpty())
                throw new IllegalArgumentException(getContext().getString(R.string.Title_cannot_be_empty));
        }

        if(values.containsKey(InventorContract.Inventor.Description))
        {
            String description = values.getAsString(InventorContract.Inventor.Description);
            if(description.isEmpty())
                throw new IllegalArgumentException(getContext().getString(R.string.desnotempty));
        }
        if(values.containsKey(InventorContract.Inventor.SupplierPhone))
        {
            String supplierPhone = values.getAsString(InventorContract.Inventor.SupplierPhone);
            if(supplierPhone.isEmpty())
                throw new IllegalArgumentException(getContext().getString(R.string.supnotempty));
        }

        if(values.containsKey(InventorContract.Inventor.SupplierName))
        {
            String supplierName = values.getAsString(InventorContract.Inventor.SupplierName);
            if(supplierName.isEmpty())
                throw new IllegalArgumentException(getContext().getString(R.string.supnamenotempty));
        }
              int updated =  database.update(InventorContract.Inventor.TableName,values,s,strings);
        if(updated!=0)
        {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return updated;
    }
    }
