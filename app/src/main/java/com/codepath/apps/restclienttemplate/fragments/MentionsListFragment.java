package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetAdapter;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by mbankole on 7/3/17.
 */

public class MentionsListFragment extends Fragment {
    private TwitterClient client;
    private String TAG = "TimelineActivityStuff";
    private TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    private SwipeRefreshLayout swipeContainer;
    FragmentManager fm;

    public MentionsListFragment() {}

    public static TweetListFragment newInstance() {
        TweetListFragment frag = new TweetListFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    public FragmentManager getFm() {
        return fm;
    }

    public void setFm(FragmentManager fm) {
        this.fm = fm;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweet_list, container, false);
        client = TwitterApp.getRestClient();
        //find the recyclerview
        rvTweets = (RecyclerView)v.findViewById(R.id.rvTweet);
        //init the arraylist
        tweets = new ArrayList<>();
        //construct the adapter
        tweetAdapter = new TweetAdapter(tweets);
        tweetAdapter.setFm(fm);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        debug("loaded up");
        super.onCreate(savedInstanceState);
        final Context context = getContext();

        //recyclerview setup
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(tweetAdapter);
        EndlessRecyclerViewScrollListener scroller = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                //Toast.makeText(context, "WHAT MORE", Toast.LENGTH_LONG).show();
                //loadMoreTimeline(totalItemsCount - 1);
            }
        };
        rvTweets.addOnScrollListener(scroller);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvTweets.getContext(),
                Configuration.ORIENTATION_PORTRAIT);
        rvTweets.addItemDecoration(dividerItemDecoration);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
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

        //jank shit
        tweetAdapter.setSwipeContainer(swipeContainer);

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        populateTimeline();
    }

    private void loadMoreTimeline(int lastIndex) {
        long lastId = tweets.get(lastIndex).getUid();
        swipeContainer.setRefreshing(true);
        debug(String.valueOf(lastId));
        client.getMentionsBefore(lastId, new JsonHttpResponseHandler() {
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


    private void populateTimeline() {
        swipeContainer.setRefreshing(true);

        client.getMentions( new JsonHttpResponseHandler() {
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

    public void debug(String message) {
        log.d(TAG, message);
    }


    public void addTweet(Tweet tweet) {
        debug("got tweet " + tweet.toString());
        tweets.add(0, tweet);
        tweetAdapter.notifyItemInserted(0);
        rvTweets.smoothScrollToPosition(0);
    }
}
