package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by mbankole on 6/29/17.
 */

public class TweetDetailDialogFragment extends DialogFragment implements View.OnClickListener{
    public ImageView ivProfileImage;
    public TextView tvUserName;
    public TextView tvBody;
    public TextView tvScreenName;
    public TextView tvRelTime;
    public TextView tvFavorites;
    public TextView tvReTweets;
    public TextView tvReplying;
    public TextView tvReplyToScreenName;
    public Button btReply;
    public Button btFavorite;
    public Button btReTweet;
    public ImageView ivTweetImage;
    TweetAdapter.ViewHolder viewHolder;
    FragmentManager fm;
    Context context;
    Tweet tweet;
    private TwitterClient client;
    static String TAG = "TweetDetailActivityFragment";

    public TweetDetailDialogFragment() {
        //empty constructor
    }

    public static TweetDetailDialogFragment newInstance(Tweet tweet) {
        TweetDetailDialogFragment frag = new TweetDetailDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("tweet", tweet);
        args.putParcelable("user", tweet.user);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tweet_detail, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        client = TwitterApp.getRestClient();
        tweet = getArguments().getParcelable("tweet");
        tweet.user = getArguments().getParcelable("user");
        client = TwitterApp.getRestClient();
        context = view.getContext();
        tvUserName = (TextView)view.findViewById(R.id.tvUserName);
        tvScreenName = (TextView)view.findViewById(R.id.tvScreenName);
        tvRelTime = (TextView)view.findViewById(R.id.tvRelTime);
        tvFavorites = (TextView)view.findViewById(R.id.tvFavorites);
        tvReTweets = (TextView)view.findViewById(R.id.tvReTweets);
        tvReplying = (TextView)view.findViewById(R.id.tvReplying);
        tvReplyToScreenName = (TextView)view.findViewById(R.id.tvReplyingScreenName);
        tvBody = (TextView)view.findViewById(R.id.tvBody);
        ivProfileImage = (ImageView)view.findViewById(R.id.ivProfileImage);
        btFavorite = (Button)view.findViewById(R.id.btFavorite);
        btReTweet = (Button)view.findViewById(R.id.btReTweet);
        btReply = (Button)view.findViewById(R.id.btReply);
        ivTweetImage = (ImageView)view.findViewById(R.id.ivTweetImage);
        btReply.setOnClickListener(this);
        btFavorite.setOnClickListener(this);
        btReTweet.setOnClickListener(this);
        ivTweetImage.setOnClickListener(this);
        client = TwitterApp.getRestClient();

        tvUserName.setText(tweet.user.name);
        log.d("TWEETIMAGEDETAIL", tweet.tweetImageUrl);
        tvBody.setText(tweet.body);
        String screenName = "@" + tweet.user.screenName;
        tvScreenName.setText(screenName);
        tvRelTime.setText(getRelativeTimeAgo(tweet.createdAt));
        tvReTweets.setText(String.valueOf(tweet.reTweetCount));
        tvFavorites.setText(String.valueOf(tweet.favoritesCount));
        if (tweet.favorited) btFavorite.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.favorited)));
        else btFavorite.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.unfavorited)));
        if (tweet.reTweeted) btReTweet.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.reTweeted)));
        else btReTweet.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.unReTweeted)));
        if (!tweet.isReply) {
            tvReplying.setVisibility(View.VISIBLE);
            tvReplyToScreenName.setVisibility(View.VISIBLE);
            tvReplyToScreenName.setText(tweet.inReplyToScreenname);
        } else {
            tvReplying.setVisibility(View.GONE);
            tvReplyToScreenName.setVisibility(View.GONE);
        }
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 5, 0))
                .into(ivProfileImage);
        if (tweet.tweetImageUrl != null) {
            ivTweetImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(tweet.tweetImageUrl)
                    .fitCenter()
                    .dontAnimate()
                    .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
                    //.override(600,600)
                    //.fitCenter()
                    .into(ivTweetImage);
        }
        else {
            ivTweetImage.setVisibility(View.GONE);
        }
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS ,
                    DateUtils.FORMAT_ABBREV_ALL).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        relativeDate = relativeDate.replace(" sec.", "s");
        relativeDate = relativeDate.replace(" min.", "m");
        relativeDate = relativeDate.replace(" hr.", "h");
        return relativeDate;
    }
    public void debug(String message) {
        log.d(TAG, message);
    }

    @Override
    public void onClick(View v) {
        // gets item position
        //final View view = v;
            final Tweet mTweet = tweet;
            if (v.getId() == btFavorite.getId()) {
                //swipeContainer.setRefreshing(true);
                if (!tweet.favorited)  {
                    client.favoriteTweet( tweet.uid,  new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            debug("first on success");
                            //Snackbar.make(view, "Favorite", Snackbar.LENGTH_LONG).show();
                            btFavorite.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.favorited)));
                            tweet.favorited = true;
                            tweet.favoritesCount += 1;
                            //notifyItemChanged(position);
                            //swipeContainer.setRefreshing(false);
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
                            //Snackbar.make(view, "Un-Favorite", Snackbar.LENGTH_LONG).show();
                            btFavorite.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.unfavorited)));
                            tweet.favorited = false;
                            tweet.favoritesCount -= 1;
                            //notifyItemChanged(position);
                            //swipeContainer.setRefreshing(false);
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
                //viewHolder.notifyItemChanged(position);

            } else if (v.getId() == btReTweet.getId()) {
                //swipeContainer.setRefreshing(true);
                if (!tweet.reTweeted) {
                    client.reTweet( tweet.uid,  new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            debug("first on success");
                            //Snackbar.make(view, "Retweeted", Snackbar.LENGTH_LONG).show();
                            btReTweet.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.reTweeted)));
                            tweet.reTweeted = true;
                            tweet.reTweetCount += 1;
                            //notifyItemChanged(position);
                            //swipeContainer.setRefreshing(false);
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
                else {
                    client.unReTweet( tweet.uid,  new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            debug("first on success");
                            //Snackbar.make(view, "Un-Retweeted", Snackbar.LENGTH_LONG).show();
                            btReTweet.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.unReTweeted)));
                            tweet.reTweeted = false;
                            tweet.reTweetCount -= 1;
                            //notifyItemChanged(position);
                            //swipeContainer.setRefreshing(false);
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
                //notifyItemChanged(position);

            } else if (v.getId() == btReply.getId()) {
                debug("clicked reply button");
                dismiss();
                ComposeTweetDialogFragment composeTweetDialogFragment =
                        ComposeTweetDialogFragment.newInstance("Replying to " + tweet.user.name, tweet.user.screenName, tweet.uid);
                composeTweetDialogFragment.show(fm, "fragment_edit_name");
                return;
            } else {
                Toast.makeText(context, "DETAIL", Toast.LENGTH_LONG).show();
                // make sure the position is valid, i.e. actually exists in the view
                // get the movie at the position, this won't work if the class is static
                // create intent for the new activity
                TweetDetailDialogFragment tweetDetailDialogFragment =
                        TweetDetailDialogFragment.newInstance(tweet);
                tweetDetailDialogFragment.show(fm, "fragment_edit_name");
                return;
                //Intent intent = new Intent(context, TweetDetailActivity.class);
                // serialize the movie using parceler, use its short name as a key
                //intent.putExtra("tweet", tweet);
                //intent.putExtra("user", tweet.user);
                // show the activity
                //context.startActivity(intent);
            }
        }
    }


