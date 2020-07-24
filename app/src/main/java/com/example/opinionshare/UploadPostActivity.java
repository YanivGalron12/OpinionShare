package com.example.opinionshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    String memberId;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference usersRef;
    Member member = new Member();
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

        user = auth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = mFirebaseDatabase.getReference().child("users");
        memberId = user.getUid();
        postVideoVideoView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (postVideoVideoView.isPlaying()){
                    postVideoVideoView.pause();
                } else {
                    postVideoVideoView.start();
                }
            }

        });
        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add post to data base and
                Toast.makeText(UploadPostActivity.this, postType + " Upload", Toast.LENGTH_SHORT).show();
//                postCategoryEditText ;
//                postRequestEditText;
//                postDescriptionEditText;
//                memberId;
                if (postType.equals("Video")) {
//                    postVideoVideoView.setVideoURI(Uri.parse(selectedContant));
                }else{
//                    Picasso.get().load(selectedContant).into(postImageImageView);
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
    @Override
    protected void onStart() {
        super.onStart();
        // Read from the database
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this Location is updated
                if (dataSnapshot.exists()) {
                    member = dataSnapshot.child(memberId).getValue(Member.class);
                    postOwnerNameTextView.setText(member.getUsername());
                    Picasso.get().load(member.getProfilePhotoUri()).into(postOwnerPhotoImageView);
                } else {
                    // TODO: change else actions
                    Toast.makeText(UploadPostActivity.this, "data does not exist", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
