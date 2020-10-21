package com.example.opinionshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import bolts.Bolts;
import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity {

    private static final String SHOW_FRIENDS_OF_USER = "SHOW_FRIENDS_OF_USER" ;
    private static final String USER_TO_DISPLAY = "USER_TO_DISPLAY";
    private static final String URI_SELECTED = "URI_SELECTED";
    private static final String POST_TYPE = "POST_TYPE";
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = "TAG";
    private static final String ORIGIN = "ORIGIN";
    Random mRand = new Random();
    boolean doubleClick = false;
    MediaPlayer likeSoundMP;
    String friend_to_post_list[];


    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private StorageReference mStorageRef;
    private Uri selectedImage;
    String memberId;
    Member member;

    // Set UI Variables
    ListView postsListView;
    TextView NoPostsTextView;
    LinearLayout uploadPhotoLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mStorageRef = FirebaseStorage.getInstance().getReference().child("MembersPosts");
        postsListView = findViewById(R.id.PostsListView);
        uploadPhotoLayout = findViewById(R.id.UploadPhotoLayout);
        NoPostsTextView = findViewById(R.id.NoPostsLabel);
        NoPostsTextView.setVisibility(View.GONE);


        uploadPhotoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Move to Upload Page and say to it that you want to upload Photo
                Intent galleyIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleyIntent, RESULT_LOAD_IMAGE);
            }
        });


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        memberId = user.getUid();
        member = new Member();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Set Profile Selected
        bottomNavigationView.setSelectedItemId(R.id.home);
        // Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // this is the navigation bar at the bottom of the page so the user will be able to
                //move from different activities.
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.profile:
                        intent = new Intent(HomeActivity.this, ProfileActivity.class);
                        memberId = user.getUid();
                        intent.putExtra(USER_TO_DISPLAY, memberId);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.explore:
                        intent = new Intent(HomeActivity.this, ExploreActivity.class);
                        intent.putExtra(SHOW_FRIENDS_OF_USER, "Show friends of all users");
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.inbox:
                        startActivity(new Intent(getApplicationContext(), InboxActivity.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.shop:
                        startActivity(new Intent(getApplicationContext(), ShopActivity.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.home:
                        return true;
                }
                return false;
            }
        });
    }

    public void getAllFriendsPostFromLast20Days(Member member) {
        //a function that sorts all the post of the user's friends from the last 20 days
        //and displays them on the screen.
        try {//we used the try&catch many times in this app to prevent null pointer error.
            ArrayList<FriendsPost> allPostFromLast20Days = new ArrayList<>();
            ArrayList<String> ownersProfilePhoto = new ArrayList<>();

            ArrayList<String> FriendsID = (ArrayList<String>) member.getFriendList();
            for (String friendID : FriendsID) {//going through each of the user's friends.
                DatabaseReference postRef = mFirebaseDatabase.getReference().child("users").child(friendID);
                postRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this Location is updated
                        Log.d(TAG, "onDataChange: Added information to database: \n" +
                                dataSnapshot.getValue());
                        if (dataSnapshot.exists()) {
                            Member friend = dataSnapshot.getValue(Member.class);
                            int size = 0;
                            ArrayList<Posts> friend_post_list = new ArrayList<>();
                            friend_post_list = (ArrayList<Posts>) friend.getPostList();
                            if (friend_post_list != null) {
                                friend_post_list.removeAll(Collections.singleton(null));
                                //making sure there are no deleted posts still located in the database
                            }
                            try {
                                size = friend_post_list.size();
                            } catch (Exception e) {
                                size = 0;
                            }

                            for (int i = 0; i < size; i++) {
                                Posts post = friend_post_list.get(i);
                                LocalDate creationDate = LocalDate.parse(post.getCreationDate());
                                LocalDate cutoffDate = LocalDate.now().minusDays(20);
                                if (creationDate.isAfter(cutoffDate)) {
                                    FriendsPost friendsPost = new FriendsPost();
                                    friendsPost.setPost(post);
                                    friendsPost.setIsvideo(post.getPostType().equals("Video"));
                                    friendsPost.setMtitle(friend.getUsername());
                                    friendsPost.setFriendID(friendID);
                                    allPostFromLast20Days.add(friendsPost);
                                    ownersProfilePhoto.add(friend.getProfilePhotoUri());
                                }
                            }

                            if (allPostFromLast20Days.size() != 0) {
                                Toast.makeText(HomeActivity.this, "there are " + allPostFromLast20Days.size() + " posts", Toast.LENGTH_LONG).show();
                                NoPostsTextView.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(HomeActivity.this, "no posts recognized", Toast.LENGTH_LONG).show();
                                NoPostsTextView.setVisibility(View.VISIBLE);
                            }
                            friend_to_post_list = new String[allPostFromLast20Days.size()];
                            int i = 0;
                            for (FriendsPost friendsPost : allPostFromLast20Days) {
                                Toast.makeText(HomeActivity.this, "3", Toast.LENGTH_SHORT);
                                friend_to_post_list[allPostFromLast20Days.size() - 1 - i] = friendsPost.getFriendID();
                                i = i + 1;
                            }
                            Collections.reverse(allPostFromLast20Days);//to show it in LIFO.
                            Collections.reverse(ownersProfilePhoto);
                            MyAdapter adapter = new MyAdapter(HomeActivity.this, allPostFromLast20Days, friend_to_post_list, ownersProfilePhoto);
                            postsListView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", databaseError.toException());
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(HomeActivity.this, e.toString(), Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Toast.makeText(HomeActivity.this, "Uploading", Toast.LENGTH_LONG).show();
            selectedImage = data.getData();
            //444444
            StorageReference postRef = mStorageRef.child("postRef" + selectedImage.getLastPathSegment());//where the post is located in the firebase.
            StorageReference takePost = mStorageRef.child("resizes").child("postRef" + selectedImage.getLastPathSegment() + "_1000x1000");
            //here we resize the images so they will take less space and will be easier to upload and in the same size.
            postRef.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    try {
                        Thread.sleep(10000);//the time needed for the upload
                        // we dont wnat to proceed before the image is resized and ready for storage


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    takePost.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            selectedImage = uri;
                            Intent intent = new Intent(HomeActivity.this, UploadPostActivity.class);
                            intent.putExtra(POST_TYPE, "Image");
                            intent.putExtra(URI_SELECTED, selectedImage.toString());
                            intent.putExtra(ORIGIN,"HomeActivity");
                            startActivity(intent);
                        }
                    });
                }
            });
        }
    }

    class MyAdapter extends ArrayAdapter<String> {//this is the adapter for the listview of the posts.
        Context context;
        ArrayList<FriendsPost> rAllPostFromLast20Days;
        String rFriend_to_post_list[];
        ArrayList<String> rOwnersProfilePhoto;

        MyAdapter(Context c, ArrayList<FriendsPost> allPostFromLast20Days, String friend_to_post_list[], ArrayList<String> ownersProfilePhoto) {
            super(c, R.layout.post_display, R.id.PostOwnerNameTextView1, friend_to_post_list);
            this.context = c;
            this.rAllPostFromLast20Days = allPostFromLast20Days;
            this.rFriend_to_post_list = friend_to_post_list;
            this.rOwnersProfilePhoto = ownersProfilePhoto;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View post_display = layoutInflater.inflate(R.layout.post_display, parent, false);
            CircleImageView postOwnerPhotoImageView = post_display.findViewById(R.id.PostOwnerPhotoImageView1);
            ProportionalImageView postImageImageView = post_display.findViewById(R.id.PostImageImageView1);
            TextView postOwnerNameTextView = post_display.findViewById(R.id.PostOwnerNameTextView1);
            TextView postCategoryTextView = post_display.findViewById(R.id.PostCategoryTextView1);
            TextView postRequestTextView = post_display.findViewById(R.id.PostRequestTextView1);
            TextView postDescriptionTextView = post_display.findViewById(R.id.PostDescriptionTextView1);
            ToggleButton likeButton = post_display.findViewById(R.id.like_button);

            postOwnerNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                    //when clicking the username of the user who uploaded the post it goes to his profile.
                    String userToDisplay = friend_to_post_list[position];
                    intent.putExtra(USER_TO_DISPLAY, userToDisplay);
                    startActivity(intent);
                    finish();
                }
            });
            postOwnerPhotoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    postOwnerNameTextView.callOnClick();
                }
            });

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
            //different sound for the like button
            int x = mRand.nextInt(7);
            likeSoundMP = MediaPlayer.create(HomeActivity.this, Sounds[x]);
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    likeSoundMP.start();
                    int x = mRand.nextInt(7);
                    likeSoundMP = MediaPlayer.create(HomeActivity.this, Sounds[x]);
                }
            });


            FriendsPost currentPost = rAllPostFromLast20Days.get(position);
            String rTitle = currentPost.getMtitle();
            Boolean rIsVideo = currentPost.getIsvideo();
            Posts ThisPost = currentPost.getPost();
            // now set our resources
            postOwnerNameTextView.setText(rTitle);
            if (rIsVideo) {
                postImageImageView.setVisibility(View.GONE);
            } else {
                postImageImageView.setVisibility(View.VISIBLE);
                Picasso.get().load(ThisPost.getPostUri()).into(postImageImageView);
                postCategoryTextView.setText(ThisPost.getCategory());
                postDescriptionTextView.setText(ThisPost.getDescription());
                postRequestTextView.setText(ThisPost.getCaption());
                Picasso.get().load(rOwnersProfilePhoto.get(position)).into(postOwnerPhotoImageView);

            }
            return post_display;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Read from the database
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this Location is updated
                Log.d(TAG, "onDataChange: Added information to database: \n" +
                        dataSnapshot.getValue());
                if (dataSnapshot.exists()) {
                    member = dataSnapshot.child("users").child(memberId).getValue(Member.class);
                    if (member.getFriendList() != null) {
                        NoPostsTextView.setText("No Posts");
                        NoPostsTextView.setVisibility(View.GONE);
                        getAllFriendsPostFromLast20Days(member);
                    } else {
                        NoPostsTextView.setVisibility(View.VISIBLE);
                        NoPostsTextView.setText("No Friends");
                    }

                } else {
                    // TODO: change else actions
                    Toast.makeText(HomeActivity.this, "data does not exist", Toast.LENGTH_LONG).show();
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
