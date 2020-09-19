package com.example.opinionshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import com.facebook.login.widget.ProfilePictureView;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Choose an arbitrary request code value
    private static final int MY_REQUEST_CODE = 3423;
    private static final String TAG = "TAG";
    // add Firebase Database stuff
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUserMetadata metadata;
    List<AuthUI.IdpConfig> providers;
    String memberId;
    private static final String USER_TO_DISPLAY = "USER_TO_DISPLAY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            // user is already signed in
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            memberId=user.getUid();
            intent.putExtra(USER_TO_DISPLAY, memberId);
            startActivity(intent);
            finish();
            return; // need return because we don't need to execute showSignInOption and set providers
        }

        providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(), // Google Builder
                //new AuthUI.IdpConfig.FacebookBuilder().build(), //Facebook Builder
                //new AuthUI.IdpConfig.PhoneBuilder().build(), // Phone Builder
                new AuthUI.IdpConfig.EmailBuilder().build() // Email Builder
                // TODO: fix Facebook provider authentication - follow the guide at
                // TODO: https://developers.facebook.com/apps/202893697669946/fb-login/quickstart/ At 2.6
                // TODO: add email/phone authentication
        );

        showSignInOptions();

    }

    private void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.opinionshare_logo)
                        .setTheme(R.style.AppTheme)
                        .setIsSmartLockEnabled(false)
                        .build(),
                MY_REQUEST_CODE);
        // we can add PrivacyPolicy by adding .setTosAndPrivacyPolicyUrls
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // MY_REQUEST_CODE is the request code you passed into startActivityForResult(...) when starting the sign in flow
        if (requestCode == MY_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                //Get User  ONLY HERE the user is finished logging in
                FirebaseUser user = auth.getCurrentUser();
                // TODO: check what to do with comment on .getMetadata
                metadata = user.getMetadata();
                if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
                    // The user is new, show them a fancy intro screen!
                    // TODO: pass to the next intent that this is a new user or check there if the user is new
                    Toast.makeText(this, "Welcome to OpinionShare !!!!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                    finish();
                } else {
                    // This is an existing user.
                    // TODO: show welcome back screen?
                    Toast.makeText(this, "Welcome back my old friend", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    finish();
                }
            } else {
                // Sign in failed
                // TODO: check what is it Snackbar - (Flutter?)
                if (response == null) {
                    // User pressed back button
                    finish();
                    //showSnackbar(R.string.sign_in_cancelled);
                    return;
                }
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "Failed to sign in", Toast.LENGTH_LONG).show();
                    //showSnackbar(R.string.no_internet_connection);
                    return;
                }

                //showSnackbar(R.string.unknown_error);
                //Log.e(TAG, "Sign-in error: ", response.getError());

            }
        }
    }
}
