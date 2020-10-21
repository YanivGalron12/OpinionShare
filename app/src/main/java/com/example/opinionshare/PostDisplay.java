package com.example.opinionshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDisplay extends AppCompatActivity {

    private static final String TAG = "POST DISPLAY";

    // UI Objects
    CircleImageView postOwnerPhotoImageView;
    TextView postOwnerNameTextView;
    TextView postCategoryTextView;
    TextView postRequestTextView;
    TextView postDescriptionTextView;
   // ListView itemsInPost;
    public ProportionalImageView postImageImageView;
    ToggleButton likeButton;
  //  ItemListAdapter adapter;

    String userToDisplay_ID;
    String memberId, postType;
    int postToShow_position;
    Member userToDisplay = new Member();
    Member member = new Member();
    ArrayList<Item> items=new ArrayList<>();

    // add Firebase Database stuff
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference usersRef;
    Intent intent;
    MediaPlayer likeSoundMP;
    Random mRand = new Random();
    boolean doubleClick = false;
 //   ArrayList<String> items_uri=new ArrayList<>();
 // ArrayList<String> items_type=new ArrayList<>();
 //   ArrayList<String>items_company=new ArrayList<>();

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
       // itemsInPost=findViewById(R.id.ItemsInPost);
        likeButton = findViewById(R.id.like_button);
        //adapter = new ItemListAdapter(getApplicationContext(),R.layout.items_for_sale_listview,items);
     //   itemsInPost.setAdapter(adapter);
        postImageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        doubleClick = false;
                    }
                };
                if (doubleClick) {
                    //your logic for double click action
                    likeButton.callOnClick();
                    likeButton.toggle();
                    doubleClick = false;
                } else {
                    doubleClick = true;
                    handler.postDelayed(r, 200);
                }
            }
        });

        int[] Sounds = {R.raw.like1, R.raw.like2, R.raw.like3, R.raw.like4, R.raw.like5, R.raw.like6, R.raw.like7};
        int x = mRand.nextInt(7);
        likeSoundMP = MediaPlayer.create(this, Sounds[x]);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeSoundMP.start();
                int x = mRand.nextInt(7);
                likeSoundMP = MediaPlayer.create(PostDisplay.this, Sounds[x]);
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
                    if (postToShow_position != -11) {
                        postToShow = userToDisplay.getPostByPosition(postToShow_position);
                      //   items=postToShow.getItems();
                     //    if(items!=null) {
//                             for (int i = 0; i < items.size(); i++) {
//                                 items_uri.add(items.get(i).getImage());
//                                 items_company.add(items.get(i).getCompany());
//                                 items_type.add(items.get(i).getType());
//                             }
//                         }
//                        itemsInPost.setAdapter(adapter);

                    } else {
                        String[] postDetails = intent.getStringArrayExtra("POST_DETAILS");
                        postToShow.setCategory(postDetails[0]);
                        postToShow.setCaption(postDetails[1]);
                        postToShow.setDescription(postDetails[2]);
                        postToShow.setPostType(postDetails[3]);
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

                    } else {
                        postImageImageView.setVisibility(View.VISIBLE);
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