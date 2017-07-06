package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetDialogFragment.ComposeTweetDialogListener{

    private TwitterClient client;
    private String TAG = "TimelineActivityStuff";
    TweetFragmentPagerAdapter fragmentPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        debug("loaded up");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        fragmentPager = new TweetFragmentPagerAdapter(getSupportFragmentManager(),
                TimelineActivity.this);
        viewPager.setAdapter(fragmentPager);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        client = TwitterApp.getRestClient();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                Intent i = new Intent(TimelineActivity.this, SearchActivity.class);
                i.putExtra("query", query);
                getApplicationContext().startActivity(i);
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    public void onComposeAction(MenuItem mi) {
        showEditDialog();
    }

    public void onProfileAction(MenuItem mi) {
        Toast.makeText(this, "PROFILE", Toast.LENGTH_LONG).show();
        showUser();
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetDialogFragment composeTweetDialogFragment = ComposeTweetDialogFragment.newInstance("Compose New Tweet");
        composeTweetDialogFragment.show(fm, "fragment_edit_name");
    }

    private  void showUser() {
        //swipeContainer.setRefreshing(true);
        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                log.d("TwitterClient", response.toString());
                try {
                    User user = User.fromJSON(response);
                    //swipeContainer.setRefreshing(false);
                    Intent i = new Intent(TimelineActivity.this, UserPageActivity.class);
                    i.putExtra("user", user);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                    //swipeContainer.setRefreshing(false);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                log.d("TwitterClient", response.toString());
                //swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                log.d("TwitterClient", responseString);
                throwable.printStackTrace();
                //swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
                //swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
                //swipeContainer.setRefreshing(false);
            }
        });
    }

    public void debug(String message) {
        log.d(TAG, message);
    }

    @Override
    public void onFinishEditTweet(Tweet tweet) {
        debug("got tweet " + tweet.toString());
        fragmentPager.tweetListFragment.addTweet(tweet);
        //tweets.add(0, tweet);
        //tweetAdapter.notifyItemInserted(0);
        //rvTweets.smoothScrollToPosition(0);
    }
}
