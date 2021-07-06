package com.codepath.apps.restclienttemplate.unused;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MediaListAdapter extends BaseAdapter {
    public static final String TAG = "MediaListAdapter";
    public Context context;
    public List<String> photoUrls;

    public MediaListAdapter(Context context, List<String> photoUrls) {
        this.context = context;
        this.photoUrls = photoUrls;
    }

    @Override
    public int getCount() {
        return photoUrls.size();
    }

    @Override
    public Object getItem(int i) {
        return photoUrls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.item_embedphoto, parent, false);
        }

        // get current item to be displayed
        String currentPhotoUrl = (String) getItem(i);

        ImageView ivEmbedPhoto1 = (ImageView) convertView.findViewById(R.id.ivEmbedPhoto);
        Log.i(TAG,currentPhotoUrl);
        Glide.with(context).load(currentPhotoUrl)
                .transform(new RoundedCornersTransformation(30,0))
                .into(ivEmbedPhoto1);

        // returns the view for the current row
        return convertView;
    }
}
