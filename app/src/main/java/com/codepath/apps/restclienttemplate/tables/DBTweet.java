package com.codepath.apps.restclienttemplate.tables;

import com.codepath.apps.restclienttemplate.MyDatabase;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by mbankole on 7/7/17.
 */

@Table(database = MyDatabase.class)
@Parcel(analyze={User.class})
public class DBTweet extends BaseModel {

    @Column
    String body;

    @Column
    @PrimaryKey
    long uid;

    @Column
    String createdAt;

    @Column
    String user;

    @Column
    int reTweetCount;

    @Column
    int favoritesCount;

    @Column
    String inReplyToScreenname;

    @Column
    String tweetImageUrl;

    @Column
    boolean favorited;

    @Column
    boolean reTweeted;

    @Column
    boolean isReply;

    public static DBTweet fromTweet(Tweet tweet) {
        DBTweet dbtweet = new DBTweet();
        dbtweet.setUid(tweet.uid);
        dbtweet.setBody(tweet.body);
        dbtweet.setCreatedAt(tweet.createdAt);
        dbtweet.setFavorited(tweet.favorited);
        dbtweet.setFavoritesCount(tweet.favoritesCount);
        dbtweet.setInReplyToScreenname(tweet.inReplyToScreenname);
        dbtweet.setReTweetCount(tweet.reTweetCount);
        dbtweet.setReTweeted(tweet.reTweeted);
        dbtweet.setReply(tweet.isReply);
        dbtweet.setUser(tweet.getUser().toString());
        return dbtweet;
    }

    public static Tweet toTweet(DBTweet dbtweet) {
        Tweet tweet = new Tweet();
        tweet.setUid(dbtweet.uid);
        tweet.setBody(dbtweet.body);
        tweet.setCreatedAt(dbtweet.createdAt);
        tweet.setFavorited(dbtweet.favorited);
        tweet.setFavoritesCount(dbtweet.favoritesCount);
        tweet.setInReplyToScreenname(dbtweet.inReplyToScreenname);
        tweet.setReTweetCount(dbtweet.reTweetCount);
        tweet.setReTweeted(dbtweet.reTweeted);
        tweet.setReply(dbtweet.isReply);
        tweet.setUser(null);
        try {
            String userString = dbtweet.getUser();
            JSONObject obj = new JSONObject(dbtweet.getUser());
            tweet.setUser(User.fromJSON(obj));
        }
        catch (JSONException e) {
            log.d("DBTWEET","FAILED TO PARSE USER");
        }
        return tweet;
    }

    @Override
    public String toString() {
        return "DBTweet{" +
                "body='" + body + '\'' +
                ", uid=" + uid +
                ", createdAt='" + createdAt + '\'' +
                ", user='" + user + '\'' +
                ", reTweetCount=" + reTweetCount +
                ", favoritesCount=" + favoritesCount +
                ", inReplyToScreenname='" + inReplyToScreenname + '\'' +
                ", tweetImageUrl='" + tweetImageUrl + '\'' +
                ", favorited=" + favorited +
                ", reTweeted=" + reTweeted +
                ", isReply=" + isReply +
                '}';
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getReTweetCount() {
        return reTweetCount;
    }

    public void setReTweetCount(int reTweetCount) {
        this.reTweetCount = reTweetCount;
    }

    public int getFavoritesCount() {
        return favoritesCount;
    }

    public void setFavoritesCount(int favoritesCount) {
        this.favoritesCount = favoritesCount;
    }

    public String getInReplyToScreenname() {
        return inReplyToScreenname;
    }

    public void setInReplyToScreenname(String inReplyToScreenname) {
        this.inReplyToScreenname = inReplyToScreenname;
    }

    public String getTweetImageUrl() {
        return tweetImageUrl;
    }

    public void setTweetImageUrl(String tweetImageUrl) {
        this.tweetImageUrl = tweetImageUrl;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public boolean isReTweeted() {
        return reTweeted;
    }

    public void setReTweeted(boolean reTweeted) {
        this.reTweeted = reTweeted;
    }

    public boolean isReply() {
        return isReply;
    }

    public void setReply(boolean reply) {
        isReply = reply;
    }
}