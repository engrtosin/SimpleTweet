package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.activities.ComposeActivity;
import com.codepath.apps.restclienttemplate.activities.TimelineActivity;
import com.codepath.apps.restclienttemplate.databinding.FragmentEditNameBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
// ...

public class ComposeTweetDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    public static final String TAG = "ComposeDialogFragment";
    private EditText mEditText;
    private ImageView mBtnTweet;
    FragmentEditNameBinding binding;
    TwitterClient client;
    Tweet tweetToReply;
    String replyToId;
    public static final int MAX_TWEET_LENGTH = 140;

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (EditorInfo.IME_ACTION_DONE == i) {
            publishNewTweet();
            return true;
        }
        return false;
    }

    public interface ComposeTweetDialogListener {
        void onFinishEditDialog(List<Tweet> tweet_and_reply);
    }

    public ComposeTweetDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ComposeTweetDialogFragment  newInstance(String title, Tweet replyTweet) {
        ComposeTweetDialogFragment frag = new ComposeTweetDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putParcelable("replyTo",Parcels.wrap(replyTweet));
        frag.setArguments(args);
        return frag;
    }

    public static ComposeTweetDialogFragment newInstance(String title) {
        ComposeTweetDialogFragment frag = new ComposeTweetDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditNameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        client = TwitterApp.getRestClient(getActivity());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.etCompose1.setOnEditorActionListener(this);
        binding.etCompose1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.tvCharCounter.setText((MAX_TWEET_LENGTH - (editable.toString().length())) + "/" + MAX_TWEET_LENGTH);
            }
        });
        // Get field from view
//        mEditText = (EditText) view.findViewById(R.id.etCompose1);
//        mBtnTweet = (ImageView) view.findViewById(R.id.)
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        if (title.equals("Compose Tweet")) {
            // Show soft keyboard automatically and request focus to field
            binding.etCompose1.requestFocus();
            // set on click listener for send button
            binding.ivBtnTweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    publishNewTweet();
                }
            });
        }
        else if (title.equals("Reply Tweet")) {
            tweetToReply = (Tweet) Parcels.unwrap(getArguments().getParcelable("replyTo"));
            binding.etCompose1.setText("@" + tweetToReply.getUser().getScreenName());
            binding.etCompose1.requestFocus();
            // set on click listener for send button
            binding.ivBtnTweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    publishNewReply();
                }
            });
        }
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void publishNewReply() {
        String replyContent = binding.etCompose1.getText().toString();
        if (replyContent.isEmpty()) {
            return;
        }
        if (replyContent.length() > 140) {
            return;
        }
        if (!replyContent.contains(tweetToReply.getUser().getScreenName())) {
            return;
        }
        Log.i(TAG,replyContent);
        Log.i(TAG,tweetToReply.getId());
        client.replyTweet(replyContent, tweetToReply.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess to reply tweet");
                try {
                    Tweet tweet = Tweet.fromJson(json.jsonObject);
                    Log.i(TAG,"Reply says: " + tweet.getBody());
                    ComposeTweetDialogListener listener = (ComposeTweetDialogListener) getActivity();
                    List<Tweet> tweet_and_reply = new ArrayList<>();
                    tweet_and_reply.add(tweetToReply);
                    tweet_and_reply.add(tweet);
                    Log.i(TAG,String.valueOf(tweet_and_reply.size()));
                    listener.onFinishEditDialog(tweet_and_reply);
                    dismiss();
                } catch (JSONException e) {
                    Log.e(TAG,"Failed to get the tweet object", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG,  "onFailure", throwable);
            }
        });
    }

    private void publishNewTweet() {
        String tweetContent = binding.etCompose1.getText().toString();
        if (tweetContent.isEmpty()) {
            return;
        }
        if (tweetContent.length() > 140) {
            return;
        }
        client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess to publish tweet");
                try {
                    Tweet tweet = Tweet.fromJson(json.jsonObject);
                    Log.i(TAG,"Published tweet says: " + tweet.getBody());
                    ComposeTweetDialogListener listener = (ComposeTweetDialogListener) getActivity();
                    List<Tweet> tweet_and_reply = new ArrayList<>();
                    tweet_and_reply.add(tweet);
                    listener.onFinishEditDialog(tweet_and_reply);
                    dismiss();
                } catch (JSONException e) {
                    Log.e(TAG,"Failed to get the tweet object", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG,  "onFailure", throwable);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}