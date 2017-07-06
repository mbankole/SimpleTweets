package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.User;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.loopj.android.http.AsyncHttpClient.log;

public class UserPageActivity extends AppCompatActivity {


    private TwitterClient client;
    private String TAG = "UserPageActivity";
    FragmentManager fm;
    TextView tvUserName;
    TextView tvScreenName;
    ImageView ivProfileImage;
    ImageView ivProfileBanner;
    ImageView ivVerified;
    UserFragmentPagerAdapter fragmentPager;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        user = getIntent().getParcelableExtra("user");

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        fragmentPager = new UserFragmentPagerAdapter(getSupportFragmentManager(),
                UserPageActivity.this, user);
        viewPager.setAdapter(fragmentPager);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        client = TwitterApp.getRestClient();
        //find the recyclerview

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);


        tvUserName = (TextView)findViewById(R.id.tvUserName);
        tvScreenName = (TextView)findViewById(R.id.tvScreenName);
        ivProfileImage = (ImageView)findViewById(R.id.ivProfileImage);
        ivProfileBanner = (ImageView)findViewById(R.id.ivProfileBanner);
        ivVerified = (ImageView)findViewById(R.id.ivVerified);

        tvUserName.setText(user.name);
        tvScreenName.setText("@" + user.screenName);

        if (user.verified) ivVerified.setVisibility(View.VISIBLE);
        else ivVerified.setVisibility(View.GONE);

        Glide.with(this)
                .load(user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(this, 25, 0))
                .into(ivProfileImage);
        Glide.with(this)
                .load(user.profileBannerUrl)
                //.bitmapTransform(new RoundedCornersTransformation(this, 5, 0))
                .into(ivProfileBanner);

        if (user.profileBannerUrl == null) {
            ivProfileBanner.setVisibility(View.GONE);
        }
        else {
            ivProfileBanner.setVisibility(View.VISIBLE);
        }
    }


    public void debug(String message) {
        log.d(TAG, message);
    }

}
