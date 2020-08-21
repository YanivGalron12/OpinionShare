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

    private Context mContext;
    public  ArrayList<String> postArray = new ArrayList<String>();
//            "https://images.unsplash.com/photo-1536366764833-579c4e583927?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80",
//            "https://images.unsplash.com/photo-1518806118471-f28b20a1d79d?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80",
//            "https://data.whicdn.com/images/322027365/original.jpg?t=1541703413",
//            "https://i.pinimg.com/originals/be/ac/96/beac96b8e13d2198fd4bb1d5ef56cdcf.jpg",
//            "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcQXpnH-00qStJwOZt4JB_XUYCv7XNcL0EKMhA&usqp=CAU",
//            "https://fiverr-res.cloudinary.com/images/q_auto,f_auto/gigs/144210359/original/3c963d035643c70401b0a0460ce49ac47f3e7398/create-a-cute-anime-profile-picture-for-you.jpg"
//
//    };

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
