package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Andreas on 28.09.2015.
 * Sources: http://developer.android.com/guide/topics/appwidgets/index.html
 * http://dharmangsoni.blogspot.no/2014/03/collection-widget-with-event-handling.html
 */
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    private static final String LOG_TAG = WidgetDataProvider.class.getSimpleName();
    private ArrayList<List> mCollections = new ArrayList();
    private Context mContext;

    private static final int COL_MATCHTIME = 0;
    private static final int COL_HOME = 1;
    private static final int COL_AWAY = 2;
    private static final int COL_HOME_GOALS = 3;
    private static final int COL_AWAY_GOALS = 4;

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
        // get current date for SQL query
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String[] selectionArgs = {currentDate};

        // Get latest matches from database.
        ScoresDBHelper dbHelper = new ScoresDBHelper(mContext);
        Cursor cursor = dbHelper.getReadableDatabase().query(DatabaseContract.SCORES_TABLE,
                null,
                ScoresProvider.SCORES_BY_DATE,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null) {
            // Add cursor data to a temporary list and add the list to collections array
            // to be populated in getViewAt().
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    List<CharSequence> scoresData = new ArrayList();
                    scoresData.add(COL_MATCHTIME, cursor.getString(ScoresAdapter.COL_MATCHTIME));
                    scoresData.add(COL_HOME, cursor.getString(ScoresAdapter.COL_HOME));
                    scoresData.add(COL_AWAY, cursor.getString(ScoresAdapter.COL_AWAY));
                    scoresData.add(COL_HOME_GOALS, cursor.getString(ScoresAdapter.COL_HOME_GOALS));
                    scoresData.add(COL_AWAY_GOALS, cursor.getString(ScoresAdapter.COL_AWAY_GOALS));

                    mCollections.add(scoresData);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

        // Assign data to a temporary array and populate data to the remote view
        List<CharSequence> scoresData = mCollections.get(position);
        rv.setTextViewText(R.id.widget_home_name, scoresData.get(COL_HOME));
        rv.setTextViewText(R.id.widget_away_name, scoresData.get(COL_AWAY));
        String score = " - ";
        if (!scoresData.get(COL_HOME_GOALS).equals("-1")) {
            score = scoresData.get(COL_HOME_GOALS) + " - " + scoresData.get(COL_AWAY_GOALS);
        }
        rv.setTextViewText(R.id.widget_score, score);
        rv.setTextViewText(R.id.widget_match_time, scoresData.get(COL_MATCHTIME));

        // Set item click event
        final Intent fillInIntent = new Intent();
        fillInIntent.setAction(WidgetProvider.ACTION_START_ACTIVITY);
        final Bundle bundle = new Bundle();
        bundle.putString(WidgetProvider.EXTRA_MESSAGE, (String) scoresData.get(COL_HOME) +
                " vs " + (String) scoresData.get(COL_AWAY));
        fillInIntent.putExtras(bundle);
        rv.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

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
}
