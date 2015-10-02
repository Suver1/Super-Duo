package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andreas on 28.09.2015.
 * Sources: http://developer.android.com/guide/topics/appwidgets/index.html
 * http://dharmangsoni.blogspot.no/2014/03/collection-widget-with-event-handling.html
 */
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    private static final String LOG_TAG = WidgetDataProvider.class.getSimpleName();
    private List<CharSequence> mCollections = new ArrayList();
    private Context mContext;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        // ANR after 20 sec. Do heavy lifting in onDataSetChanged() or getViewAt().
    }

    @Override
    public int getCount() {
        return mCollections.size();
    }

    @Override
    public void onDataSetChanged() {
        initData();

        // Get latest matches from database.
        ScoresDBHelper dbHelper = new ScoresDBHelper(mContext);
        Cursor cursor = dbHelper.getReadableDatabase().query(DatabaseContract.SCORES_TABLE,
                null, null, null, null, null, null);
        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                Log.d(LOG_TAG, cursor.getString(cursor.getColumnIndex("date")));
                Log.d(LOG_TAG, cursor.getString(cursor.getColumnIndex("home")));
                Log.d(LOG_TAG, cursor.getString(cursor.getColumnIndex("away")));
                // TODO add data to collections
                cursor.moveToNext();
            }
        }
        cursor.close();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        rv.setTextViewText(R.id.widget_home_name, mCollections.get(position));
        return rv;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onDestroy(){

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        mCollections.clear();
        for (int i = 1; i <= 10; i++) {
            mCollections.add("ListView item " + i);
        }
    }
}
