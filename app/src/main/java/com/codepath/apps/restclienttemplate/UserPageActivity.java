package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.User;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class UserPageActivity extends AppCompatActivity {

    TextView tvUserName;
    TextView tvScreenName;
    ImageView ivProfileImage;
    ImageView ivProfileBanner;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        user = getIntent().getParcelableExtra("user");

        tvUserName = (TextView)findViewById(R.id.tvUserName);
        tvScreenName = (TextView)findViewById(R.id.tvScreenName);
        ivProfileImage = (ImageView)findViewById(R.id.ivProfileImage);
        ivProfileBanner = (ImageView)findViewById(R.id.ivProfileBanner);

        tvUserName.setText(user.name);
        tvScreenName.setText(user.screenName);

        Glide.with(this)
                .load(user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(this, 5, 0))
                .into(ivProfileImage);
        Glide.with(this)
                .load(user.profileBannerUrl)
                .bitmapTransform(new RoundedCornersTransformation(this, 5, 0))
                .into(ivProfileBanner);
    }
}
