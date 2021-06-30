package com.codepath.apps.restclienttemplate.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetailActivity extends AppCompatActivity {

    public static final String TAG = "TweetDetailActivity";
    ActivityTweetDetailBinding binding;

    Tweet tweet;
    Tweet reply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTweetDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.viewTweetReply.setVisibility(View.GONE);

        List<Tweet> tweet_and_reply = (List<Tweet>) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        tweet = tweet_and_reply.get(0);
        if (tweet_and_reply.size() > 1) {
            reply = tweet_and_reply.get(1);
        }
        setUpDetailsView();
    }

    private void setUpDetailsView() {
        String bodyText = tweet.getBody();
        if (tweet.getBodyUrl().length() > 1) {
            bodyText += "\n" + tweet.getBodyUrl();
        }
        binding.tvBody.setText(bodyText);
        binding.tvName.setText(tweet.getUser().getName());
        binding.tvScreenName.setText("@" + tweet.getUser().getScreenName());
        binding.tvTimeStamp.setText("· " + tweet.getRelativeTimeAgo());
        binding.tvReplyCount.setText(String.valueOf(tweet.getReplyCount()));
        binding.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        binding.tvHeartCount.setText(String.valueOf(tweet.getHeartCount()));
        Glide.with(this).load(tweet.getUser().getProfileImageUrl())
                .transform(new RoundedCornersTransformation(30,0))
                .into(binding.ivProfileImage);
        if (tweet.getPhotoUrls().size() > 0) {
            binding.ivEmbedPhoto.setVisibility(View.VISIBLE);
            Log.i(TAG,tweet.getPhotoUrls().get(0));
            Glide.with(this).load(tweet.getPhotoUrls().get(0)).transform(new RoundedCornersTransformation(30,0))
                    .into(binding.ivEmbedPhoto);
        }
        else {
            binding.ivEmbedPhoto.setVisibility(View.GONE);
        }
        updateRetweetedOrHeartedIcons();
        binding.ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Wants to reply");
            }
        });

        binding.ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tweet.userRetweeted = !tweet.userRetweeted;
                updateRetweetedOrHeartedIcons();
            }
        });

        binding.ivHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tweet.userHearted = !tweet.userHearted;
                updateRetweetedOrHeartedIcons();
            }
        });
    }

    private void updateRetweetedOrHeartedIcons() {
        if (tweet.isUserRetweeted() == true) {
            Glide.with(this).load(R.drawable.ic_vector_retweet).into(binding.ivRetweet);
            binding.ivRetweet.setColorFilter(ContextCompat.getColor(this, R.color.medium_green), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        else {
            Glide.with(this).load(R.drawable.ic_vector_retweet_stroke).into(binding.ivRetweet);
            binding.ivRetweet.setColorFilter(ContextCompat.getColor(this, R.color.inline_action), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        if (tweet.isUserHearted() == true) {
            Glide.with(this).load(R.drawable.ic_vector_heart).into(binding.ivHeart);
            binding.ivHeart.setColorFilter(ContextCompat.getColor(this, R.color.medium_red), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        else {
            Glide.with(this).load(R.drawable.ic_vector_heart_stroke).into(binding.ivHeart);
            binding.ivHeart.setColorFilter(ContextCompat.getColor(this, R.color.inline_action), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        if (reply != null) {
            if (reply.isUserRetweeted() == true) {
                Glide.with(this).load(R.drawable.ic_vector_retweet).into(binding.ivRetweet);
                binding.ivRetweet1.setColorFilter(ContextCompat.getColor(this, R.color.medium_green), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                Glide.with(this).
                load(R.drawable.ic_vector_retweet_stroke).into(binding.ivRetweet);
                binding.ivRetweet1.setColorFilter(ContextCompat.getColor(this, R.color.inline_action), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            if (reply.isUserHearted() == true) {
                Glide.with(this).load(R.drawable.ic_vector_heart).into(binding.ivHeart);
                binding.ivHeart1.setColorFilter(ContextCompat.getColor(this, R.color.medium_red), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                Glide.with(this).load(R.drawable.ic_vector_heart_stroke).into(binding.ivHeart);
                binding.ivHeart1.setColorFilter(ContextCompat.getColor(this, R.color.inline_action), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }
    }
}