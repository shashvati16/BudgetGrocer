package capstone.android.project.com.capstoneproject.util;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import capstone.android.project.com.capstoneproject.R;

/**
 * Implementation of App Widget functionality.
 */
public class GroceryListWidget extends AppWidgetProvider {
    public static final String ACTION_SHOPPING_LIST_CHANGED = "capstone.android.project.com.capstoneproject.util.SHOPPING_LIST_CHANGED";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(
                    context.getPackageName(),
                    R.layout.grocery_list_widget
            );
            views.setTextViewText(R.id.appwidget_text,context.getString(R.string.appwidget_text));
            Intent intent = new Intent(context, GroceryListWidgetService.class);
            views.setRemoteAdapter(R.id.shoppingList, intent);
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
    }


}

