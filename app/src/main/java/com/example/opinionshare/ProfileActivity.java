package com.example.opinionshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {


    private static final String TAG = "AddToDatabase";
    private FirebaseAuth mAuth;


    // UI Objects
    TextView username_textview;
    TextView text;
    Button signout_btn;
    Button info_btn;

    CircleImageView profileImage;

    // add Firebase Database stuff
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUserMetadata metadata;
    FirebaseUser user;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    String memberId;
    Member member;



//    // TODO: put this onBackPressed function on the main activity of the app
//    // TODO: take care of back button bug on other activities -
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
//        homeIntent.addCategory( Intent.CATEGORY_HOME );
//        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(homeIntent);
//        return;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        signout_btn = findViewById(R.id.signout_btn);
        info_btn= findViewById(R.id.info_btn);
        text = findViewById(R.id.text);
        profileImage = findViewById(R.id.profileImage);
        username_textview = findViewById(R.id.username_textview);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Set Profile Selected
        bottomNavigationView.setSelectedItemId(R.id.profile);
        // Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.explore:
                        startActivity(new Intent(getApplicationContext(),ExploreActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.inbox:
                        startActivity(new Intent(getApplicationContext(), InboxActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.shop:
                        startActivity(new Intent(getApplicationContext(), ShopActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        return true;
                }
                return false;
            }
        });

        user = auth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        memberId = user.getUid();

        String userDisplayName = user.getDisplayName();
        Uri photoUri =  user.getPhotoUrl();


        signout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, RegisterActivity.class));
            }
        });




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
                    username_textview.setText(member.getName());
                    if(!member.getProfilePhotoUri().equals("NoPhoto")){
                        Picasso.get().load(member.getProfilePhotoUri()).into(profileImage);
                    }else{
                        Picasso.get().load("https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcThwba7bWlXMP_8RyrorKR_NqUpHKlZMBcAJNxzdOMiOC7d5csj&usqp=CAU").into(profileImage);
                    }
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
}
