package com.example.opinionshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = "TAG";
    // add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUserMetadata metadata;
    String memberId;
    private DocumentReference mDocRef;

    // member details
    String memberProfilePhotoUri;
    String memberPhoneNumber;
    String memberEmail;
    String memberName;
    Member member;


    // Activity UI
    TextView change_profile_photo_TextView;
    EditText edit_text_username;
    EditText edit_text_fullname;
    EditText edit_text_email;
    ImageView profileImage;
    Button continue_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        change_profile_photo_TextView = findViewById(R.id.change_profile_photo_TextView);
        edit_text_fullname = findViewById(R.id.edit_text_fullname);
        edit_text_username = findViewById(R.id.edit_text_username);
        edit_text_email = findViewById(R.id.edit_text_email);
        continue_btn = findViewById(R.id.continue_btn);
        profileImage = findViewById(R.id.profileImage);



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

            if (user.getPhotoUrl() != null) {
                memberProfilePhotoUri = user.getPhotoUrl().toString();
            } else {
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
                Toast.makeText(RegisterActivity.this, "Your details have been updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
            }
        });
        change_profile_photo_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleyIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleyIntent, RESULT_LOAD_IMAGE);

            }
        });
        member = new Member();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            profileImage.setImageURI(selectedImage);
            // TODO: photo needs to be downloaded and then stored in our database (Firestore ?) as file
//
//            String path = "users/"+ memberId +"/pictures";
//            // Add a new document with a generated ID
//            db.collection(path)
//                    .add()
//                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.w(TAG, "Error adding document", e);
//                        }
//                    });

            member.setProfilePhotoUri(selectedImage.toString());

            addUserToDatabase(member);
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
                    Picasso.get().load(member.getProfilePhotoUri()).into(profileImage);
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
