package com.karthyks.geoguide;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by karthik on 13/10/15.
 */
public class LocationProvider extends ContentProvider {
    static final String PROVIDER_NAME = Constants.PACKAGE_NAME + "." + Constants.DATABASE_NAME;
    public SQLiteDatabase mDB;
    // scheme content
    // authority - PROVIDER_NAME
    // appendPath - table name

    static final Uri mURL = new Uri.Builder()
            .scheme("content")
            .authority(PROVIDER_NAME)
            .appendPath(Constants.LOCATIONS_TABLE_NAME).build();

    static UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, Constants.LOCATIONS_TABLE_NAME, 1);
        uriMatcher.addURI(PROVIDER_NAME, Constants.LOCATIONS_TABLE_NAME + "/#", 2);
    }

    private HashMap<String, String> Locations_Projection_Map;
    @Override
    public boolean onCreate() {
        Context context = getContext();
        DBHelper dbHelper = new DBHelper(context);
        mDB = dbHelper.getWritableDatabase();
        return (mDB == null) ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(Constants.LOCATIONS_TABLE_NAME);

        switch(uriMatcher.match(uri)){
            case 1:
                sqLiteQueryBuilder.setProjectionMap(Locations_Projection_Map);
                break;
            case 2:
                sqLiteQueryBuilder.appendWhere("id = " + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (sortOrder == null || sortOrder == ""){
            /**
             * By default sort on id
             */
            sortOrder = "id";
        }
        Cursor c = sqLiteQueryBuilder.query(mDB, projection, selection, selectionArgs,null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            /**
             * Get all student records
             */
            case 1:
                return "vnd.android.cursor.dir/vnd.example.students";

            /**
             * Get a particular student
             */
            case 2:
                return "vnd.android.cursor.item/vnd.example.students";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long insertID = mDB.insert(Constants.LOCATIONS_TABLE_NAME, null, values);
        if(insertID > 0){
            Uri _uri = ContentUris.withAppendedId(mURL, insertID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;

        switch (uriMatcher.match(uri)){
            case 1:
                count = mDB.delete(Constants.LOCATIONS_TABLE_NAME, selection, selectionArgs);
                break;

            case 2:
                String id = uri.getPathSegments().get(1);
                count = mDB.delete( Constants.LOCATIONS_TABLE_NAME, "id = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;

        switch (uriMatcher.match(uri)){
            case 1:
                count = mDB.update(Constants.LOCATIONS_TABLE_NAME, values, selection, selectionArgs);
                break;

            case 2:
                count = mDB.update(Constants.LOCATIONS_TABLE_NAME, values, "id = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
