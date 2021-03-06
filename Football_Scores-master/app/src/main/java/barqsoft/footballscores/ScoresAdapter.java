package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorAdapter {
    private static final String LOG_TAG = ScoresAdapter.class.getSimpleName();
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;
    public double detail_match_id = 0;
    private String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";
    public ScoresAdapter(Context context,Cursor cursor,int flags)
    {
        super(context,cursor,flags);
    }

    // Inflate a new view template
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);
        //Log.v(LOG_TAG, "new View inflated.");
        return mItem;
    }

    // Bind all data to a given view to populate the template content for the item
    @Override
    public void bindView(View view, final Context context, Cursor cursor)
    {
        // Set score info in 'scores_list_item'
        final ViewHolder mHolder = (ViewHolder) view.getTag();
        Resources resources = context.getResources();
        String homeTeam = resources.getString(R.string.home_team);
        String awayTeam = resources.getString(R.string.away_team);
        String homeName = cursor.getString(COL_HOME);
        String awayName = cursor.getString(COL_AWAY);
        String matchTime = cursor.getString(COL_MATCHTIME);
        // Scores description ex: Scores, Home team 1, Away team 2
        String scoresDesc = "";
        if (cursor.getInt(COL_HOME_GOALS) >= 0) {
            scoresDesc = resources.getString(R.string.scores) + ", " +
                    homeTeam + " " + cursor.getInt(COL_HOME_GOALS) + ", " +
                    awayTeam + " " + cursor.getInt(COL_AWAY_GOALS);
        }
        // Set content description for better talkBack experience
        view.setContentDescription(homeTeam + " " + homeName + ", " + awayTeam + " " + awayName +
                ". " + scoresDesc + ". " + resources.getString(R.string.match_time) + " " + matchTime);
        mHolder.home_name.setText(homeName);
        mHolder.away_name.setText(awayName);
        mHolder.date.setText(matchTime);
        mHolder.score.setText(Utilies.getScores(cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS)));
        mHolder.match_id = cursor.getDouble(COL_ID);
        mHolder.home_crest.setImageResource(Utilies.getTeamCrestByTeamName(
                cursor.getString(COL_HOME)));
        mHolder.away_crest.setImageResource(Utilies.getTeamCrestByTeamName(
                cursor.getString(COL_AWAY)
        ));
        //Log.e(LOG_TAG, mHolder.home_name.getText() + " Vs. " + mHolder.away_name.getText() +" id " + String.valueOf(mHolder.match_id));
        //Log.v(LOG_TAG, String.valueOf(detail_match_id));

        // Prepare detailed view
        LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, (ViewGroup) view.getParent());
        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);

        // When a detailed view is clicked, the clicked match's id is sent to the adapter.
        if(mHolder.match_id == detail_match_id)
        {
            // Add detailed view
            //Log.v(LOG_TAG,"will insert extraView");
            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            TextView matchDayView = (TextView) container.findViewById(R.id.matchday_textview);
            Integer matchDay = cursor.getInt(COL_MATCHDAY);
            Integer league = cursor.getInt(COL_LEAGUE);
            matchDayView.setText(Utilies.getMatchDay(context, matchDay, league));
            TextView leagueView = (TextView) container.findViewById(R.id.league_textview);
            leagueView.setText(Utilies.getLeague(context, league));
            container.setContentDescription(Utilies.getLeague(context, league) + ", " +
                    Utilies.getMatchDay(context, matchDay, league));

            Button share_button = (Button) v.findViewById(R.id.share_button);
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    //add Share Action
                    context.startActivity(createShareMatchIntent(mHolder.home_name.getText()+" "
                    +mHolder.score.getText()+" "+mHolder.away_name.getText() + " "));
                }
            });
        }
        else
        {
            // remove detailed view
            container.removeAllViews();
        }

    }
    @SuppressWarnings("deprecation")
    public Intent createShareMatchIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (Build.VERSION.SDK_INT >= 21) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        } else {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }
}
