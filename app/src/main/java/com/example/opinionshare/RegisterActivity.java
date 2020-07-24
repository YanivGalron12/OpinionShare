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
import com.firebase.client.GenericTypeIndicator;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.Lists;
import com.google.firebase.Timestamp;
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
import com.google.firebase.firestore.remote.Stream;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = "TAG";
    private static final String USER_TO_DISPLAY = "USER_TO_DISPLAY";
    // add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference usersRef;
    private DatabaseReference usersMapRef;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseUserMetadata metadata = user.getMetadata();

    Date date = new Date();
    private DocumentReference mDocRef;

    // member details
//    GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
    HashMap<String,String> usersMap = new HashMap<>();



    String[] newUserToAdd;
    boolean userexists=false;
    String memberProfilePhotoUri;
    String memberPhoneNumber;
    String memberUserName;
    String memberEmail;
    String newUserName;
    String memberName;
    String OldUserName;
    String memberId;
    Member member = new Member();
    //    ArrayList<String> friendslist = new ArrayList<>();
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
        OldUserName=edit_text_username.getText().toString();


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = mFirebaseDatabase.getReference().child("users");
        usersMapRef = mFirebaseDatabase.getReference().child("usersMap");
        memberId = user.getUid();

        /**need to add the isTaskRoot() because otherwise the app will think this is a new user
         * even if the user is already been in the RegisterActivity and just wanted to come back
         * and change stuff at the same logging session (if he didn't logged out since)
         **/
        if ((isTaskRoot()) && (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp())) {
            // The user is new. Save all the user's data you can get
            // Object.requireNoNull was added by android studio suggestions
            createMemberForUser(user);


            // TODO: create UI platform for adding more information about the new user
        } else {
            // This is an existing user get Member information from ds
            userexists=true;
        }
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                member.setEmail(edit_text_email.getText().toString());
                member.setName(edit_text_fullname.getText().toString());
                newUserName = edit_text_username.getText().toString();
                if (!newUserName.equals("")) {
                    if (!newUserName.equals(memberUserName)&&!usersMap.containsValue(newUserName)) {
//                        String[] oldUserToRemove = new String[]{memberUserName,memberId};
                       usersMap.remove(memberId,memberUserName);
                       usersMap.put(memberId,newUserName);
                        member.setUsername(newUserName);
                        memberUserName = member.getUsername();
                        Toast.makeText(RegisterActivity.this, "Your details have been updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, ProfileActivity.class);
                        intent.putExtra(USER_TO_DISPLAY, memberId);
                        startActivity(intent);
                        finish();
                        addUserToDatabase(member);
                        usersMapRef.setValue(usersMap);


//                        friendslist.add(newUserToAdd.getUsername());

                    }
                    else {
                        if (newUserName.equals(memberUserName)) {
                            Intent intent = new Intent(RegisterActivity.this, ProfileActivity.class);
                            intent.putExtra(USER_TO_DISPLAY, memberId);
                            startActivity(intent);
                            finish();
                            addUserToDatabase(member);
                            usersMapRef.setValue(usersMap);
                        } else {
                            Toast.makeText(RegisterActivity.this, "UserName is taken", Toast.LENGTH_SHORT).show();
                        }
                    }


                } else {

                    Toast.makeText(RegisterActivity.this, "UserName can't be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        change_profile_photo_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleyIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleyIntent, RESULT_LOAD_IMAGE);
            }
        });
    }

    private void createMemberForUser(FirebaseUser user) {
        memberEmail = user.getEmail();
        memberName = user.getDisplayName();
        memberPhoneNumber = user.getPhoneNumber();


        if (user.getPhotoUrl() != null) {
            memberProfilePhotoUri = user.getPhotoUrl().toString();
        } else {
            memberProfilePhotoUri = "NoPhoto";
        }
        memberUserName = edit_text_username.getText().toString();
//            friendslist.add(memberId);
        member = new Member(memberId, memberName, memberEmail, memberProfilePhotoUri, memberPhoneNumber, memberUserName);
        addUserToDatabase(member); // Add this member to RTDB (Real Time Database)
        // Display user details
        edit_text_email.setText(memberEmail);
        edit_text_fullname.setText(memberName);

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
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this Location is updated
                Log.d(TAG, "onDataChange: Added information to database: \n" +
                        dataSnapshot.getValue());
                if (dataSnapshot.child(memberId).exists()) {
                    member = dataSnapshot.child(memberId).getValue(Member.class);
                    memberName = member.getName();
                    memberEmail = member.getEmail();
                    memberUserName = member.getUsername();
                    memberPhoneNumber = member.getPhoneNumber();
                    memberProfilePhotoUri = member.getProfilePhotoUri();
                    memberUserName = member.getUsername();

//
//                    if (!memberUserName.equals("")) {
//                        usersList.add(memberUserName);
//                    }
                    edit_text_email.setText(memberEmail);
                    edit_text_fullname.setText(memberName);
                    edit_text_username.setText(memberUserName);
                    Picasso.get().load(member.getProfilePhotoUri()).into(profileImage);
                } else {
                    if (!(metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp())) {
                        FirebaseUser user = auth.getCurrentUser();
                        createMemberForUser(user);
                    }
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

        usersMapRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this Location is updated
                Log.d(TAG, "onDataChange: Added information to database: \n" +
                        dataSnapshot.getValue());
                if (dataSnapshot.exists()) {

                    for (DataSnapshot d : dataSnapshot.getChildren()) {
//
//                        Object value = d.getValue();
//                        UserToShow tD = new UserToShow();
//                        if (value instanceof List) {
//                            List<Object> t = (List<Object>) value;
//                            tD.setId((String) t.get(0));
//                            tD.setUsername((String) t.get(1));
//                            // do your magic with values
//                        } else {
//                            // handle other possible types
//                            HashMap<String, String> map = (HashMap<String, String>) value;
//                            //Getting Collection of values from HashMap
//                            Collection<String> values = map.values();
//                            //Creating an ArrayList of values
//                            ArrayList<String> listOfValues = new ArrayList<String>(values);
//                            tD.setId(listOfValues.get(0));
//                            tD.setUsername(listOfValues.get(1));
//
//                        }
//
                        usersMap.put((String)d.getKey(),(String)d.getValue());


                    }
//                    memberUserName = member.getUsername();
//                    edit_text_username.setText(memberUserName);
//                    if (!memberUserName.equals("")) {
//                        usersList.add(memberUserName);
//                    }
                } else {
                    // TODO: change else actions
                    Toast.makeText(RegisterActivity.this, "users list data does not exist", Toast.LENGTH_LONG).show();
                }
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
        usersRef.child(memberId).setValue(member);
    }

}
