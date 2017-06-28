package com.codepath.apps.restclienttemplate.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mbankole on 6/26/17.
 */

public class Tweet implements Parcelable{
    public String body;
    public long uid;
    public String createdAt;
    public User user;
    public int reTweetCount;
    public String inReplyToScreenname;
    public boolean favorited;
    // deserialze data

    public Tweet() {};

    public static Tweet fromJSON(JSONObject obj) throws JSONException {
        Tweet tweet = new Tweet();
        //extract data
        tweet.body = obj.getString("text");
        tweet.uid = obj.getLong("id");
        tweet.createdAt = obj.getString("created_at");
        tweet.reTweetCount = obj.getInt("retweet_count");
        tweet.inReplyToScreenname = obj.getString("in_reply_to_screen_name");
        tweet.favorited = obj.getBoolean("favorited");
        tweet.user = User.fromJSON(obj.getJSONObject("user"));
        return tweet;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "body='" + body + '\'' +
                ", uid=" + uid +
                ", createdAt='" + createdAt + '\'' +
                ", user=" + user +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.body);
        dest.writeLong(this.uid);
        dest.writeString(this.createdAt);
        dest.writeParcelable(this.user, flags);
        dest.writeInt(this.reTweetCount);
        dest.writeString(this.inReplyToScreenname);
        dest.writeByte(this.favorited ? (byte) 1 : (byte) 0);
    }

    protected Tweet(Parcel in) {
        this.body = in.readString();
        this.uid = in.readLong();
        this.createdAt = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.reTweetCount = in.readInt();
        this.inReplyToScreenname = in.readString();
        this.favorited = in.readByte() != 0;
    }

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
