package com.example.opinionshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bolts.Bolts;
import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = "TAG";
    private static final String POST_TYPE = "POST_TYPE";
    private static final String URI_SELECTED = "URI_SELECTED";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private StorageReference mStorageRef;
    int SELECT_VIDEO_REQUEST = 100;
    String memberId;
    Member member;
    private Uri selectedImage;
    private Uri selectedVideoPath;
    private static final String USER_TO_DISPLAY = "USER_TO_DISPLAY";
    ListView listView;

    // Set UI Variables
    ListView postsListView;
    LinearLayout uploadVideoLayout;
    LinearLayout uploadPhotoLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mStorageRef = FirebaseStorage.getInstance().getReference().child("MembersPosts");
        postsListView = findViewById(R.id.PostsListView);
        uploadPhotoLayout = findViewById(R.id.UploadPhotoLayout);
        uploadVideoLayout = findViewById(R.id.UploadVideoLayout);

        uploadVideoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Move to Upload Page and say to it that you want to upload Video
                selectVideoFromGallery();
            }
        });

        uploadPhotoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Move to Upload Page and say to it that you want to upload Photo
                Intent galleyIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleyIntent, RESULT_LOAD_IMAGE);
            }
        });

        ArrayList<FriendsPost> allPostFromLast20Days = getAllFriendsPostFromLast20Days(member);
        String mTitle[] =new String[allPostFromLast20Days.size()];
        Boolean mIsVideo[] =new Boolean[allPostFromLast20Days.size()];
        String friend_to_post_list[] = new String[allPostFromLast20Days.size()];
        int i = 0;
        for (FriendsPost friendsPost: allPostFromLast20Days){
            Toast.makeText(HomeActivity.this,"3",Toast.LENGTH_SHORT);

            mTitle[i] = friendsPost.getMtitle();
            mIsVideo[i] = friendsPost.getIsvideo();
            friend_to_post_list[i] = friendsPost.getFriendID();
            i=i+1;
        }


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
                    switch (item.getItemId()) {
                    case R.id.profile:
                        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                        memberId = user.getUid();
                        intent.putExtra(USER_TO_DISPLAY, memberId);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.explore:
                        startActivity(new Intent(getApplicationContext(), ExploreActivity.class));
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

    public void selectVideoFromGallery() {
        Intent intent;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI);
        }
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, SELECT_VIDEO_REQUEST);
    }
    public  ArrayList<FriendsPost> getAllFriendsPostFromLast20Days(Member member){

        ArrayList<FriendsPost> allPostFromLast20Days = new ArrayList<>();
        try {
            ArrayList<String> FriendsID = (ArrayList<String>) member.getFriendList();

            for (String friendID : FriendsID){
                allPostFromLast20Days.addAll(getPostFromLast20Days(friendID));
            }
        }catch (Exception e) {
            Toast.makeText(HomeActivity.this,e.toString(),Toast.LENGTH_SHORT);
            return allPostFromLast20Days;
        }

        return allPostFromLast20Days;
    }

    public ArrayList<FriendsPost> getPostFromLast20Days(String friendID){

        ArrayList<FriendsPost> allPostFromLast20Days = new ArrayList<>();

        DatabaseReference postRef = mFirebaseDatabase.getReference().child("users").child(friendID);
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this Location is updated
                Log.d(TAG, "onDataChange: Added information to database: \n" +
                        dataSnapshot.getValue());
                Toast.makeText(HomeActivity.this,"1",Toast.LENGTH_LONG);

                if (dataSnapshot.exists()) {
                    Toast.makeText(HomeActivity.this,"2",Toast.LENGTH_LONG);

                    Member friend = dataSnapshot.getValue(Member.class);
                    ArrayList<Posts> friend_post_list = (ArrayList<Posts>) friend.getPostList();
                    for (int i =0; i<friend_post_list.size();i++)
                    {
                        Posts post=friend_post_list.get(i);
                        LocalDate creationDate = LocalDate.parse(post.getCreationDate());
                        LocalDate cutoffDate = LocalDate.now().minusDays(20);
                        if(creationDate.isAfter(cutoffDate)) {
                            FriendsPost friendsPost = new FriendsPost();
                            friendsPost.setPost(post);
                            friendsPost.setIsvideo(post.getPostType().equals("Video"));
                            friendsPost.setMtitle(friend.getUsername());
                            friendsPost.setFriendID(friendID);
                            allPostFromLast20Days.add(friendsPost);
                            int x = 5 ;
                        }
                    }
                    Toast.makeText(HomeActivity.this, "there are "+allPostFromLast20Days.size()+" posts", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(HomeActivity.this, "no posts recognized", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });



        return allPostFromLast20Days;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
            Intent intent = new Intent(HomeActivity.this, UploadPostActivity.class);
            //444444
            StorageReference postRef = mStorageRef.child("postRef" + selectedImage.getLastPathSegment());
            postRef.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    postRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            selectedImage = uri;
                            Toast.makeText(HomeActivity.this, "HappyPhotoUploaded", Toast.LENGTH_LONG).show();
                            intent.putExtra(POST_TYPE, "Image");
                            intent.putExtra(URI_SELECTED, selectedImage.toString());
                            startActivity(intent);
                        }

                    });
                }
            });
        }
        if (requestCode == SELECT_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedVideoPath = data.getData();
            Intent intent = new Intent(HomeActivity.this, UploadPostActivity.class);
            //4444444
            StorageReference postRef = mStorageRef.child("post" + selectedVideoPath.getLastPathSegment());
            postRef.putFile(selectedVideoPath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    postRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            selectedVideoPath = uri;
                            Toast.makeText(HomeActivity.this, "Happy", Toast.LENGTH_LONG).show();
                            intent.putExtra(POST_TYPE, "Video");
                            intent.putExtra(URI_SELECTED, selectedVideoPath.toString());
                            startActivity(intent);
                        }
                    });
                }
            });
        }
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String rTitle[];
        Boolean rIsVideo[];

        MyAdapter(Context c, String title[], Boolean isVideo[]) {
            super(c, R.layout.post_display, R.id.PostOwnerNameTextView1, title);
            this.context = c;
            this.rTitle = title;
            this.rIsVideo = isVideo;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View post_display = layoutInflater.inflate(R.layout.post_display, parent, false);
            CircleImageView postOwnerPhotoImageView = post_display.findViewById(R.id.PostOwnerPhotoImageView1);
            ProportionalImageView postImageImageView = post_display.findViewById(R.id.PostImageImageView1);
            ProportionalVideoView postVideoVideoView = post_display.findViewById(R.id.PostVideoVideoView1);
            TextView postOwnerNameTextView = post_display.findViewById(R.id.PostOwnerNameTextView1);
            TextView postCategoryTextView = post_display.findViewById(R.id.PostCategoryTextView1);
            TextView postRequestTextView = post_display.findViewById(R.id.PostRequestTextView1);
            TextView postDescriptionTextView = post_display.findViewById(R.id.PostDescriptionTextView1);

            // now set our resources
            postOwnerNameTextView.setText(rTitle[position]);
            if (rIsVideo[position]) {
                postVideoVideoView.setVisibility(View.VISIBLE);
                postImageImageView.setVisibility(View.GONE);
            } else {
                postVideoVideoView.setVisibility(View.GONE);
                postImageImageView.setVisibility(View.VISIBLE);
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
                    member  = dataSnapshot.child("users").child(memberId).getValue(Member.class);
                    ArrayList<FriendsPost> allPostFromLast20Days = getAllFriendsPostFromLast20Days(member);
                    String mTitle[] =new String[allPostFromLast20Days.size()];
                    Boolean mIsVideo[] =new Boolean[allPostFromLast20Days.size()];
                    String friend_to_post_list[] = new String[allPostFromLast20Days.size()];
                    int i = 0;
                    for (FriendsPost friendsPost: allPostFromLast20Days){
                        Toast.makeText(HomeActivity.this,"3",Toast.LENGTH_SHORT);

                        mTitle[i] = friendsPost.getMtitle();
                        mIsVideo[i] = friendsPost.getIsvideo();
                        friend_to_post_list[i] = friendsPost.getFriendID();
                        i=i+1;
                    }

                    MyAdapter adapter = new MyAdapter(HomeActivity.this, mTitle, mIsVideo);
                    postsListView.setAdapter(adapter);
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
