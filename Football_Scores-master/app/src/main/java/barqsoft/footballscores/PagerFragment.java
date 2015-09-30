package barqsoft.footballscores;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class PagerFragment extends Fragment
{
    private static final String LOG_TAG = PagerFragment.class.getSimpleName();
    public static final int NUM_PAGES = 10;
    public ViewPager mPagerHandler;
    private MyPageAdapter mPagerAdapter;
    private MainScreenFragment[] viewFragments = new MainScreenFragment[NUM_PAGES];
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
        mPagerAdapter = new MyPageAdapter(getChildFragmentManager());
        for (int i = 0;i < NUM_PAGES;i++)
        {
            Date fragmentDate = new Date(System.currentTimeMillis()+((i-2)*86400000));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            viewFragments[i] = new MainScreenFragment();
            viewFragments[i].setFragmentDate(format.format(fragmentDate));
        }
        mPagerHandler.setAdapter(mPagerAdapter);
        mPagerHandler.setCurrentItem(MainActivity.current_fragment);

        // Set color to Pager's tab strip
        PagerTabStrip pagerTabStrip = (PagerTabStrip) rootView.findViewById(R.id.pager_header);
        pagerTabStrip.setTabIndicatorColorResource(R.color.teal05);

        return rootView;
    }
    private class MyPageAdapter extends FragmentStatePagerAdapter
    {
        @Override
        public Fragment getItem(int i)
        {
            return viewFragments[i];
        }

        @Override
        public int getCount()
        {
            return NUM_PAGES;
        }

        public MyPageAdapter(FragmentManager fm)
        {
            super(fm);
        }
        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position)
        {
            return getDayName(getActivity(),System.currentTimeMillis()+((position-2)*86400000));
        }
        public String getDayName(Context context, long dateInMillis) {
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.

            Time t = new Time();
            t.setToNow();
            //Log.e(LOG_TAG, "time now: " + t.toString());
            int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
            //Log.e(LOG_TAG, "julianDay: " + julianDay);
            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
            //Log.e(LOG_TAG, "currentJulianDay: " + currentJulianDay);
            if (julianDay == currentJulianDay) {
                return context.getString(R.string.today);
            } else if ( julianDay == currentJulianDay +1 ) {
                return context.getString(R.string.tomorrow);
            }
             else if ( julianDay == currentJulianDay -1)
            {
                return context.getString(R.string.yesterday);
            }
            else
            {
                Time time = new Time();
                time.setToNow();
                // Otherwise, the format is just the day of the week (e.g "Wednesday".
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
                //Log.e(LOG_TAG, "dayFormat: " + dayFormat);
                return dayFormat.format(dateInMillis);
            }
        }
    }
}
