package capstone.android.project.com.capstoneproject.util;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Shashvati on 9/4/2017.
 */

public class GroceryListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GroceryListRemoteViewsFactory(this.getApplicationContext(),intent);
    }
}
