package com.example.opinionshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

    public class ShopActivity extends AppCompatActivity {

        private static final String TAG = "SHOP_ACTIVITY:";
        private static final String POST_DETAILS = "POST_DETAILS";
        private FirebaseDatabase mFirebaseDatabase;
        private DatabaseReference mRef;
        private DatabaseReference forSaleListRef;
        private static final String USER_TO_DISPLAY = "USER_TO_DISPLAY";
        private static final String POST_LOCATION = "POST_LOCATION";
        private static final String SHOW_FRIENDS_OF_USER = "SHOW_FRIENDS_OF_USER" ;

        String memberId;
        Member member;

        // UI Objects
        GridView forSale_grid_view;
        ArrayList<String> posts_uri = new ArrayList<>();
        ArrayList<PostForSale> forSaleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        forSale_grid_view = findViewById(R.id.forSale_grid_view);

        forSale_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // go to PostDisplay activity and pass to that activity which user is currently looked on and which post to show
                Posts post = forSaleList.get(position).getPost();
                String[] postDetails= {post.getCategory(),post.getCaption(),post.getDescription(),post.getPostType(),post.getPostUri()};

                Intent intent = new Intent(ShopActivity.this, PostDisplay.class);
                intent.putExtra(USER_TO_DISPLAY,forSaleList.get(position).getOwnerId());
                intent.putExtra(POST_LOCATION, "Irrelevant");
                intent.putExtra(POST_DETAILS, postDetails);
                startActivity(intent);
            }
        });


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        forSaleListRef = mRef.child("ForSaleList");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        memberId = user.getUid();
        member = new Member();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Set Profile Selected
        bottomNavigationView.setSelectedItemId(R.id.shop);
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
                        intent = new Intent(ShopActivity.this, ExploreActivity.class);
                        intent.putExtra(SHOW_FRIENDS_OF_USER, "Show friends of all users");
                        startActivity(intent); finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        intent = new Intent(ShopActivity.this, ProfileActivity.class);
                        memberId = user.getUid();
                        intent.putExtra(USER_TO_DISPLAY, memberId);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.inbox:
                        startActivity(new Intent(getApplicationContext(), InboxActivity.class));
                        overridePendingTransition(0, 0);
                    case R.id.shop:
                        return true;
                }
                return false;
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
                } else {
                    // TODO: change else actions
                    Toast.makeText(ShopActivity.this, "data does not exist", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
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
                        PostForSale postForSale=d.getValue(PostForSale.class);
                        boolean flag=false;
                        for(int i=0;i<forSaleList.size();i++) {
                            PostForSale currpost = forSaleList.get(i);
                            if (currpost.getTimeStamp().equals(postForSale.getTimeStamp())
                                    && currpost.getOwnerId().equals(postForSale.getOwnerId()))
                                flag = true;//a check meant to prevent unwanted multiplicity
                        }
                        if(!flag) {
                            forSaleList.add(postForSale);
                            posts_uri.add(postForSale.getPost().getPostUri());
                        }
                    }
                    forSale_grid_view.setAdapter(new PostAdapter(ShopActivity.this, posts_uri));
                } else {
                    Toast.makeText(ShopActivity.this, "nothing for sale", Toast.LENGTH_LONG).show();
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
