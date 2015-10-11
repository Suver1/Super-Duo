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
        mPagerHandler.setCurrentItem(MainActivity.current_fragment); // Set current selected page

        // Set color to Pager's tab strip
        PagerTabStrip pagerTabStrip = (PagerTabStrip) rootView.findViewById(R.id.pager_header);
        pagerTabStrip.setTabIndicatorColorResource(R.color.teal05);

        return rootView;
    }
    private class MyPageAdapter extends FragmentStatePagerAdapter
    {
        private int lastPage = -1;
        private int counter = 0;
        private boolean resetCounter = false;
        private final int numPages = PagerFragment.NUM_PAGES - 1;
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
            return getDayName(getActivity(), position);
        }
        public String getDayName(Context context, int position) {
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.

            // Find the selected page position and add a string to the end of the page title
            // Example. "Today" will become "Today's matches", but the previous and next page title
            // will be "Yesterday" and "Tomorrow" respectively.
            // This function runs three times for each swipe, once for each page, unless the first
            // or last page is reached.
            String titlePostfix = "";
            if (counter == 0) { // First page
                if (lastPage == 1 && position == 0) {
                    // First page reached, (the user has swiped all the way to the left)
                    titlePostfix = "'s matches";
                    lastPage = 0;
                    // First page will only count two pages, reset counter when it reaches 1
                    resetCounter = true;
                    MainActivity.current_fragment = 0;
                }
                counter++;
            }
            else if (counter == 1) { // Second page
                if (!resetCounter) {
                    counter++;
                    if (lastPage != 0 || lastPage == 0 && position == 1) {
                        // Second page reached
                        titlePostfix = "'s matches";
                        lastPage = position;
                        if (position == numPages) {
                            // Last page reached, (the user has swiped all the way to the right)
                            counter = 0;
                            MainActivity.current_fragment = numPages;
                        } else {
                            MainActivity.current_fragment = position;
                        }
                    }
                } else {
                    // Reset counter
                    counter = 0;
                    resetCounter = false;
                }
            } else { // Third page
                counter = 0;
            }

            long dateInMillis = System.currentTimeMillis()+((position-2)*86400000);

            Time t = new Time();
            t.setToNow();
            //Log.e(LOG_TAG, "time now: " + t.toString());
            int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
            //Log.e(LOG_TAG, "julianDay: " + julianDay);
            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
            //Log.e(LOG_TAG, "currentJulianDay: " + currentJulianDay);
            if (julianDay == currentJulianDay) {
                return context.getString(R.string.today) + titlePostfix;
            } else if ( julianDay == currentJulianDay +1 ) {
                return context.getString(R.string.tomorrow) + titlePostfix;
            }
             else if ( julianDay == currentJulianDay -1)
            {
                return context.getString(R.string.yesterday) + titlePostfix;
            }
            else
            {
                Time time = new Time();
                time.setToNow();
                // Otherwise, the format is just the day of the week (e.g "Wednesday".
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
                //Log.e(LOG_TAG, "dayFormat: " + dayFormat);
                return dayFormat.format(dateInMillis) + titlePostfix;
            }
        }
    }
}
