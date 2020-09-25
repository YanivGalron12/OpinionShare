package com.example.opinionshare;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;

public class PostAdapter extends BaseAdapter {
// PROFILE POST ADAPTER
    private Context mContext;
    public  ArrayList<String> postArray = new ArrayList<String>();

    public PostAdapter(Context mContext, ArrayList<String> post_array){
        this.mContext = mContext;
        if(post_array!=null){
            this.postArray = post_array;
        }
    }

    @Override
    public int getCount() {
       return this.postArray.size();
    }

    @Override
    public Object getItem(int i) {
        return postArray.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView postView = new ImageView(mContext);
        Picasso.get().load(postArray.get(i)).into(postView);

        postView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        postView.setLayoutParams(new GridView.LayoutParams(340,350));
        return postView;
    }
}
