package capstone.android.project.com.capstoneproject.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;

import static android.provider.BaseColumns._ID;
import static capstone.android.project.com.capstoneproject.data.DealsContract.DealEntry.TABLE_NAME;

/**
 * Created by Shashvati on 8/22/2017.
 */

public class DealProvider extends ContentProvider {
    DealsDBHelper dealDBHelper;
    private static final int CODE_DEALS = 100;
    private static final int CODE_DEAL_ID = 101;
    private static final int CODE_DEAL_DATE = 102;
    private static UriMatcher mUriMatcher=buildUriMatcher();
    @Override
    public boolean onCreate() {
        dealDBHelper=new DealsDBHelper(getContext());
        return true;
    }
    public static UriMatcher buildUriMatcher(){
        mUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(DealsContract.CONTENT_AUTHORITY,DealsContract.PATH_DEAL,CODE_DEALS);
        mUriMatcher.addURI(DealsContract.CONTENT_AUTHORITY,DealsContract.PATH_DEAL +  "/*", CODE_DEAL_ID);
        mUriMatcher.addURI(DealsContract.CONTENT_AUTHORITY,DealsContract.PATH_DEAL +  "/*", CODE_DEAL_DATE);
        return mUriMatcher;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db= dealDBHelper.getReadableDatabase();
        int match=mUriMatcher.match(uri);
        Cursor queryDeals;
        switch (match){
            case CODE_DEALS:
                queryDeals=db.query(TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case CODE_DEAL_ID:
                String dealItem=uri.getLastPathSegment();
                String mSelection=DealsContract.DealEntry.COLUMN_DEAL_ITEM + "=?";
                String[] mSelectionArgs=new String[]{dealItem};
                queryDeals=db.query(TABLE_NAME,projection,mSelection,mSelectionArgs,null,null,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        queryDeals.setNotificationUri(getContext().getContentResolver(),uri);
        return queryDeals;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("Unknown Operation.");
    }


    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = dealDBHelper.getWritableDatabase();
        Uri returnUri;
        switch (mUriMatcher.match(uri)) {
            case CODE_DEALS:
                long id = db.insert(TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(DealsContract.DealEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dealDBHelper.getWritableDatabase();
        int dealDeleted;
        switch (mUriMatcher.match(uri)) {
            case CODE_DEAL_ID:
                try {
                    db.beginTransaction();
                    String id = uri.getPathSegments().get(1);
                    String mSelection = DealsContract.DealEntry._ID + "=?" ;
                    String[] mSelectionArgs = new String[]{String.valueOf(id)};
                    dealDeleted = db.delete(TABLE_NAME, mSelection, mSelectionArgs);
                    db.setTransactionSuccessful();
                } finally {

                    db.endTransaction();
                }
                if (dealDeleted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return dealDeleted;
            case CODE_DEALS:
                try {
                    db.beginTransaction();
                    dealDeleted = db.delete(TABLE_NAME,null,null);
                    db.setTransactionSuccessful();
                } finally {

                    db.endTransaction();
                }
                if (dealDeleted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return dealDeleted;
            case CODE_DEAL_DATE:
                try {
                    db.beginTransaction();
                    String date = uri.getPathSegments().get(1);
                    String mSelection = DealsContract.DealEntry.COLUMN_DEAL_DATE + "<?" ;
                    String[] mSelectionArgs = new String[]{date};
                    dealDeleted = db.delete(TABLE_NAME, mSelection, mSelectionArgs);
                    db.setTransactionSuccessful();
                } finally {

                    db.endTransaction();
                }
                if (dealDeleted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return dealDeleted;

            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }




    }
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Unknown uri:" + uri);

    }


}
