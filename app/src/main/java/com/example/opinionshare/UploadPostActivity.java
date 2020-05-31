package com.example.opinionshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadPostActivity extends AppCompatActivity {

    CircleImageView postOwnerPhotoImageView;
    TextView postOwnerNameTextView;
    EditText postCategoryEditText;
    EditText postRequestEditText;
    EditText postDescriptionEditText;
    ProportionalImageView postImageImageView;
    ProportionalVideoView postVideoVideoView;
    String selectedContant, postType;
    Button upload_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post);

        Intent intent = getIntent();
        postType = intent.getStringExtra("POST_TYPE");
        selectedContant = intent.getStringExtra("URI_SELECTED");

        postOwnerPhotoImageView = findViewById(R.id.PostOwnerPhotoImageView);
        postOwnerNameTextView = findViewById(R.id.PostOwnerNameTextView);
        postCategoryEditText = findViewById(R.id.PostCategoryEditText);
        postRequestEditText = findViewById(R.id.PostRequestEditText);
        postDescriptionEditText = findViewById(R.id.PostDescriptionEditText);
        postImageImageView = findViewById(R.id.PostImageImageView);
        postVideoVideoView = findViewById(R.id.PostVideoVideoView);

        upload_button = findViewById(R.id.upload_button);

        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add post to data base and
                Toast.makeText(UploadPostActivity.this, postType + " Upload", Toast.LENGTH_SHORT).show();
                if (postType.equals("Video")) {
                    postVideoVideoView.start();
                }
            }
        });

        if (postType.equals("Video")) {
            postVideoVideoView.setVisibility(View.VISIBLE);
            postImageImageView.setVisibility(View.GONE);
            postVideoVideoView.setVideoURI(Uri.parse(selectedContant));
//            postVideoVideoView.setVideoPath(selectedContant);
        } else {
            postVideoVideoView.setVisibility(View.GONE);
            postImageImageView.setVisibility(View.VISIBLE);
            Picasso.get().load(selectedContant).into(postImageImageView);
        }
    }
}
