package capstone.android.project.com.capstoneproject;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import java.io.IOException;
import java.net.URL;

import capstone.android.project.com.capstoneproject.data.Deals;
import capstone.android.project.com.capstoneproject.data.Grocery;
import capstone.android.project.com.capstoneproject.util.RemoteEndPointUtil;
import timber.log.Timber;


public class StorePricesActivity extends AppCompatActivity{
    private EditText zipCode;
    private static String TAG = StorePricesActivity.class.toString();
    private Button findDeals;
    private Grocery groceryItem;
    private int size;
    private DealsAdapter mAdapter;
    private String itemName;
    private String zip;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_prices);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        zipCode = (EditText) findViewById(R.id.zipCode);
        findDeals = (Button) findViewById(R.id.find_deals);
        Intent itemIntent=getIntent();
        groceryItem = itemIntent.getParcelableExtra("itemDeals");
        itemName = groceryItem.getItem();
        findDeals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    zip = zipCode.getText().toString();
                   if(isOnline()) {
                        URL url = RemoteEndPointUtil.buildLatLongURL(zip);
                        new FetchLocation().execute(url);
                   }
                   else {
                      Toast.makeText(StorePricesActivity.this," No Internet Connection.", Toast.LENGTH_LONG).show();
                   }

                }
        });

    }



    public class FetchLocation extends AsyncTask<URL,Void,double[]>{
        @Override
        protected  double[] doInBackground(URL... params) {
            String results = null;
            URL locationURL = params[0];
            double[] latlng=new double[2];
            try {
                results = RemoteEndPointUtil.getResponseFromHttpUrl(locationURL);

            } catch (IOException e) {
                Timber.e("IOException",e);
                e.printStackTrace();
            }
            try {
                latlng = RemoteEndPointUtil.getLatLong(results);
            } catch (JSONException e) {
                Timber.e("JSONException",e);
                e.printStackTrace();
            }

            return latlng;
        }

        @Override
        protected void onPostExecute(double[] latlng) {
            URL dealURL;
            dealURL=RemoteEndPointUtil.buildURL(itemName, zip, latlng);
            ItemFragment item = new ItemFragment();
            item.setItemUrl(dealURL.toString());
            item.setItemName(itemName);
            FragmentManager fragmentManager = getSupportFragmentManager();
            Timber.i("Starting Item Fragment ",item);
            fragmentManager.beginTransaction()
                    .add(R.id.deals_container,item)
                    .commit();

        }

    }
    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          {
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
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        Intent i = getIntent();
        setResult(RESULT_OK, i);
        Timber.i("StorePricesActivity ended",i);
        finish();
    }
}

