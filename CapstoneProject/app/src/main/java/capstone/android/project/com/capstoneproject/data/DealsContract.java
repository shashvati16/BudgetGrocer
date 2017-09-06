package capstone.android.project.com.capstoneproject.data;

import android.net.Uri;
import android.provider.BaseColumns;



/**
 * Created by Shashvati on 8/22/2017.
 */

public class DealsContract {
    public static final String CONTENT_AUTHORITY = "capstone.android.project.com.capstoneproject";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_DEAL="deals";

    public static final class DealEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_DEAL)
                .build();
        public static final String TABLE_NAME = "grocery_deals";
        public static final String COLUMN_DEAL_ITEM = "deal_item";
        public static final String COLUMN_DEAL_QTY ="deal_qty";
        public static final String COLUMN_DEAL_NAME = "deal_name";
        public static final String COLUMN_DEAL_STORE = "deal_store";
        public static final String COLUMN_DEAL_IMAGE = "deal_image";
        public static final String COLUMN_DEAL_PRICE = "deal_price";
        public static final String COLUMN_DEAL_DATE = "deal_date";

    }

}
