package com.example.opinionshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    // add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUserMetadata metadata;
    String memberId;

    // member details
    String memberProfilePhotoUri;
    String memberPhoneNumber;
    String memberEmail;
    String memberName;
    Member member;



    // Activity UI
    EditText edit_text_email, edit_text_password, edit_text_fullname, edit_text_username;
    Button continue_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edit_text_email = findViewById(R.id.edit_text_email);
        edit_text_fullname = findViewById(R.id.edit_text_fullname);
        edit_text_username = findViewById(R.id.edit_text_username);
        continue_btn = findViewById(R.id.continue_btn);




        FirebaseUser user = auth.getCurrentUser();
        // TODO: check what to do with comment on .getMetadata
        assert user != null; // This was added by android studio suggestions
        metadata = user.getMetadata();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        memberId = user.getUid();
        if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
            // The user is new. Save all the user's data you can get
            // Object.requireNoNull was added by android studio suggestions
            memberEmail = user.getEmail();
            memberName = user.getDisplayName();
            memberPhoneNumber = user.getPhoneNumber();

            if (user.getPhotoUrl()!=null) {
                memberProfilePhotoUri = user.getPhotoUrl().toString();
            }else{
                memberProfilePhotoUri = "NoPhoto";
            }
            member = new Member(memberId, memberName, memberEmail, memberProfilePhotoUri, memberPhoneNumber);
            addUserToDatabase(member); // Add this member to RTDB (Real Time Database)

            // Display user details
            edit_text_email.setText(memberEmail);
            edit_text_fullname.setText(memberName);
            // TODO: create UI platform for adding more information about the new user
        } else {
            // This is an existing user get Member information from ds
            member = new Member(); // I Thinks we need to declare empty Member in order for the
                                   // addValueEventListener to be able to retrieve data to it
        }
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this,"Your details have been updated",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
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
                    Toast.makeText(RegisterActivity.this, "data does not exist", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
        memberName = member.getName();
        memberEmail = member.getEmail();
        memberPhoneNumber = member.getPhoneNumber();
        memberProfilePhotoUri = member.getProfilePhotoUri();

        edit_text_email.setText(memberEmail);
        edit_text_fullname.setText(memberName);
    }

    private void addUserToDatabase(Member member) {
        String memberId = member.getUserId();
        mRef.child("users").child(memberId).setValue(member);
        Toast.makeText(this, "Your information is now saved in our system :)", Toast.LENGTH_LONG).show();
    }

}
