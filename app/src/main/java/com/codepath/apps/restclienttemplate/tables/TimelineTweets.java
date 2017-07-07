package com.codepath.apps.restclienttemplate.tables;

import com.codepath.apps.restclienttemplate.MyDatabase;
import com.codepath.apps.restclienttemplate.models.User;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

/**
 * Created by mbankole on 7/6/17.
 */

@Table(database = MyDatabase.class)
@Parcel(analyze={User.class})
public class TimelineTweets extends BaseModel {

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
