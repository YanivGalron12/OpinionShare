package com.example.androidfirabaseauth2019;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;

import com.facebook.login.widget.ProfilePictureView;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth ;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;



import java.util.Arrays;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 3423 ;
    String memberEmail, memberName, memberId;
    Button btn_sign_out;
    TextView username, text_email;
    // Write a message to the database
    DatabaseReference memberRef;
    Member member;


    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        btn_sign_out = findViewById(R.id.btn_sign_out);
        username = findViewById(R.id.username);
        text_email = findViewById(R.id.text_email);
        memberEmail = currentFirebaseUser.getEmail();
        memberName = currentFirebaseUser.getDisplayName();
        memberId = currentFirebaseUser.getUid();

        member = new Member(memberName,memberEmail,memberId);
        memberRef = FirebaseDatabase.getInstance().getReference().child("Member");
        memberRef.push().setValue(member);
        Toast.makeText(this,"data inserted successfully", Toast.LENGTH_LONG).show();

        username.setText(memberName);
        text_email.setText(memberEmail);
        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Logout
                AuthUI.getInstance()
                        .signOut(ProfileActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent goToLogin = new Intent(ProfileActivity.this, MainActivity.class);
                                startActivity(goToLogin);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
