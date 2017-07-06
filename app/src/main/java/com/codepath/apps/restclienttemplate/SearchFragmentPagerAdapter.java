package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.restclienttemplate.fragments.TweetSearchResultsFragment;
import com.codepath.apps.restclienttemplate.fragments.UserSearchResultsFragment;

/**
 * Created by mbankole on 7/5/17.
 */

public class SearchFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Tweets", "Users" };
    private Context context;
    FragmentManager fragmentManager;
    TweetSearchResultsFragment tweetListFragment;
    UserSearchResultsFragment userListFragment;
    String query;

    public SearchFragmentPagerAdapter(FragmentManager fm, Context context, String query) {
        super(fm);
        fragmentManager = fm;
        this.context = context;
        this.query = query;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                tweetListFragment = TweetSearchResultsFragment.newInstance(query);
                tweetListFragment.setFm(fragmentManager);
                return tweetListFragment;
            case 1:
                userListFragment = UserSearchResultsFragment.newInstance(query);
                userListFragment.setFm(fragmentManager);
                return userListFragment;
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
