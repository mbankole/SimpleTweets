package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.User;

/**
 * Created by mbankole on 7/5/17.
 */

public class UserInfoFragment extends Fragment{

    TextView tvTweetsCount;
    TextView tvFollowersCount;
    TextView tvUserDescription;
    User user;

    public UserInfoFragment() {}

    public static UserInfoFragment newInstance(User user) {
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        UserInfoFragment fragment = new UserInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tvTweetsCount = (TextView)view.findViewById(R.id.tvTweetCount);
        tvFollowersCount = (TextView)view.findViewById(R.id.tvFollowersCount);
        tvUserDescription = (TextView)view.findViewById(R.id.tvUserDescription);

        user = getArguments().getParcelable("user");
        tvTweetsCount.setText(String.valueOf(user.statusesCount));
        tvFollowersCount.setText(String.valueOf(user.followerCount));
        tvUserDescription.setText(user.description);
    }
}
