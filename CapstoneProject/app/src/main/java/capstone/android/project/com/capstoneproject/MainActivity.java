package capstone.android.project.com.capstoneproject;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import capstone.android.project.com.capstoneproject.data.DealsContract;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;
    private static final int DEAL_LOADER_ID = 0;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    private FloatingActionButton addItem;

    private String mUsername;
    private RecyclerView shoppingList;
    private GroceryListAdapter groceryListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUsername = ANONYMOUS;
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Toast.makeText(MainActivity.this, getString(R.string.hi) + " " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                    Timber.i(user.getDisplayName() + "Signed in!", user);


                } else {
                    // User is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER)
                                            .build(),
                            RC_SIGN_IN);
                }
            }
        };
        setContentView(R.layout.activity_main);
        addItem = (FloatingActionButton) findViewById(R.id.add_item);

        shoppingList = (RecyclerView) findViewById(R.id.grocery_list);
        shoppingList.setLayoutManager(new LinearLayoutManager(this));
        groceryListAdapter = new GroceryListAdapter(this);
        shoppingList.setAdapter(groceryListAdapter);
        removeOld();
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                String stringId = Long.toString(id);
                Uri uri = DealsContract.DealEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();
                int delete = getContentResolver().delete(uri, null, null);
                SharedPreferences mySavedDeals = getSharedPreferences("MY SAVED DEALS",MODE_PRIVATE);
                SharedPreferences.Editor editor = mySavedDeals.edit();
                editor.clear();
                editor.commit();
                if(delete>0) {
                    getSupportLoaderManager().restartLoader(DEAL_LOADER_ID, null, MainActivity.this);
                }

            }
        }).attachToRecyclerView(shoppingList);


        getSupportLoaderManager().initLoader(DEAL_LOADER_ID,null,MainActivity.this);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addItem= new Intent(MainActivity.this,AddtoListActivity.class);
                Timber.i("Starting AddtoListActivity",addItem);
                startActivity(addItem);
            }

        });

    }

    private void removeOld() {
        Calendar date = Calendar.getInstance();
        String stringDate = String.valueOf(date.getTimeInMillis());
        Uri uri = DealsContract.DealEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringDate).build();
        Timber.i("remove old deals",uri);
        getContentResolver().delete(uri, null, null);
        SharedPreferences mySavedDeals = getSharedPreferences("MY SAVED DEALS",MODE_PRIVATE);
        SharedPreferences.Editor editor = mySavedDeals.edit();
        editor.clear();
        editor.commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, getString(R.string.signedIn), Toast.LENGTH_SHORT).show();
                Timber.i(getString(R.string.signedIn),resultCode);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, getString(R.string.signInCancel), Toast.LENGTH_SHORT).show();
                Timber.i(getString(R.string.signInCancel),resultCode);
                finish();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        getSupportLoaderManager().restartLoader(DEAL_LOADER_ID, null, MainActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
       if (mAuthStateListener != null) {
           mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
           groceryListAdapter.swapCursor(null);
       }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu :
                // sign out
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.clear :
                Uri uri = DealsContract.DealEntry.CONTENT_URI;
                getContentResolver().delete(uri,null,null);
                getSupportLoaderManager().restartLoader(DEAL_LOADER_ID, null, this);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mDealData = null;
            @Override
            protected void onStartLoading() {
                if (mDealData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mDealData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }
            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(DealsContract.DealEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            DealsContract.DealEntry.COLUMN_DEAL_DATE);
                } catch (Exception e) {
                    Timber.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }
            public void deliverResult(Cursor data) {
                mDealData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        groceryListAdapter.swapCursor(data);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        groceryListAdapter.swapCursor(null);

    }
    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;

    }

}

