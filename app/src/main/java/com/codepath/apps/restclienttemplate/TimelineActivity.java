package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private String TAG = "TimelineActivityStuff";
    private TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    private SwipeRefreshLayout swipeContainer;




    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        debug("loaded up");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        client = TwitterApp.getRestClient();
        //find the recyclerview
        rvTweets = (RecyclerView)findViewById(R.id.rvTweet);
        //init the arraylist
        tweets = new ArrayList<>();
        //construct the adapter
        tweetAdapter = new TweetAdapter(tweets);
        //recyclerview setup
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(tweetAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvTweets.getContext(),
                Configuration.ORIENTATION_PORTRAIT);
        rvTweets.addItemDecoration(dividerItemDecoration);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                tweetAdapter.clear();
                populateTimeline();
            }
        });

        /*
        rvTweets.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public void onTouchEvent(RecyclerView recycler, MotionEvent event) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (v.getId() == btReply.getId()) {
                        debug("clicked reply button");
                        Toast.makeText(context, "REPLY", Toast.LENGTH_LONG).show();
                        Tweet tweet = mTweets.get(position);
                        Intent intent = new Intent(context, ComposeActivity.class);
                        intent.putExtra("reply", tweet.user.screenName);
                        context.startActivity(intent);
                        return;
                    } else {
                        Toast.makeText(context, "DETAIL", Toast.LENGTH_LONG).show();
                        // make sure the position is valid, i.e. actually exists in the view
                        // get the movie at the position, this won't work if the class is static
                        Tweet tweet = mTweets.get(position);
                        // create intent for the new activity
                        Intent intent = new Intent(context, TweetDetailActivity.class);
                        // serialize the movie using parceler, use its short name as a key
                        intent.putExtra("tweet", tweet);
                        intent.putExtra("user", tweet.user);
                        // show the activity
                        context.startActivity(intent);
                    }
                }
            }
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                return;
            }

            @Override
            public boolean onInterceptTouchEvent(RecyclerView recycler, MotionEvent event) {
                return false;
            }
        });*/

        populateTimeline();
    }

    private void populateTimeline() {
        client.getHomeTimeline( new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                log.d("TwitterClient", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                log.d("TwitterClient", response.toString());
                for (int i = 0; i < response.length(); i++) {
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
                        swipeContainer.setRefreshing(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onComposeAction(MenuItem mi) {
        Toast.makeText(this, "COMPOSE", Toast.LENGTH_LONG).show();
        Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
        startActivityForResult(i, REQUEST_CODE);
    }

    public void onProfileAction(MenuItem mi) {
        Toast.makeText(this, "PROFILE", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Tweet nTweet = (Tweet)data.getParcelableExtra("tweet");
            nTweet.user = (User)data.getParcelableExtra("user");
            debug("got tweet " + nTweet.toString());
            tweets.add(0, nTweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
    }

    private void sendTweet(String message) {
        client.sendTweet( message, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                debug("first on success");
                try {
                    Tweet tweet = Tweet.fromJSON(response);
                    tweets.add(0, tweet);
                    tweetAdapter.notifyItemInserted(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                debug("second on success");
                for (int i = 0; i < response.length(); i++) {
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                debug("error 1");
                log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                debug("error 2");
                log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                debug("error 3");
                log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }

    public void debug(String message) {
        log.d(TAG, message);
    }
}
