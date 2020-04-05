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

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    // add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUserMetadata metadata;
    Firebase firebase;

    // Activity UI
    Button signup_btn;
    EditText edit_text_email, edit_text_password, edit_text_fullname, edit_text_username;
    TextView login;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        signup_btn = findViewById(R.id.signup_btn);
        edit_text_email = findViewById(R.id.edit_text_email);
        edit_text_password = findViewById(R.id.edit_text_password);
        edit_text_fullname = findViewById(R.id.edit_text_fullname);
        edit_text_username = findViewById(R.id.edit_text_username);
        progressBar = findViewById(R.id.progressbar);

        FirebaseUser user = auth.getCurrentUser();
        // TODO: check what to do with comment on .getMetadata
        metadata = user.getMetadata();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
            // The user is new. Save all the user's data you can get
            String memberProfilePhotoUri = user.getPhotoUrl().toString(); //uri is saved as string
            String memberId = user.getUid();
            String memberName = user.getDisplayName();
            String memberEmail = user.getEmail();
            String memberPhoneNumber = user.getPhoneNumber();
            Member member = new Member(memberId, memberName, memberEmail, memberProfilePhotoUri, memberPhoneNumber);
            addUserToDatabase(member); // Add this member to RTDB (Real Time Database)
            // TODO: create UI platform for adding more information about the new user
        } else {
            // This is an existing user get Member information from ds
        }

        // Read from the database
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this Location is updated
                Log.d(TAG, "onDataChange: Added information to database: \n" +
                        dataSnapshot.getValue());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value

                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }
    private void addUserToDatabase(Member member) {
        String memberId = member.getUserId();
        mRef.child("users").child(memberId).setValue(member);
        Toast.makeText(this, "Your information is now saved in our system :)", Toast.LENGTH_LONG).show();
    }
}
