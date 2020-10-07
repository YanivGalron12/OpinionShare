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
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadPostActivity extends AppCompatActivity {
    private static final String TAG = "UPLOAD_POST_ACTIVITY_FOR_SALE_LIST:";
    CircleImageView postOwnerPhotoImageView;
    TextView postOwnerNameTextView;
    EditText postCategoryEditText;
    EditText postRequestEditText;
    EditText postDescriptionEditText;
    CheckBox forSaleCheckBox;
    ProportionalImageView postImageImageView;
    String selectedContant, postType;
    Button upload_button;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    String memberId;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference usersRef;
    private DatabaseReference forSaleListRef;
    Member member = new Member();
    Posts post = new Posts();
    List<Posts> PostList = new ArrayList<Posts>();
    ArrayList<PostForSale> forSaleList = new ArrayList<PostForSale>();
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
        upload_button = findViewById(R.id.upload_button);
        forSaleCheckBox = findViewById(R.id.ForSaleCheckBox);

        user = auth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = mFirebaseDatabase.getReference().child("users");
        forSaleListRef = mFirebaseDatabase.getReference().child("ForSaleList");

        memberId = user.getUid();
        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add post to data base and
                Toast.makeText(UploadPostActivity.this, postType + " Upload", Toast.LENGTH_SHORT).show();

                post.setCreationDate(java.time.LocalDate.now().toString());
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                post.setTimeStamp(Long.toString(timestamp.getTime()));
                post.setPostType(postType);
                post.setPostUri(Uri.parse(selectedContant).toString());//TODO: change link to be the link to the storage and not internal location
                post.setLikeCounter(0);
                post.setCaption(postCategoryEditText.getText().toString());
                post.setDescription(postDescriptionEditText.getText().toString());
                post.setCategory(postCategoryEditText.getText().toString());
                post.setForSale(forSaleCheckBox.isChecked());
                if (member.getPostList()!=null) {
                    PostList  = member.getPostList();
                }
                PostList.add(post);
                PostForSale postForSale = new PostForSale();
                postForSale.setOwnerId(memberId);
                postForSale.setPost(post);
                postForSale.setTimeStamp(post.getTimeStamp());
                member.setPostList(PostList);
                addPostToDatabase(member);
                if(post.getForSale()){
                    forSaleList.add(postForSale);
                    forSaleListRef.setValue(forSaleList);
                }

                // Notification for all friends of logged in user
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });


        if (postType.equals("Video")) {
            postImageImageView.setVisibility(View.GONE);
        } else {
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
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        forSaleListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this Location is updated
                Log.d(TAG, "onDataChange: Added information to database: \n" +
                        dataSnapshot.getValue());
                if (dataSnapshot.exists()) {

                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        forSaleList.add(d.getValue(PostForSale.class));
                    }
                } else {
                    // TODO: change else actions
                    Toast.makeText(UploadPostActivity.this, "ForSale list data does not exist", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }
    private void addPostToDatabase(Member member) {
        String memberId = member.getUserId();
        usersRef.child(memberId).setValue(member);
    }
}
