package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.User;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by mbankole on 7/3/17.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private List<User> users;
    Context context;
    ViewGroup mParent;
    FragmentManager fm;
    SwipeRefreshLayout swipeContainer;


    String TAG = "TweetAdapter";
    // tweets array into the constructor
    public UserAdapter(List<User> mUsers) {
        users = mUsers;
    }
    //for each row, inflate then pass into the ViewHolder Class

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        mParent = parent;
        LayoutInflater inflater = LayoutInflater.from(context);

        View userView = inflater.inflate(R.layout.item_user, parent, false);
        UserAdapter.ViewHolder viewHolder = new UserAdapter.ViewHolder(userView, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(UserAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        holder.tvUserName.setText(user.name);
        holder.tvScreenName.setText( "@" + user.screenName);
        holder.tvUserDescription.setText(user.description);
        if (user.verified) holder.ivVerified.setVisibility(View.VISIBLE);
        else holder.ivVerified.setVisibility(View.GONE);

        Glide.with(context)
                .load(user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 5, 0))
                .into(holder.ivProfileImage);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivProfileImage;
        ImageView ivVerified;
        TextView tvUserName;
        TextView tvScreenName;
        TextView tvUserDescription;

        public ViewHolder(View itemView, Context con) {
            super(itemView);

            ivProfileImage = (ImageView)itemView.findViewById(R.id.ivProfileImage);
            ivVerified = (ImageView)itemView.findViewById(R.id.ivVerified);
            tvUserName = (TextView)itemView.findViewById(R.id.tvUserName);
            tvScreenName = (TextView)itemView.findViewById(R.id.tvScreenName);
            tvUserDescription = (TextView)itemView.findViewById(R.id.tvUserDescription);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            User user = users.get(position);
            Intent i = new Intent(context, UserPageActivity.class);
            i.putExtra("user", user);
            context.startActivity(i);
        }
    }
        @Override
    public int getItemCount() {
        return users.size();
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }
}
