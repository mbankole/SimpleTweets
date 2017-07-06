package com.codepath.apps.restclienttemplate.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mbankole on 6/26/17.
 */


public class User implements Parcelable{
    public String name;
    public long uid;
    public String screenName;
    public String profileImageUrl;
    public int followerCount;
    public int statusesCount;
    public String profileSidebarBorderColor;
    public String profileSidebarFillColor;
    public String profileLinkColor;
    public String profileBannerUrl;
    public boolean verified;
    public String profileBackgroundColor;
    public String description;

    //deserializer

    public User() {};


    public static User fromJSON(JSONObject obj) throws JSONException {
        User user = new User();
        //extract the data
        user.name = obj.getString("name");
        user.uid = obj.getLong("id");
        user.screenName = obj.getString("screen_name");
        user.profileImageUrl = obj.optString("profile_image_url", null);
        user.profileBannerUrl = obj.optString("profile_banner_url", null);
        if (user.profileBannerUrl != null) user.profileBannerUrl = user.profileBannerUrl + "/600x200";
        user.followerCount = obj.getInt("followers_count");
        user.statusesCount = obj.getInt("statuses_count");
        user.profileSidebarBorderColor = obj.getString("profile_sidebar_border_color");
        user.profileBackgroundColor = obj.getString("profile_background_color");
        user.profileSidebarFillColor = obj.getString("profile_sidebar_fill_color");
        user.profileLinkColor = obj.getString("profile_link_color");
        user.verified = obj.getBoolean("verified");
        user.description = obj.optString("description", "");

        user.profileImageUrl = user.profileImageUrl.replace("_normal.jpg", "_bigger.jpg");
        return user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.uid);
        dest.writeString(this.screenName);
        dest.writeString(this.profileImageUrl);
        dest.writeInt(this.followerCount);
        dest.writeInt(this.statusesCount);
        dest.writeString(this.profileSidebarBorderColor);
        dest.writeString(this.profileSidebarFillColor);
        dest.writeString(this.profileLinkColor);
        dest.writeString(this.profileBannerUrl);
        dest.writeByte(this.verified ? (byte) 1 : (byte) 0);
        dest.writeString(this.profileBackgroundColor);
        dest.writeString(this.description);
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.uid = in.readLong();
        this.screenName = in.readString();
        this.profileImageUrl = in.readString();
        this.followerCount = in.readInt();
        this.statusesCount = in.readInt();
        this.profileSidebarBorderColor = in.readString();
        this.profileSidebarFillColor = in.readString();
        this.profileLinkColor = in.readString();
        this.profileBannerUrl = in.readString();
        this.verified = in.readByte() != 0;
        this.profileBackgroundColor = in.readString();
        this.description = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
