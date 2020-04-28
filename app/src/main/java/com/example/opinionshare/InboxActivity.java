package com.example.opinionshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InboxActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    FirebaseUserMetadata metadata;
    String memberId;
    Member member;
    private static final String USER_TO_DISPLAY = "USER_TO_DISPLAY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        memberId = user.getUid();
        member = new Member();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Set Profile Selected
        bottomNavigationView.setSelectedItemId(R.id.inbox);
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
                    case R.id.profile:
                        Intent intent = new Intent(InboxActivity.this, ProfileActivity.class);
                        memberId=user.getUid();
                        intent.putExtra(USER_TO_DISPLAY, memberId);
                        startActivity(intent);
                        finish();

                        overridePendingTransition(0,0);
                        return true;
                    case R.id.shop:
                        startActivity(new Intent(getApplicationContext(), ShopActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.inbox:
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
                    Toast.makeText(InboxActivity.this, "data does not exist", Toast.LENGTH_LONG).show();
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
