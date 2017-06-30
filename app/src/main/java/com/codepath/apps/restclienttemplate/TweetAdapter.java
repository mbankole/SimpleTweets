package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    SwipeRefreshLayout swipeContainer;


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
        //log.d("TWEETIMAGE", tweet.tweetImageUrl);
        holder.tvBody.setText(tweet.body);
        String screenName = "@" + tweet.user.screenName;
        holder.tvScreenName.setText(screenName);
        holder.tvRelTime.setText(getRelativeTimeAgo(tweet.createdAt));
        holder.tvReTweets.setText(String.valueOf(tweet.reTweetCount));
        holder.tvFavorites.setText(String.valueOf(tweet.favoritesCount));
        if (tweet.favorited) holder.btFavorite.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.favorited)));
        else holder.btFavorite.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.unfavorited)));
        if (tweet.reTweeted) holder.btReTweet.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.reTweeted)));
        else holder.btReTweet.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.unReTweeted)));
        if (!tweet.isReply) {
            holder.tvReplying.setVisibility(View.VISIBLE);
            holder.tvReplyToScreenName.setVisibility(View.VISIBLE);
            holder.tvReplyToScreenName.setText(tweet.inReplyToScreenname);
        } else {
            holder.tvReplying.setVisibility(View.GONE);
            holder.tvReplyToScreenName.setVisibility(View.GONE);
        }
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 5, 0))
                .into(holder.ivProfileImage);
        if (tweet.tweetImageUrl != null) {
            holder.ivTweetImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(tweet.tweetImageUrl)
                    .fitCenter()
                    .dontAnimate()
                    .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
                    //.override(600,600)
                    //.fitCenter()
                    .into(holder.ivTweetImage);
        }
        else {
            holder.ivTweetImage.setVisibility(View.GONE);
        }
    }

    //create Viewholder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView ivProfileImage;
        public TextView tvUsername;
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
        private TwitterClient client;

        public ViewHolder(View itemView, Context con) {
            super(itemView);

            //perform the lookups

            ivProfileImage = (ImageView)itemView.findViewById(R.id.ivProfileImage);
            ivTweetImage = (ImageView)itemView.findViewById(R.id.ivTweetImage);
            tvUsername = (TextView)itemView.findViewById(R.id.tvUserName);
            tvScreenName = (TextView)itemView.findViewById(R.id.tvScreenName);
            tvBody = (TextView)itemView.findViewById(R.id.tvBody);
            tvRelTime = (TextView)itemView.findViewById(R.id.tvRelTime);
            tvFavorites = (TextView)itemView.findViewById(R.id.tvFavorites);
            tvReTweets = (TextView)itemView.findViewById(R.id.tvReTweets);
            tvReplying = (TextView)itemView.findViewById(R.id.tvReplying);
            tvReplyToScreenName = (TextView)itemView.findViewById(R.id.tvReplyingScreenName);
            btReply = (Button)itemView.findViewById(R.id.btReply);
            btFavorite = (Button)itemView.findViewById(R.id.btFavorite);
            btReTweet = (Button)itemView.findViewById(R.id.btReTweet);
            itemView.setOnClickListener(this);
            btReply.setOnClickListener(this);
            btFavorite.setOnClickListener(this);
            btReTweet.setOnClickListener(this);
            ivTweetImage.setOnClickListener(this);
            client = TwitterApp.getRestClient();

        }
        //adds click listener for the items
        @Override
        public void onClick(View v) {
            // gets item position
            //final View view = v;
            final int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                final Tweet tweet = mTweets.get(position);
                if (v.getId() == btFavorite.getId()) {
                    swipeContainer.setRefreshing(true);
                    if (!tweet.favorited)  {
                        client.favoriteTweet( tweet.uid,  new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                debug("first on success");
                                //Snackbar.make(view, "Favorite", Snackbar.LENGTH_LONG).show();
                                btFavorite.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.favorited)));
                                tweet.favorited = true;
                                tweet.favoritesCount += 1;
                                notifyItemChanged(position);
                                swipeContainer.setRefreshing(false);
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
                                notifyItemChanged(position);
                                swipeContainer.setRefreshing(false);
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
                    notifyItemChanged(position);

                } else if (v.getId() == btReTweet.getId()) {
                    swipeContainer.setRefreshing(true);
                    if (!tweet.reTweeted) {
                        client.reTweet( tweet.uid,  new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                debug("first on success");
                                //Snackbar.make(view, "Retweeted", Snackbar.LENGTH_LONG).show();
                                btReTweet.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.reTweeted)));
                                tweet.reTweeted = true;
                                tweet.reTweetCount += 1;
                                notifyItemChanged(position);
                                swipeContainer.setRefreshing(false);
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
                                notifyItemChanged(position);
                                swipeContainer.setRefreshing(false);
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
                    notifyItemChanged(position);
                } else if (v.getId() == btReply.getId()) {
                    debug("clicked reply button");
                    ComposeTweetDialogFragment composeTweetDialogFragment =
                            ComposeTweetDialogFragment.newInstance("Replying to " + tweet.user.name, tweet.user.screenName, tweet.uid);
                    composeTweetDialogFragment.show(fm, "fragment_edit_name");
                    return;
                } else {
                    // make sure the position is valid, i.e. actually exists in the view
                    // get the movie at the position, this won't work if the class is static
                    // create intent for the new activity
                    TweetDetailDialogFragment tweetDetailDialogFragment =
                            TweetDetailDialogFragment.newInstance(tweet);
                    tweetDetailDialogFragment.fm = fm;
                    tweetDetailDialogFragment.viewHolder = this;
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
        relativeDate = relativeDate.replace(" sec.", "s");
        relativeDate = relativeDate.replace(" min.", "m");
        relativeDate = relativeDate.replace(" hr.", "h");
        relativeDate = relativeDate.replace(" ago", "");
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
