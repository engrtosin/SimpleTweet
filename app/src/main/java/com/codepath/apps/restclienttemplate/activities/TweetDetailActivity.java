package com.codepath.apps.restclienttemplate.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetailActivity extends AppCompatActivity {

    public static final int LOGOUT_RESULT_CODE = 40;
    public static final String TAG = "TweetDetailActivity";
    ActivityTweetDetailBinding binding;
    TwitterClient client;
    Tweet tweet;
    Tweet reply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTweetDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        Glide.with(this).load(R.drawable.twitter_logout)
                .transform(new RoundedCornersTransformation(40,0))
                .into(binding.btnLogout1);
        binding.btnLogout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        client = TwitterApp.getRestClient(this);
        setContentView(view);

        setSupportActionBar(binding.tbDetails);

        Drawable dr = getResources().getDrawable(R.drawable.ic_launcher_twitter_png);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
// Scale it to 50 x 50
        dr = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(dr);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        binding.viewTweetReply.setVisibility(View.GONE);

        List<Tweet> tweet_and_reply = (List<Tweet>) Parcels.unwrap(getIntent().getParcelableExtra("tweet and reply"));
        tweet = tweet_and_reply.get(0);
        setUpDetailsView();
        if (tweet_and_reply.size() > 1) {
            reply = tweet_and_reply.get(1);
            binding.viewTweetReply.setVisibility(View.VISIBLE);
            setUpReplyView();
        }
    }

