package com.example.opinionshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
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

import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {


    private static final String TAG = "AddToDatabase";
    private FirebaseAuth mAuth;
    // UI Objects
    TextView text, username_text, email, firstname, lastname;
    Button signout_btn, clicktosave;
    EditText savetext;
    ProgressBar prograssbar;
    ImageView profileImage;


    // add Firebase Database stuff
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    FirebaseUserMetadata metadata;


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


        username_text = findViewById(R.id.username_text);
        signout_btn = findViewById(R.id.signout_btn);
        text = findViewById(R.id.text);
        clicktosave = findViewById(R.id.clicktosave);
        savetext = findViewById(R.id.savetext);
        prograssbar = findViewById(R.id.prograssbar);

        user = auth.getCurrentUser();

        String userDisplayName = user.getDisplayName();
        Uri photoUri =  user.getPhotoUrl();
//        Toast.makeText(this, user.getEmail(), Toast.LENGTH_SHORT).show();
        username_text.setText(userDisplayName);


        signout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
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
