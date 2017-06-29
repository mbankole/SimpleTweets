package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by mbankole on 6/26/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{

    private List<Tweet> mTweets;
    Context context;
    ViewGroup mParent;
    FragmentManager fm;

    String TAG = "TweetAdapter";
    // tweets array into the constructor
    public TweetAdapter (List<Tweet> tweets) {
        mTweets = tweets;
    }
    //for each row, inflate then pass into the ViewHolder Class

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        mParent = parent;
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView, context);
        return viewHolder;
    }

    //Bind the values based on the element position


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //get data according to position

        Tweet tweet = mTweets.get(position);

        //populate the views with the data

        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        String screenName = "@" + tweet.user.screenName;
        holder.tvScreenName.setText(screenName);
        holder.tvRelTime.setText(getRelativeTimeAgo(tweet.createdAt));
        if (tweet.favorited) holder.btFavorite.setBackground(context.getResources().getDrawable(R.drawable.ic_vector_heart));
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 5, 0))
                .into(holder.ivProfileImage);
    }

    //create Viewholder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvScreenName;
        public TextView tvRelTime;
        public Button btReply;
        public Button btFavorite;
        public Button btReTweet;
        private TwitterClient client;

        public ViewHolder(View itemView, Context con) {
            super(itemView);

            //perform the lookups

            ivProfileImage = (ImageView)itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView)itemView.findViewById(R.id.tvUserName);
            tvScreenName = (TextView)itemView.findViewById(R.id.tvScreenName);
            tvBody = (TextView)itemView.findViewById(R.id.tvBody);
            tvRelTime = (TextView)itemView.findViewById(R.id.tvRelTime);
            btReply = (Button)itemView.findViewById(R.id.btReply);
            btFavorite = (Button)itemView.findViewById(R.id.btFavorite);
            btReTweet = (Button)itemView.findViewById(R.id.btReTweet);
            itemView.setOnClickListener(this);
            btReply.setOnClickListener(this);
            btFavorite.setOnClickListener(this);
            btReTweet.setOnClickListener(this);
            client = TwitterApp.getRestClient();

        }
        //adds click listener for the items
        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                final Tweet tweet = mTweets.get(position);
                if (v.getId() == btFavorite.getId()) {
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

                } else if (v.getId() == btReTweet.getId()) {
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

                } else if (v.getId() == btReply.getId()) {
                    debug("clicked reply button");
                    ComposeTweetDialogFragment composeTweetDialogFragment =
                            ComposeTweetDialogFragment.newInstance("Replying to " + tweet.user.name, tweet.user.screenName, tweet.uid);
                    composeTweetDialogFragment.show(fm, "fragment_edit_name");
                    return;
                } else {
                    Toast.makeText(context, "DETAIL", Toast.LENGTH_LONG).show();
                    // make sure the position is valid, i.e. actually exists in the view
                    // get the movie at the position, this won't work if the class is static
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
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
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

        return relativeDate;
    }


    public void debug(String message) {
        log.d(TAG, message);
    }

    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }
}
