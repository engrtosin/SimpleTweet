package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.activities.MediaListAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        // get the data
        Tweet tweet = tweets.get(position);
        // bind the data
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    // pass in the context and the list of tweets

    // for each row inflate the layout of the tweet

    // bind values based on the position of the element

    // define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {

        public static final String TAG = "TweetsAdapter";
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvName;
        TextView tvScreenName;
        TextView tvTimeStamp;
        ImageView ivEmbedPhoto;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvName = itemView.findViewById(R.id.tvName);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            ivEmbedPhoto = itemView.findViewById(R.id.ivEmbedPhoto);
            ivEmbedPhoto.setVisibility(View.VISIBLE);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.getBody());
            tvName.setText(tweet.getUser().getName());
            tvScreenName.setText("@" + tweet.getUser().getScreenName());
            tvTimeStamp.setText("· " + tweet.getRelativeTimeAgo());
            Glide.with(context).load(tweet.getUser().getProfileImageUrl())
                    .transform(new RoundedCornersTransformation(30,0))
                    .into(ivProfileImage);
            if (tweet.getPhotoUrls().size() > 0) {
                ivEmbedPhoto.setVisibility(View.VISIBLE);
                Log.i(TAG,tweet.getPhotoUrls().get(0));
                Glide.with(context).load(tweet.getPhotoUrls().get(0)).transform(new RoundedCornersTransformation(30,0))
                        .into(ivEmbedPhoto);
            }
            else {
                ivEmbedPhoto.setVisibility(View.GONE);
            }
        }
    }
}
