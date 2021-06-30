package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import okhttp3.Headers;
// ...

public class ComposeTweetDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    public static final String TAG = "ComposeDialogFragment";
    private EditText mEditText;
    private ImageView mBtnTweet;
    FragmentEditNameBinding binding;
    TwitterClient client;

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (EditorInfo.IME_ACTION_DONE == i) {
            publishNewTweet();
            return true;
        }
        return false;
    }

    public interface ComposeTweetDialogListener {
        void onFinishEditDialog(Tweet tweet);
    }

    public ComposeTweetDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
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
        // Get field from view
//        mEditText = (EditText) view.findViewById(R.id.etCompose1);
//        mBtnTweet = (ImageView) view.findViewById(R.id.)
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        binding.etCompose1.requestFocus();
        // set on click listener for send button
        binding.ivBtnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishNewTweet();
            }
        });
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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
                    listener.onFinishEditDialog(tweet);
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