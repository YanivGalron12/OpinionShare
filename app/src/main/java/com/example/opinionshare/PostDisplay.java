package com.example.opinionshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDisplay extends AppCompatActivity {

    private static final String TAG = "POST DISPLAY";

    // UI Objects
    CircleImageView postOwnerPhotoImageView;
    TextView postOwnerNameTextView;
    TextView postCategoryTextView;
    TextView postRequestTextView;
    TextView postDescriptionTextView;
    ProportionalImageView postImageImageView;
    ProportionalVideoView postVideoVideoView;


    String userToDisplay_ID;
    String memberId, postType;
    int postToShow_position;
    Member userToDisplay = new Member();
    Member member = new Member();


    // add Firebase Database stuff
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference usersRef;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_display);

        intent = getIntent();
        userToDisplay_ID = intent.getStringExtra("USER_TO_DISPLAY");
        String position = intent.getStringExtra("POST_LOCATION");
        if (!position.equals("Irrelevant")) {
            postToShow_position = Integer.parseInt(position);
        } else {
            postToShow_position = -11;
        }


        postOwnerPhotoImageView = findViewById(R.id.PostOwnerPhotoImageView1);
        postOwnerNameTextView = findViewById(R.id.PostOwnerNameTextView1);
        postCategoryTextView = findViewById(R.id.PostCategoryTextView1);
        postRequestTextView = findViewById(R.id.PostRequestTextView1);
        postDescriptionTextView = findViewById(R.id.PostDescriptionTextView1);
        postImageImageView = findViewById(R.id.PostImageImageView1);
        postVideoVideoView = findViewById(R.id.PostVideoVideoView1);

        postVideoVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postVideoVideoView.isPlaying()) {
                    postVideoVideoView.pause();
                } else {
                    postVideoVideoView.start();
                }
            }
        });

        user = auth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = mFirebaseDatabase.getReference().child("users");
        memberId = user.getUid();

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
                Log.d(TAG, "onDataChange: Added information to database: \n" +
                        dataSnapshot.getValue());
                if (dataSnapshot.exists()) {
                    member = dataSnapshot.child(memberId).getValue(Member.class);
                    userToDisplay = dataSnapshot.child(userToDisplay_ID).getValue(Member.class);
                    Posts postToShow = new Posts();
                    if (postToShow_position != -11){
                        postToShow = userToDisplay.getPostByPosition(postToShow_position);
                    }
                    else{
                        String[] postDetails = intent.getStringArrayExtra("POST_DETAILS");
                        postToShow.setCategory(postDetails[0]);
                        postToShow.setCaption(postDetails[1]);
                        postToShow.setDescription(postDetails[2]);
                        postToShow.setPostType(postDetails[3]);                        postToShow.setCategory(postDetails[0]);
                        postToShow.setPostUri(postDetails[4]);
                    }


                    Picasso.get().load(userToDisplay.getProfilePhotoUri()).into(postOwnerPhotoImageView);
                    postOwnerNameTextView.setText(userToDisplay.getName());

                    postCategoryTextView.setText(postToShow.getCategory());

                    postRequestTextView.setText(postToShow.getCaption());
                    postDescriptionTextView.setText(postToShow.getDescription());
                    postType = postToShow.getPostType();

                    if (postType.equals("Video")) {
                        postImageImageView.setVisibility(View.GONE);
                        postVideoVideoView.setVisibility(View.VISIBLE);

                    } else {
                        postImageImageView.setVisibility(View.VISIBLE);
                        postVideoVideoView.setVisibility(View.GONE);
                        Picasso.get().load(postToShow.getPostUri()).into(postImageImageView);

                    }
                } else {
                    // TODO: change else actions
                    Toast.makeText(PostDisplay.this, "Cant Get Post", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }
}