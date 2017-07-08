package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.codepath.apps.restclienttemplate.models.User;

public class SearchActivity extends AppCompatActivity {

    private TwitterClient client;
    User user;
    String query;
    SearchFragmentPagerAdapter fragmentPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        query = getIntent().getStringExtra("query");

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        fragmentPager = new SearchFragmentPagerAdapter(getSupportFragmentManager(),
                SearchActivity.this, query);
        viewPager.setAdapter(fragmentPager);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle('"' + query + '"');
        client = TwitterApp.getRestClient();
    }
}