//    private void onLogoutButton() {
//        Intent intent = new Intent(this,TimelineActivity.class);
//        Log.i(TAG,"Logout");
//        intent.putExtra("Logout","true");
//        this.startActivity(intent);
//    }

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
        updateRetweetedIcons(false);
        updateHeartedIcons(false);
        binding.ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Wants to reply");
            }
        });

        binding.ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateRetweetedIcons(false);
            }
        });

        binding.ivHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateHeartedIcons(false);
            }
        });
    }

    private void setUpReplyView() {
        String bodyText = reply.getBody();
        if (reply.getBodyUrl().length() > 1) {
            bodyText += "\n" + reply.getBodyUrl();
        }
        binding.tvBody1.setText(bodyText);
        binding.tvName1.setText(reply.getUser().getName());
        binding.tvScreenName1.setText("@" + reply.getUser().getScreenName());
        binding.tvTimeStamp1.setText("· " + reply.getRelativeTimeAgo());
        binding.tvReplyCount1.setText(String.valueOf(reply.getReplyCount()));
        binding.tvRetweetCount1.setText(String.valueOf(reply.getRetweetCount()));
        binding.tvHeartCount1.setText(String.valueOf(reply.getHeartCount()));
        Glide.with(this).load(reply.getUser().getProfileImageUrl())
                .transform(new RoundedCornersTransformation(30,0))
                .into(binding.ivProfileImage1);
        if (reply.getPhotoUrls().size() > 0) {
            binding.ivEmbedPhoto1.setVisibility(View.VISIBLE);
            Log.i(TAG,reply.getPhotoUrls().get(0));
            Glide.with(this).load(reply.getPhotoUrls().get(0)).transform(new RoundedCornersTransformation(30,0))
                    .into(binding.ivEmbedPhoto1);
        }
        else {
            binding.ivEmbedPhoto1.setVisibility(View.GONE);
        }
        updateHeartedIcons(false);
        updateRetweetedIcons(false);
        binding.ivReply1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Wants to reply");
            }
        });

        binding.ivRetweet1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateRetweetedIcons(true);
            }
        });

        binding.ivHeart1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateHeartedIcons(true);
            }
        });
    }

    private void updateRetweetedIcons(boolean changeIcon) {
        if (changeIcon) {
            if (tweet.isUserRetweeted() == false) {
                Glide.with(this).load(R.drawable.ic_vector_retweet).into(binding.ivRetweet);
                binding.ivRetweet.setColorFilter(ContextCompat.getColor(this, R.color.medium_green), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount() + 1));
                tweet.retweetThis(this);
            } else {
                Glide.with(this).load(R.drawable.ic_vector_retweet_stroke).into(binding.ivRetweet);
                binding.ivRetweet.setColorFilter(ContextCompat.getColor(this, R.color.inline_action), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount() - 1));
                tweet.unretweetThis(this);
            }

            if (reply != null) {
                if (reply.isUserRetweeted() == false) {
                    Glide.with(this).load(R.drawable.ic_vector_retweet).into(binding.ivRetweet1);
                    binding.ivRetweet1.setColorFilter(ContextCompat.getColor(this, R.color.medium_green), android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.tvRetweetCount1.setText(String.valueOf(reply.getRetweetCount() + 1));
                    reply.retweetThis(this);
                } else {
                    Glide.with(this).
                            load(R.drawable.ic_vector_retweet_stroke).into(binding.ivRetweet1);
                    binding.ivRetweet1.setColorFilter(ContextCompat.getColor(this, R.color.inline_action), android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.tvRetweetCount1.setText(String.valueOf(reply.getRetweetCount() - 1));
                    reply.unretweetThis(this);
                }
            }
        }
        else {
            if (tweet.isUserRetweeted() == true) {
                Glide.with(this).load(R.drawable.ic_vector_retweet).into(binding.ivRetweet);
                binding.ivRetweet.setColorFilter(ContextCompat.getColor(this, R.color.medium_green), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount() + 1));
                //            tweet.retweetThis(this);
            } else {
                Glide.with(this).load(R.drawable.ic_vector_retweet_stroke).into(binding.ivRetweet);
                binding.ivRetweet.setColorFilter(ContextCompat.getColor(this, R.color.inline_action), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount() + 1));
                //            tweet.retweetThis(this);
            }

            if (reply != null) {
                if (reply.isUserRetweeted() == true) {
                    Glide.with(this).load(R.drawable.ic_vector_retweet).into(binding.ivRetweet1);
                    binding.ivRetweet1.setColorFilter(ContextCompat.getColor(this, R.color.medium_green), android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount() + 1));
                    //                tweet.retweetThis(this);
                } else {
                    Glide.with(this).
                            load(R.drawable.ic_vector_retweet_stroke).into(binding.ivRetweet1);
                    binding.ivRetweet1.setColorFilter(ContextCompat.getColor(this, R.color.inline_action), android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount() - 1));
                    //                tweet.retweetThis(this);
                }
            }
        }
    }

    private void updateHeartedIcons(boolean changeIcon) {
        if (changeIcon) {
            if (tweet.isUserHearted() == false) {
                Glide.with(this).load(R.drawable.ic_vector_heart).into(binding.ivHeart);
                binding.ivHeart.setColorFilter(ContextCompat.getColor(this, R.color.medium_red), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.tvHeartCount.setText(String.valueOf(tweet.getHeartCount() + 1));
                tweet.heartThis(this);
            } else {
                Glide.with(this).load(R.drawable.ic_vector_heart_stroke).into(binding.ivHeart);
                binding.ivHeart.setColorFilter(ContextCompat.getColor(this, R.color.inline_action), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.tvHeartCount.setText(String.valueOf(tweet.getHeartCount() - 1));
                tweet.unheartThis(this);
            }

            if (reply != null) {
                if (reply.isUserHearted() == false) {
                    Glide.with(this).load(R.drawable.ic_vector_heart).into(binding.ivHeart1);
                    binding.ivHeart1.setColorFilter(ContextCompat.getColor(this, R.color.medium_red), android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.tvHeartCount1.setText(String.valueOf(reply.getHeartCount() + 1));
                    reply.heartThis(this);
                } else {
                    Glide.with(this).
                            load(R.drawable.ic_vector_heart_stroke).into(binding.ivHeart1);
                    binding.ivHeart1.setColorFilter(ContextCompat.getColor(this, R.color.inline_action), android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.tvHeartCount1.setText(String.valueOf(reply.getHeartCount() - 1));
                    reply.unheartThis(this);
                }
            }
        }
        else {
            if (tweet.isUserRetweeted() == true) {
                Glide.with(this).load(R.drawable.ic_vector_retweet).into(binding.ivRetweet);
                binding.ivRetweet.setColorFilter(ContextCompat.getColor(this, R.color.medium_green), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount() + 1));
                //            tweet.retweetThis(this);
            } else {
                Glide.with(this).load(R.drawable.ic_vector_retweet_stroke).into(binding.ivRetweet);
                binding.ivRetweet.setColorFilter(ContextCompat.getColor(this, R.color.inline_action), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount() + 1));
                //            tweet.retweetThis(this);
            }

            if (reply != null) {
                if (reply.isUserRetweeted() == true) {
                    Glide.with(this).load(R.drawable.ic_vector_retweet).into(binding.ivRetweet1);
                    binding.ivRetweet1.setColorFilter(ContextCompat.getColor(this, R.color.medium_green), android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount() + 1));
                    //                tweet.retweetThis(this);
                } else {
                    Glide.with(this).
                            load(R.drawable.ic_vector_retweet_stroke).into(binding.ivRetweet1);
                    binding.ivRetweet1.setColorFilter(ContextCompat.getColor(this, R.color.inline_action), android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount() - 1));
                    //                tweet.retweetThis(this);
                }
            }
        }
    }
}