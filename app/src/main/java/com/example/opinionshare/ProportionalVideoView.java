package com.example.opinionshare;

import android.app.Activity;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.VideoView;

import io.grpc.internal.SharedResourceHolder;

public class ProportionalVideoView extends VideoView {
    private int mVideoWidth;
    private int mVideoHeight;

    public ProportionalVideoView(Context context) {
        super(context);
    }

    public ProportionalVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProportionalVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setVideoURI(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this.getContext(), uri);
        mVideoWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        mVideoHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        super.setVideoURI(uri);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            height = width * mVideoWidth/mVideoHeight;

        }
        setMeasuredDimension(width, height);
    }
}

