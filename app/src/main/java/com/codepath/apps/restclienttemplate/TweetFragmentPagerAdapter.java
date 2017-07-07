package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.restclienttemplate.fragments.MentionsListFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetListFragment;

/**
 * Created by mbankole on 7/3/17.
 */

public class TweetFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Timeline", "Mentions" };
    private Context context;
    FragmentManager fragmentManager;
    TweetListFragment tweetListFragment;
    MentionsListFragment mentionsListFragment;

    public TweetFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        fragmentManager = fm;
        this.context = context;
    }

    public TweetListFragment getTweetListFragment() {
        if (tweetListFragment == null) {
            tweetListFragment = new TweetListFragment();
            tweetListFragment.setFm(fragmentManager);
        }
        return tweetListFragment;
    }

    public MentionsListFragment getMentionsListFragment() {
        if (mentionsListFragment == null) {
            mentionsListFragment = new MentionsListFragment();
            mentionsListFragment.setFm(fragmentManager);
        }
        return mentionsListFragment;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return getTweetListFragment();
            case 1:
                return getMentionsListFragment();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
