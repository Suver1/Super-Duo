package barqsoft.footballscores;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import barqsoft.footballscores.service.WidgetService;

/**
 * Created by Andreas on 24.09.2015.
 * Sources: http://developer.android.com/guide/topics/appwidgets/index.html
 * http://dharmangsoni.blogspot.no/2014/03/collection-widget-with-event-handling.html
 *
 * AppWidgetProvider parses relevant fields out of the Intent that is received in onReceive().
 * It holds all the logic for creating the widget views, filling the views with the correct data,
 * and setting up the click listeners so the widget will respond to touch.
 */
public class WidgetProvider extends AppWidgetProvider {
    private static final String LOG_TAG = WidgetProvider.class.getSimpleName();
    public static final String ACTION_START_ACTIVITY = "barqsoft.footballscores.widgets.ACTION_START_ACTIVITY";
    public static final String EXTRA_ID = "barqsoft.footballscores.widgets.EXTRA_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_START_ACTIVITY)) {
            Intent mainActivity = new Intent(context, MainActivity.class);
            mainActivity.setAction(ACTION_START_ACTIVITY);
            mainActivity.putExtras(intent.getExtras());
            mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivity);
        }

        super.onReceive(context, intent);
    }

    @SuppressLint("NewApi")
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_provider_layout);
            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            remoteViews.setRemoteAdapter(widgetId, R.id.widgetCollectionList, intent);

            // Add collection list item handler
            final Intent onItemClick = new Intent(context, WidgetProvider.class);
            onItemClick.setAction(ACTION_START_ACTIVITY);
            onItemClick.setData(Uri.parse(onItemClick.toUri(Intent.URI_INTENT_SCHEME)));
            final PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0,
                    onItemClick, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.widgetCollectionList, onClickPendingIntent);

            // The empty view is displayed when the collection has no items.
            remoteViews.setEmptyView(R.id.widgetCollectionList, R.id.emptyWidgetView);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
