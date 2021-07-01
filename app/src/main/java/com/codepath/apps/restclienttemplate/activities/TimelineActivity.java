package com.codepath.apps.restclienttemplate.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.fragments.ComposeTweetDialogFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetDialogFragment.ComposeTweetDialogListener {

    public static final int LOGOUT_RESULT_CODE = 40;
    public final int REQUEST_CODE = 20;
    public final int REQUEST_DETAILS_CODE = 30;
    public static final String TAG = "TimelineActivity";
    TwitterClient client;
    List<Tweet> tweets;
//    RecyclerView rvTweets;
    TweetsAdapter tweetsAdapter;
    ActivityTimelineBinding binding;
    MediaListAdapter mediaListAdapter;
    MenuItem miActionProgressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApp.getRestClient(this);

        binding = ActivityTimelineBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        Glide.with(this).load(R.drawable.twitter_logout)
                .transform(new RoundedCornersTransformation(40,0))
                .into(binding.btnLogout);
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLogoutButton();
            }
        });


        setContentView(view);
        setSupportActionBar(binding.tbTimeline);

        Drawable dr = getResources().getDrawable(R.drawable.ic_launcher_twitter_png);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
// Scale it to 50 x 50
        dr = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(dr);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setMenuIcons();

//        rvTweets = binding.rvTweets;
//        rvTweets = findViewById(R.id.rvTweets);

        tweets = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(this,tweets);

        binding.rvTweets.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTweets.setAdapter(tweetsAdapter);

        populateHomeTimeline();

        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync(0);
            }
        });
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetDialogFragment composeTweetDialogFragment = ComposeTweetDialogFragment.newInstance("Compose Tweet");
        composeTweetDialogFragment.show(fm, "fragment_edit_name");
    }

    private void setMenuIcons() {
        Log.i(TAG,"setMenuIcons");
        MenuItem compose = (MenuItem) findViewById(R.id.compose);
//        miActionProgressItem = (MenuItem) findViewById(R.id.miActionProgress);
//        Log.i(TAG, miActionProgressItem.toString());
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                showProgressBar();
                Log.i(TAG,"onSuccess " + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    tweetsAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG,"Cannot get tweets from jsonarray", e);
                }
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//                hideProgressBar();
                Log.e(TAG,"onFailure " + response, throwable);
            }
        });
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        showProgressBar();
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
//                hideProgressBar();
                // Remember to CLEAR OUT old items before appending in the new ones
                tweetsAdapter.clear();
                // ...the data has come back, add new items to your adapter...
                try {
                    tweetsAdapter.addAll(Tweet.fromJsonArray(json.jsonArray));
                    // Now we call setRefreshing(false) to signal refresh has finished
                    binding.swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e(TAG,"Failed to retrieve tweets objects: ", e);
                }
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//                hideProgressBar();
                Log.e(TAG, "Fetch timeline error: ",throwable);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline,menu);
        Drawable drawable = menu.findItem(R.id.compose).getIcon();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        Log.i(TAG,"onPrepareOptions");
        miActionProgressItem = menu.findItem(R.id.miActionProgress);

        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.compose) {
            showEditDialog();
//            Intent intent = new Intent(this, ComposeActivity.class);
//            startActivityForResult(intent,REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0,tweet);
            tweetsAdapter.notifyItemInserted(0);
            binding.rvTweets.smoothScrollToPosition(0);
        }
        if (requestCode == REQUEST_DETAILS_CODE && resultCode == LOGOUT_RESULT_CODE) {
            onLogoutButton();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // TimelineActivity.java
    public void onLogoutButton() {
        client.clearAccessToken(); // forget who's logged in
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // navigate backwards to Login screen
    }

    @Override
    public void onFinishEditDialog(List<Tweet> tweet_and_reply) {
        if (tweet_and_reply.size() == 1) {
            tweets.add(0, tweet_and_reply.get(0));
            tweetsAdapter.notifyItemInserted(0);
            binding.rvTweets.smoothScrollToPosition(0);
        }
        else if (tweet_and_reply.size() > 1) {
            Log.i(TAG,"reply sent");
            Intent intent = new Intent(this, TweetDetailActivity.class);
            intent.putExtra("tweet and reply",Parcels.wrap(tweet_and_reply));
            this.startActivity(intent);
        }
    }
}