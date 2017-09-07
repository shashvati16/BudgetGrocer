package capstone.android.project.com.capstoneproject.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Shashvati on 8/22/2017.
 */

public class DealsDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="grocery.db";

    private static final int DATABASE_VERSION = 3;
    public DealsDBHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_DEAL_TABLE = "CREATE TABLE " +
                DealsContract.DealEntry.TABLE_NAME + " (" +
                DealsContract.DealEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,  " +
                DealsContract.DealEntry.COLUMN_DEAL_NAME + " VARCHAR(20), " +
                DealsContract.DealEntry.COLUMN_DEAL_QTY + " VARCHAR(20) NOT NULL, " +
                DealsContract.DealEntry.COLUMN_DEAL_ITEM + " VARCHAR(20) NOT NULL, " +
                DealsContract.DealEntry.COLUMN_DEAL_STORE + " VARCHAR(20), " +
                DealsContract.DealEntry.COLUMN_DEAL_PRICE + " VARCHAR(20), " +
                DealsContract.DealEntry.COLUMN_DEAL_IMAGE + " VARCHAR(50), " +
                DealsContract.DealEntry.COLUMN_DEAL_DATE + " INTEGER, " +
                " UNIQUE (" + DealsContract.DealEntry.COLUMN_DEAL_NAME + " , "
                + DealsContract.DealEntry.COLUMN_DEAL_STORE + ") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_DEAL_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DealsContract.DealEntry.TABLE_NAME);
        onCreate(db);

    }
}
