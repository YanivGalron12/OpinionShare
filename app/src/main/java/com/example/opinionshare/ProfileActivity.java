package com.example.opinionshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements DeleteDialog.NoticeDialogListener {

    private static final String SHOW_FRIENDS_OF_USER = "SHOW_FRIENDS_OF_USER";
    private static final String USER_TO_DISPLAY = "USER_TO_DISPLAY";
    private static final String POST_LOCATION = "POST_LOCATION";
    private static final String TAG = "AddToDatabase";
    private FirebaseAuth mAuth;


    // UI Objects
    TextView username_textview, text, numberOfFriends_textView;
    Button signout_btn, info_btn, addfriend_btn;
    GridView postGridView;
    CircleImageView profileImage;

    // add Firebase Database stuff
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference usersRef;
    String userToDisplay_ID;
    String memberId;
    Member userToDisplay = new Member();
    Member member = new Member();
    ArrayList<String> friendList = new ArrayList<>();
    ArrayList<String> posts_uri = new ArrayList<>();
    ArrayList<String> devicesToken;
    Boolean signning_out = false;

    int PositionOfPost;
    boolean userprofile = false;// if this is the current user's profile or someone else's
    boolean DeletePost = false;//if user decide to delete post or not


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        userToDisplay_ID = intent.getStringExtra("USER_TO_DISPLAY");

        signout_btn = findViewById(R.id.signout_btn);
        info_btn = findViewById(R.id.info_btn);
        addfriend_btn = findViewById(R.id.addfriend_btn);
        text = findViewById(R.id.text);
        profileImage = findViewById(R.id.profileImage);
        username_textview = findViewById(R.id.username_textview);
        postGridView = findViewById(R.id.post_grid_view);
        numberOfFriends_textView = findViewById(R.id.numofFriendsTextView);

        postGridView.setAdapter(new PostAdapter(this, posts_uri));


        postGridView.setOnItemLongClickListener((AdapterView.OnItemLongClickListener) (parent, view, position, id) -> {
            if (userprofile) {
                //option of deleting post from user profile and database
                PositionOfPost = position;
                openDialog();
            }
            return true;
        });

        postGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // go to PostDisplay activity and pass to that activity which user is currently looked on and which post to show

                Intent intent = new Intent(ProfileActivity.this, PostDisplay.class);
                intent.putExtra(USER_TO_DISPLAY, userToDisplay_ID);
                intent.putExtra(POST_LOCATION, String.valueOf(position));
                startActivity(intent);

            }
        });

        numberOfFriends_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberOfFriends_textView.getText() == "0") {
                    Toast.makeText(ProfileActivity.this, "Add user to your friend", Toast.LENGTH_SHORT).show();
                    return;
                }
                String showFriends_ofUser = userToDisplay_ID;
                Intent intent = new Intent(ProfileActivity.this, ExploreActivity.class);
                intent.putExtra(SHOW_FRIENDS_OF_USER, showFriends_ofUser);
                startActivity(intent);

            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Set Profile Selected
        bottomNavigationView.setSelectedItemId(R.id.profile);
        // Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.explore:
                        intent = new Intent(ProfileActivity.this, ExploreActivity.class);
                        intent.putExtra(SHOW_FRIENDS_OF_USER, "Show friends of all users");
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.inbox:
                        startActivity(new Intent(getApplicationContext(), InboxActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.shop:
                        startActivity(new Intent(getApplicationContext(), ShopActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        if (!memberId.equals(userToDisplay_ID)) {
                            intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            intent.putExtra(USER_TO_DISPLAY, memberId);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }

                        return true;
                }
                return false;
            }
        });


        user = auth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = mFirebaseDatabase.getReference().child("users");
        memberId = user.getUid();
        String userDisplayName = user.getDisplayName();
        Uri photoUri = user.getPhotoUrl();
        if (!userToDisplay_ID.equals(memberId)) {
            signout_btn.setEnabled(false);
            signout_btn.setVisibility(View.GONE);
            info_btn.setEnabled(false);
            info_btn.setVisibility(View.GONE);
            addfriend_btn.setEnabled(true);
            addfriend_btn.setVisibility(View.VISIBLE);

        } else {
            userprofile = true;
            signout_btn.setEnabled(true);
            signout_btn.setVisibility(View.VISIBLE);
            info_btn.setEnabled(true);
            info_btn.setVisibility(View.VISIBLE);
            addfriend_btn.setEnabled(false);
            addfriend_btn.setVisibility(View.GONE);
        }

        signout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signning_out = true;
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }
                                // Get new Instance ID token
                                String token = task.getResult().getToken();
                                devicesToken.remove(token);
                                member.setDevicesToken(devicesToken);
                                addUserToDatabase(member);
                            }
                        });
                signOut();
            }
        });
        info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, RegisterActivity.class));
            }
        });

        addfriend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (member.getFriendList() != null) {
                    friendList = (ArrayList<String>) member.getFriendList();

                    if (friendList.contains(userToDisplay_ID)) {
                        friendList.remove(userToDisplay_ID);
                        addfriend_btn.setText("add friend");
                    } else {
                        friendList.add(userToDisplay_ID);
                        addfriend_btn.setText("remove friend");
                    }
                } else {
                    friendList.add(userToDisplay_ID);
                    addfriend_btn.setText("remove friend");
                }

                member.setFriendList(friendList);
                addUserToDatabase(member);

            }
        });
    }

    public void openDialog() {
        DeleteDialog deletedialog = new DeleteDialog();
        deletedialog.show(getSupportFragmentManager(), "delete dialog"); //showing dialog of deleting option
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) { //if user decides to delete post
        DeletePost = true;
        onStart();//where all the post grid is created and where the user data can be updated
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

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
                    if (userToDisplay_ID.equals(memberId)) {
                        member = dataSnapshot.child(memberId).getValue(Member.class);
                        userToDisplay = member;
                        username_textview.setText(member.getUsername());
                        Picasso.get().load(member.getProfilePhotoUri()).into(profileImage);
                    } else {
                        userToDisplay = dataSnapshot.child(userToDisplay_ID).getValue(Member.class);
                        member = dataSnapshot.child(memberId).getValue(Member.class);
                        username_textview.setText(userToDisplay.getUsername());
                        Picasso.get().load(userToDisplay.getProfilePhotoUri()).into(profileImage);
                        if (member.getFriendList() != null) {
                            friendList = (ArrayList<String>) member.getFriendList();
                            if (friendList.contains(userToDisplay_ID)) {
                                addfriend_btn.setText("remove friend");
                            }
                        }
                    }
                    devicesToken = (ArrayList<String>) member.getDevicesToken();
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "getInstanceId failed", task.getException());
                                        return;
                                    }
                                    // Get new Instance ID token
                                    String token = task.getResult().getToken();
                                    if (devicesToken == null) {
                                        devicesToken = new ArrayList<>();
                                    }
                                    if (!devicesToken.contains(token) && !signning_out) {
                                        devicesToken.add(token);
                                        member.setDevicesToken(devicesToken);
                                        addUserToDatabase(member);
                                    }
                                }
                            });
                    if (userToDisplay.getFriendList() != null) {
                        numberOfFriends_textView.setText(String.valueOf(userToDisplay.getFriendList().size()));
                    } else {
                        numberOfFriends_textView.setText(String.valueOf(0));
                    }
                    ArrayList<Posts> PostList = (ArrayList<Posts>) userToDisplay.getPostList();

                    if (PostList != null) {
                        Toast.makeText(ProfileActivity.this, "does have posts", Toast.LENGTH_LONG).show();
                        if (DeletePost) {
                            PostList.remove(PositionOfPost);//removing post user deletes
                        }
                        if (PostList != null) {
                            for (int i = 0; i < PostList.size(); i++) {
                                posts_uri.add(PostList.get(i).getPostUri());
                            }
                            DeletePost = false;
                            userToDisplay.setPostList(PostList);
                            addUserToDatabase(userToDisplay);
                        }

                        postGridView.setAdapter(new PostAdapter(ProfileActivity.this, posts_uri));// new adapter that does not include deleted post
                        posts_uri = new ArrayList<String>();
                    } else
                        Toast.makeText(ProfileActivity.this, "has no posts", Toast.LENGTH_LONG).show();

                } else {
                    // TODO: change else actions
                    Toast.makeText(ProfileActivity.this, "data does not exist", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                        // TODO: check if logic ok and if finish is really needed
                        finish();
                    }
                });
    }

    private void addUserToDatabase(Member member) {
        String memberId = member.getUserId();
        usersRef.child(memberId).setValue(member);
    }

}
