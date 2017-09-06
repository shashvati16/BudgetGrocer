package capstone.android.project.com.capstoneproject.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import capstone.android.project.com.capstoneproject.R;
import capstone.android.project.com.capstoneproject.data.DealsContract;

import static capstone.android.project.com.capstoneproject.data.DealsContract.BASE_CONTENT_URI;
import static capstone.android.project.com.capstoneproject.data.DealsContract.PATH_DEAL;

/**
 * Created by Shashvati on 9/4/2017.
 */

public class GroceryListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    Cursor mCursor;
    public GroceryListRemoteViewsFactory(Context applicationContext,Intent intent) {
        this.mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Uri SHOPPING_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_DEAL).build();
        if(mCursor!=null) mCursor.close();
        final long identityToken = Binder.clearCallingIdentity();

        mCursor = mContext.getContentResolver().query(
                SHOPPING_URI,
                null,
                null,
                null,
                DealsContract.DealEntry.COLUMN_DEAL_DATE
        );


        Binder.restoreCallingIdentity(identityToken);

    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        if (mCursor==null) return 0;
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                mCursor == null || !mCursor.moveToPosition(position)) {
            return null;
        }
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_shopping_list_item);
        int itemIndex = mCursor.getColumnIndex(DealsContract.DealEntry.COLUMN_DEAL_ITEM);
        int dealIndex = mCursor.getColumnIndex(DealsContract.DealEntry.COLUMN_DEAL_NAME);
        String item = mCursor.getString(itemIndex);
        String deal = mCursor.getString(dealIndex);
        if (deal==null){
            rv.setTextViewText(R.id.itemText,item);
        }
        else {
            rv.setTextViewText(R.id.itemText, deal);
        }

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return mCursor.moveToPosition(position) ? mCursor.getLong(0) : position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

