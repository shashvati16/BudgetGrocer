package capstone.android.project.com.capstoneproject;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.icu.text.DateFormat;
import android.icu.text.DateFormatSymbols;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import capstone.android.project.com.capstoneproject.data.Deals;
import capstone.android.project.com.capstoneproject.data.DealsContract;
import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;

public class ItemFragment extends Fragment implements LoaderManager.LoaderCallbacks<Deals[]>, DealsAdapter.DealsAdapterCallback{

    private String itemUrl;
    private String itemName;
    private Parcelable[] mDeals;
    private TextView itemInfo;

    private ArrayList<Deals> savedDeals = new ArrayList<Deals>();
    private RecyclerView dealsRecycleView;
    private DealsAdapter dealsAdapter;
    private Deals[] itemDeals;


    private static final int DEAL_LOADER_ID = 0;


    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public ItemFragment() {
    }
    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dv = inflater.inflate(R.layout.fragment_item, container, false);
        if (savedInstanceState != null) {
            mDeals = savedInstanceState.getParcelableArray("deals");
        }
        else {
            mDeals = (Parcelable[]) itemDeals;
        }

        itemInfo = (TextView) dv.findViewById(R.id.itemInfo);
        dealsRecycleView = (RecyclerView) dv.findViewById(R.id.dealsRecycleView);


        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            dealsRecycleView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }
        else {
            dealsRecycleView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        }

        dealsRecycleView.setHasFixedSize(true);

        LoaderManager.LoaderCallbacks<Deals[]> callback = this;
        Bundle bundleForLoader = null;
        int loaderId = DEAL_LOADER_ID;
        getLoaderManager().initLoader(loaderId, bundleForLoader, callback);
        return dv;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray("deals", mDeals);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Deals[]> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Deals[]>(getActivity()) {
            @Override
            protected void onStartLoading() {
                if (itemDeals != null) {
                    deliverResult(itemDeals);
                } else {
                    forceLoad();
                }
            }
            @Override
            public Deals[] loadInBackground() {
                Document page=null;
                try {
                    if(isOnline()==true) {
                        page = Jsoup.connect(itemUrl).get();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Elements divs = page.getElementsByClass("deal-productname");
                int size = divs.size();
                if(size>1){
                    itemDeals = new Deals[size];
                    for (int i = 0; i < size; i++) {
                        itemDeals[i] = new Deals();
                        itemDeals[i].setDealImgs(page.select("img.deal-productimg").get(i).absUrl("src"));
                        itemDeals[i].setBrandItems(page.select("p.deal-productname").get(i).text());
                        itemDeals[i].setStoreNames(page.select("p.deal-storename").get(i).text());
                        itemDeals[i].setPrice(page.select("span.pricetag").get(i).text());
                        itemDeals[i].setExpiryDate(page.select("div.expirydate").get(i).text().substring(5));

                    }
                }
                else{
                    itemDeals = new Deals[1];
                    itemDeals[0]=new Deals();
                    itemDeals[0].setBrandItems("No deals found for " + itemName);
                }
                return itemDeals;
            }

        };

    }


    @Override
    public void onLoadFinished(Loader<Deals[]> loader, Deals[] deals) {
        if(deals.length>1 && deals[0].getBrandItems()=="No deals found for " + itemName){
            itemInfo.setVisibility(View.VISIBLE);
            itemInfo.setText(deals[0].getBrandItems());
            itemInfo.setTextSize(30);
            dealsRecycleView.setVisibility(View.GONE);
        }
        else {
            itemInfo.setVisibility(View.GONE);
            dealsAdapter = new DealsAdapter(getActivity(), deals, itemName, this);
            dealsRecycleView.setAdapter(dealsAdapter);


        }
    }
    @Override
    public void onLoaderReset(Loader<Deals[]> loader) {}
    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e){
            e.printStackTrace();
            Timber.e("IOException",e);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            Timber.e("InterruptedException",e);
        }
        return false;
    }


    @Override
    public void onMethodCallback(Deals checkedDeal,boolean isChecked) {
        if(isChecked==true){
            checkedDeal.setExpiryDate(getDate(checkedDeal.getExpiryDate()));
            savedDeals.add(checkedDeal);
            Toast.makeText(getActivity(),checkedDeal.getBrandItems() + " saved!",Toast.LENGTH_LONG).show();
        }
        else {
            if(savedDeals.contains(checkedDeal)){
                savedDeals.remove(checkedDeal);
                Toast.makeText(getActivity(),checkedDeal.getBrandItems() + " removed!",Toast.LENGTH_LONG).show();
            }

        }
        saveResults(savedDeals);

    }
    @TargetApi(Build.VERSION_CODES.N)
    private static String getDate(String dateStr) {
        String dateNext;
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

        if(dow!=0) {
            int diff = dow - today;
            if (!(diff > 0)) {
                diff += 7;
            }

            date.add(Calendar.DAY_OF_MONTH, diff);
            int month = date.get(Calendar.MONTH) + 1;

            dateNext = date.get(Calendar.YEAR) + "-" + month + "-" + date.get(Calendar.DATE);
        }
        else{
            dateNext = dateStr.replaceAll("/","-");
        }
        return dateNext;
    }


    public void saveResults(ArrayList<Deals> deals) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("savedDeals",savedDeals);
        intent.putExtras(bundle);
        getActivity().setIntent(intent);
    }
}
