package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

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
    public boolean connected;
    TweetFragmentPagerAdapter fragmentPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        debug("loaded up");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            log.d("NETTEST", "has connection");
        }
        else {
            connected = false;
            noInternetSnackBar();
            log.d("NETTEST", "no connection");
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        fragmentPager = new TweetFragmentPagerAdapter(getSupportFragmentManager(),
                TimelineActivity.this, connected);
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
                if (connected) {
                    Intent i = new Intent(TimelineActivity.this, SearchActivity.class);
                    i.putExtra("query", query);
                    getApplicationContext().startActivity(i);
                    // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                    // see https://code.google.com/p/android/issues/detail?id=24599
                    searchView.clearFocus();
                }
                else {
                    noInternetSnackBar();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));

        return true;
    }

    public void onComposeAction(MenuItem mi) {
        if (connected) showEditDialog();
        else noInternetSnackBar();
    }

    public void onProfileAction(MenuItem mi) {
        if (connected) showUser();
        else noInternetSnackBar();
    }

    private void showEditDialog() {
        if (connected) {
            FragmentManager fm = getSupportFragmentManager();
            ComposeTweetDialogFragment composeTweetDialogFragment = ComposeTweetDialogFragment.newInstance("Compose New Tweet");
            composeTweetDialogFragment.show(fm, "fragment_edit_name");
        }
        else noInternetSnackBar();
    }

    private  void showUser() {
        //swipeContainer.setRefreshing(true);
        if (connected) {
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
        else noInternetSnackBar();
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

    public void noInternetSnackBar() {
        Snackbar.make(findViewById(R.id.viewpager), "No Internet", Snackbar.LENGTH_LONG).show();
    }
}
