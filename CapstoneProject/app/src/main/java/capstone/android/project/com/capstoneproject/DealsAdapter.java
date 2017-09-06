package capstone.android.project.com.capstoneproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import capstone.android.project.com.capstoneproject.data.Deals;
import capstone.android.project.com.capstoneproject.data.DealsContract;
import capstone.android.project.com.capstoneproject.data.Grocery;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Shashvati on 8/19/2017.
 */

public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.DealsAdapterViewHolder>{
    private Deals[] mDeals;
    final Context mContext;
    private String mItemName;
    public DealsAdapterCallback mCallback;

    public interface DealsAdapterCallback{
        void onMethodCallback(Deals savedDeals,boolean checked);
    }


    public DealsAdapter(final Context context,Deals[] deals,String itemName, DealsAdapterCallback callback) {
        mDeals = deals;
        mContext = context;
        mItemName = itemName;
        mCallback = callback;
    }

    public class DealsAdapterViewHolder extends RecyclerView.ViewHolder {
        public final ImageView dealImages;
        public final TextView dealProduct;
        public final TextView dealStore;
        public final TextView dealPrice;
        public final TextView dealexp;
        public final View itemViewSelect;
        public final ToggleButton dealChecker;

        public DealsAdapterViewHolder(View itemView) {
            super(itemView);
            itemViewSelect = itemView;
            dealImages = (ImageView) itemView.findViewById(R.id.deal_imgs);
            dealProduct = (TextView) itemView.findViewById(R.id.deal_product);
            dealStore = (TextView) itemView.findViewById(R.id.deal_store);
            dealPrice = (TextView) itemView.findViewById(R.id.deal_price);
            dealChecker = (ToggleButton) itemView.findViewById(R.id.checkDeals);
            dealexp = (TextView) itemView.findViewById(R.id.expiry_dt);
        }
    }
    @Override
    public DealsAdapter.DealsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.store_prices_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately=false;
        View view=inflater.inflate(layoutId,parent,shouldAttachToParentImmediately);
        return new DealsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DealsAdapter.DealsAdapterViewHolder holder, final int position) {
        final Deals eachDeal = mDeals[position];
        if (eachDeal!=null){
            Glide.with(mContext).load(eachDeal.getDealImgs()).into(holder.dealImages);
            holder.dealProduct.setText(eachDeal.getBrandItems());
            holder.dealStore.setText(eachDeal.getStoreNames());
            holder.dealPrice.setText(eachDeal.getPrice());
            SharedPreferences shared = mContext.getSharedPreferences("MY SAVED DEALS", MODE_PRIVATE);
            if(shared.contains(eachDeal.getBrandItems())){
                boolean state = false;
                if(shared.getString(eachDeal.getBrandItems(),"").equals(eachDeal.getStoreNames())){
                    state = true;
                }
                holder.dealChecker.setChecked(state);
            }
            holder.dealexp.setText(eachDeal.getExpiryDate());

            holder.dealChecker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onMethodCallback(eachDeal,holder.dealChecker.isChecked());

              }
            });


        }

    }

    private static String getDate(String dateStr) {

        Calendar date = Calendar.getInstance();
        int dow;
        switch(dateStr){
            case "Sunday":
                dow = 1;
                break;
            case "Monday":
                dow = 2;
                break;
            case "Tuesday":
                dow = 3;
                break;
            case "Wednesday":
                dow = 4;
                break;
            case "Thursday":
                dow = 5;
                break;
            case "Friday":
                dow = 6;
                break;
            case "Saturday":
                dow = 7;
                break;
            default:
                dow=0;
                break;
        }
        int today = date.get(Calendar.DAY_OF_WEEK);
        int diff = dow - today;
        if (!(diff > 0)) {
            diff += 7;
        }
        date.add(Calendar.DAY_OF_MONTH,diff);
        String dateNext = date.get(Calendar.YEAR) + "-" + date.get(Calendar.MONTH) + "-" + date.get(Calendar.DATE);
        return dateNext;
    }

    @Override
    public int getItemCount() {
        if(null==mDeals){
            return 0;
        }
        else {
            return mDeals.length;
        }
    }
}
