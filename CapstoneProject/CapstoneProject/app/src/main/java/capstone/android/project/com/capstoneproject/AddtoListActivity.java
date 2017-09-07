package capstone.android.project.com.capstoneproject;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import capstone.android.project.com.capstoneproject.data.Deals;
import capstone.android.project.com.capstoneproject.data.DealsContract;
import capstone.android.project.com.capstoneproject.data.Grocery;
import timber.log.Timber;

public class AddtoListActivity extends AppCompatActivity {
    private Button deals;
    private EditText existingItem;
    private EditText existingItemQty;
    private TextView deal_saved;
    private Spinner qty_unit;
    private Button addItem;
    private Grocery itemDeals;
    boolean backFromChild =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addto_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        deals = (Button) findViewById(R.id.view_deals);
        existingItem = (EditText) findViewById(R.id.item);
        existingItemQty = (EditText) findViewById(R.id.quantity);
        qty_unit = (Spinner) findViewById(R.id.units);
        addItem = (Button) findViewById(R.id.add);
        deals = (Button) findViewById(R.id.view_deals);
        deal_saved = (TextView) findViewById(R.id.deal_saved);
        itemDeals = new Grocery();
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (existingItem.getText().toString().isEmpty()) {
                    Toast.makeText(AddtoListActivity.this, getString(R.string.ItemNameNotEmpty), Toast.LENGTH_LONG).show();
                } else {
                    itemDeals.setItems(existingItem.getText().toString());
                    itemDeals.setQuantity(existingItemQty.getText().toString() + qty_unit.getSelectedItem());
                    int length;
                    if(itemDeals.getDeals()!=null){
                        length = itemDeals.getDeals().length;
                        Deals[] deals = itemDeals.getDeals();
                        for(int i = 0;i<length;i++){

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DealsContract.DealEntry.COLUMN_DEAL_ITEM,itemDeals.getItem());
                            contentValues.put(DealsContract.DealEntry.COLUMN_DEAL_QTY,itemDeals.getQuantity());
                            contentValues.put(DealsContract.DealEntry.COLUMN_DEAL_NAME, deals[i].getBrandItems());
                            contentValues.put(DealsContract.DealEntry.COLUMN_DEAL_STORE, deals[i].getStoreNames());
                            contentValues.put(DealsContract.DealEntry.COLUMN_DEAL_PRICE, deals[i].getPrice());
                            contentValues.put(DealsContract.DealEntry.COLUMN_DEAL_IMAGE, deals[i].getDealImgs());
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                            try {
                                contentValues.put(DealsContract.DealEntry.COLUMN_DEAL_DATE, sdf.parse(deals[i].getExpiryDate()).getTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Uri uri = getContentResolver().insert(DealsContract.DealEntry.CONTENT_URI, contentValues);
                            if(uri != null) {
                                Toast.makeText(AddtoListActivity.this, itemDeals.getItem() + " " + getString(R.string.added), Toast.LENGTH_LONG).show();
                                Timber.i(itemDeals.getItem() + " " + getString(R.string.added), itemDeals);
                            }
                        }
                    }else {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DealsContract.DealEntry.COLUMN_DEAL_ITEM, itemDeals.getItem());
                        contentValues.put(DealsContract.DealEntry.COLUMN_DEAL_QTY, itemDeals.getQuantity());
                        Uri uri = getContentResolver().insert(DealsContract.DealEntry.CONTENT_URI, contentValues);
                        if(uri != null) {

                            Toast.makeText(AddtoListActivity.this, itemDeals.getItem() + " " + getString(R.string.added), Toast.LENGTH_LONG).show();
                            Timber.i(itemDeals.getItem() + " " + getString(R.string.added), itemDeals);
                        }
                    }
                    Intent i = new Intent(AddtoListActivity.this, MainActivity.class);
                    Timber.i("Returning to MainActivity", i);
                    startActivity(i);
                }
            }
        });
    }
    public void viewDeals(View v){
        Intent storeIntent = new Intent(AddtoListActivity.this,StorePricesActivity.class);
        if(existingItem.getText().toString()!=null) {
            itemDeals.setItems(existingItem.getText().toString());
            itemDeals.setQuantity(existingItemQty.getText().toString() + qty_unit.getSelectedItem());
            storeIntent.putExtra("itemDeals", itemDeals);
            startActivityForResult(storeIntent,1);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ArrayList<Deals> savedDeals;
        if(requestCode==1){
            if (resultCode == RESULT_OK) {
                Bundle b = data.getExtras();
                savedDeals = b.getParcelableArrayList("savedDeals");
                backFromChild = true;
                if(savedDeals!=null) {
                    int size = savedDeals.size();
                    Deals[] saveDeals = new Deals[size];
                    for(int i=0;i<size;i++) {
                        saveDeals[i]=savedDeals.get(i);
                    }
                    itemDeals.setDeals(saveDeals);
                    deal_saved.setText(saveDeals.length + " " + getString(R.string.dealsSaved));
                    Timber.i(saveDeals.length + " " + getString(R.string.dealsSaved) ,savedDeals);
                }
                else {
                    deal_saved.setText(getString(R.string.NoDeals));
                    Timber.i(" " + getString(R.string.dealsSaved),savedDeals);
                }

            }

        }
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

}
