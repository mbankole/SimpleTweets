package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.restclienttemplate.fragments.FollowerListFragment;
import com.codepath.apps.restclienttemplate.fragments.UserInfoFragment;
import com.codepath.apps.restclienttemplate.fragments.UserTweetsListFragment;
import com.codepath.apps.restclienttemplate.models.User;

/**
 * Created by mbankole on 7/5/17.
 */

public class UserFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "UserInfo", "followers","Tweets" };
    private Context context;
    FragmentManager fragmentManager;
    UserInfoFragment userInfoFragment;
    UserTweetsListFragment userTweetsListFragment;
    FollowerListFragment followerListFragment;
    User user;

    public UserFragmentPagerAdapter(FragmentManager fm, Context context, User user) {
        super(fm);
        fragmentManager = fm;
        this.context = context;
        this.user = user;
        tabTitles = new String[] { "UserInfo", "followers (" + user.followerCount + ")","Tweets" };
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                userInfoFragment = UserInfoFragment.newInstance(user);
                return userInfoFragment;
            case 1:
                followerListFragment = FollowerListFragment.newInstance(user);
                return  followerListFragment;
            case 2:
                userTweetsListFragment = UserTweetsListFragment.newInstance(user);
                userTweetsListFragment.setFm(fragmentManager);
                return userTweetsListFragment;
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}