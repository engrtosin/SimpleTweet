package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {

    public static final String TAG = "TweetModel";
    public String body;
    public String createdAt;
    public User user;
    public List<String> photoUrls;
    public List<String> hashtags;
    public List<String> urls;

    // empty constructor needed by parcel
    public Tweet() {}

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }


    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        JSONObject entities = jsonObject.getJSONObject("entities");
        tweet.fromEntities(entities);
        Log.i(TAG,String.valueOf(tweet.photoUrls.size()));
        return tweet;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public List<String> getUrls() {
        return urls;
    }

    private void fromEntities(JSONObject entities) throws JSONException {
        if (entities.has("media")) {
            fromMedia(entities.getJSONArray("media"));
        }
        else {
            photoUrls = new ArrayList<>();
        }
        if (entities.has("hashtags")) {
            Log.i(TAG,"has hashtags");
            hashtags = new ArrayList<>();
        }
        else {
            hashtags = new ArrayList<>();
        }
        if (entities.has("urls")) {
            Log.i(TAG,"has urls");
            urls = new ArrayList<>();
        }
        else {
            urls = new ArrayList<>();
        }
    }

    private void fromMedia(JSONArray media) throws JSONException {
        photoUrls = new ArrayList<>();
        for (int i = 0; i < media.length(); i++) {
            if (media.getJSONObject(i).getString("type").equals("photo")) {
                String mediaUrl = media.getJSONObject(i).getString("media_url_https");
                photoUrls.add(mediaUrl);
                Log.i(TAG,"got media url: " + mediaUrl);
            }
        }
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public String getCrudeRelativeTime() {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(createdAt).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public String getRelativeTimeAgo() {
        String relativeDate = getCrudeRelativeTime();
        String[] relativeDateSplit = relativeDate.split(" ");
        relativeDate = relativeDateSplit[0] + relativeDateSplit[1].substring(0,1);

        return relativeDate;
    }
}
