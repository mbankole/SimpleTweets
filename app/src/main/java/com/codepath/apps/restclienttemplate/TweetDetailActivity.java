package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.loopj.android.http.AsyncHttpClient.log;

public class TweetDetailActivity extends AppCompatActivity {

    TextView tvUserName;
    TextView tvScreenName;
    TextView tvBody;
    ImageView ivProfileImage;
    Button btFavorite;
    Button btReTweet;
    Context context;
    Tweet tweet;
    private TwitterClient client;
    static String TAG = "TweetDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        tweet = getIntent().getParcelableExtra("tweet");
        tweet.user = getIntent().getParcelableExtra("user");
        client = TwitterApp.getRestClient();
        context = this;
        tvUserName = (TextView)findViewById(R.id.tvUserName);
        tvScreenName = (TextView)findViewById(R.id.tvScreenName);
        tvBody = (TextView)findViewById(R.id.tvBody);
        ivProfileImage = (ImageView)findViewById(R.id.ivProfileImage);
        btFavorite = (Button)findViewById(R.id.btFavorite);
        btReTweet = (Button)findViewById(R.id.btReTweet);

        if (tweet.favorited) btFavorite.setBackground(context.getResources().getDrawable(R.drawable.ic_vector_heart));

        tvUserName.setText(tweet.user.name);
        String screenName = "@" + tweet.user.screenName;
        tvScreenName.setText(screenName);
        tvBody.setText(tweet.body);
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 5, 0))
                .into(ivProfileImage);

        btFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Favorite", Snackbar.LENGTH_LONG).show();
                if (!tweet.favorited)  {
                    client.favoriteTweet( tweet.uid,  new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            debug("first on success");
                            btFavorite.setBackground(context.getResources().getDrawable(R.drawable.ic_vector_heart));
                            tweet.favorited = true;
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            debug("second on success");
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
                else  {
                    client.unFavoriteTweet( tweet.uid,  new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            debug("first on success");
                            btFavorite.setBackground(context.getResources().getDrawable(R.drawable.ic_vector_heart_stroke));
                            tweet.favorited = false;
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            debug("second on success");
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
            }
        });

        btReTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Retweet", Snackbar.LENGTH_LONG).show();
                client.reTweet( tweet.uid,  new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        debug("first on success");
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        debug("second on success");
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
        });

    }

    public void debug(String message) {
        log.d(TAG, message);
    }
}
