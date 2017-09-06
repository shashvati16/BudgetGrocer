package capstone.android.project.com.capstoneproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import capstone.android.project.com.capstoneproject.data.DealsContract;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Shashvati on 8/10/2017.
 */

public class GroceryListAdapter extends RecyclerView.Adapter<GroceryListAdapter.GroceryViewHolder> {
    private Cursor mCursor;
    private Context mContext;

    public GroceryListAdapter(Context mContext) {
        this.mContext = mContext;
    }
    @Override
    public GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.grocery_item, parent, false);

        return new GroceryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroceryViewHolder holder, int position) {


        int itemIndex = mCursor.getColumnIndex(DealsContract.DealEntry.COLUMN_DEAL_ITEM);
        int qtyIndex = mCursor.getColumnIndex(DealsContract.DealEntry.COLUMN_DEAL_QTY);
        int dealIndex = mCursor.getColumnIndex(DealsContract.DealEntry.COLUMN_DEAL_NAME);
        int storeIndex = mCursor.getColumnIndex(DealsContract.DealEntry.COLUMN_DEAL_STORE);
        int priceIndex = mCursor.getColumnIndex(DealsContract.DealEntry.COLUMN_DEAL_PRICE);
        int expDateIndex = mCursor.getColumnIndex(DealsContract.DealEntry.COLUMN_DEAL_DATE);
        int idIndex = mCursor.getColumnIndex(DealsContract.DealEntry._ID);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Determine the values of the wanted data
        String item = mCursor.getString(itemIndex);
        String qty = mCursor.getString(qtyIndex);
        String deal = mCursor.getString(dealIndex);
        String store = mCursor.getString(storeIndex);
        String price = mCursor.getString(priceIndex);
        String expDate = mCursor.getString(expDateIndex);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String expiry = sdf.format(new Date(Long.parseLong(expDate)));
        long id = mCursor.getLong(idIndex);
        SharedPreferences mySavedDeals = mContext.getSharedPreferences("MY SAVED DEALS",MODE_PRIVATE);
        SharedPreferences.Editor editor = mySavedDeals.edit();
        editor.putString(deal,store);
        editor.commit();

        if(deal!=null) {
            holder.item.setText(deal);
            holder.deal.setText(price + " from " + store + " expires " + expiry);
        }
        else {
            holder.item.setText(item);
            holder.deal.setVisibility(View.GONE);

        }

        holder.qty.setText(qty);
        holder.itemView.setTag(id);

    }
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }
    class GroceryViewHolder extends RecyclerView.ViewHolder {

        // Class variables for the task description and priority TextViews
        TextView item;
        TextView qty;
        TextView deal;



        public GroceryViewHolder(View itemView) {
            super(itemView);

            item = (TextView) itemView.findViewById(R.id.itemName);
            qty = (TextView) itemView.findViewById(R.id.itemQty);
            deal = (TextView) itemView.findViewById(R.id.deal_view);

        }
    }
}
