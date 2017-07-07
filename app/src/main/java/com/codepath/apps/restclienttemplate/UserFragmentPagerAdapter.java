package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.restclienttemplate.fragments.FollowerListFragment;
import com.codepath.apps.restclienttemplate.fragments.UserFriendsFragment;
import com.codepath.apps.restclienttemplate.fragments.UserInfoFragment;
import com.codepath.apps.restclienttemplate.fragments.UserTweetsListFragment;
import com.codepath.apps.restclienttemplate.models.User;

/**
 * Created by mbankole on 7/5/17.
 */

public class UserFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Tweets", "followers","Friends" };
    private Context context;
    FragmentManager fragmentManager;
    UserInfoFragment userInfoFragment;
    UserTweetsListFragment userTweetsListFragment;
    FollowerListFragment followerListFragment;
    UserFriendsFragment userFriendsFragment;
    User user;

    public UserFragmentPagerAdapter(FragmentManager fm, Context context, User user) {
        super(fm);
        fragmentManager = fm;
        this.context = context;
        this.user = user;
        tabTitles = new String[] { "Tweets (" + user.statusesCount + ")",
                "followers (" + user.followerCount + ")",
                "Friends (" + user.friendsCount + ")" };
    }

    public UserFriendsFragment getUserFriendsFragment() {
        if (userFriendsFragment == null) {
            userFriendsFragment = UserFriendsFragment.newInstance(user);
        }
        return userFriendsFragment;
    }

    public FollowerListFragment getFollowerListFragment() {
        if (followerListFragment == null) {
            followerListFragment = FollowerListFragment.newInstance(user);
        }
        return followerListFragment;
    }

    public UserTweetsListFragment getUserTweetsListFragment() {
        if (userTweetsListFragment == null) {
            userTweetsListFragment = UserTweetsListFragment.newInstance(user);
            userTweetsListFragment.setFm(fragmentManager);
        }
        return userTweetsListFragment;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 2:
                return getUserFriendsFragment();
            case 1:
                return getFollowerListFragment();
            case 0:
                return getUserTweetsListFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}