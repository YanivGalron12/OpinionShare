package com.example.opinionshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.firebase.ui.auth.data.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExploreActivity extends AppCompatActivity {

    private static final String SHOW_FRIENDS_OF_USER = "SHOW_FRIENDS_OF_USER";
    private static final String USER_TO_DISPLAY = "USER_TO_DISPLAY";
    private static final String TAG = "TAG";
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference usersRef;
    private DatabaseReference usersMapRef;
    String memberId;
    Member member;


    //Set UI Variables
    BottomNavigationView bottomNavigationView;
    SearchView searchView;
    ListView listView;
    Intent intent;
    String showFriends_ofUser;

    private ArrayList<UserListRow> userRowsList = new ArrayList<>();
    ArrayList<String> userIdlist = new ArrayList<>();

    UsersListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        intent = getIntent();
        showFriends_ofUser = intent.getStringExtra("SHOW_FRIENDS_OF_USER");


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = mFirebaseDatabase.getReference().child("users");
        usersMapRef = mFirebaseDatabase.getReference().child("usersMap");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        memberId = user.getUid();
        member = new Member();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // go to profile activity and pass to that activity witch user as been selected and is ID
                // over there show the profile according to the selected user ID
                Intent intent = new Intent(ExploreActivity.this, ProfileActivity.class);
                intent.putExtra(USER_TO_DISPLAY, userRowsList.get(position).getUserID());
                startActivity(intent);

                userRowsList = new ArrayList<>();
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.explore);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        intent = new Intent(ExploreActivity.this, ProfileActivity.class);
                        memberId = user.getUid();
                        intent.putExtra(USER_TO_DISPLAY, memberId);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.inbox:
                        startActivity(new Intent(getApplicationContext(), InboxActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.shop:
                        startActivity(new Intent(getApplicationContext(), ShopActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.explore:
                        if (!showFriends_ofUser.equals("Show friends of all users")) {
                            intent = new Intent(ExploreActivity.this, ExploreActivity.class);
                            intent.putExtra(SHOW_FRIENDS_OF_USER, "Show friends of all users");
                            startActivity(intent);
                        }
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
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this Location is updated
                Log.d(TAG, "onDataChange: Added information to database: \n" +
                        dataSnapshot.getValue());
                if (dataSnapshot.exists()) {
                    if (!showFriends_ofUser.equals("Show friends of all users")) {
                        userIdlist = (ArrayList<String>) dataSnapshot.child(showFriends_ofUser).getValue(Member.class).getFriendList();
                    }
                    member = dataSnapshot.child(memberId).getValue(Member.class);
                } else {
                    // TODO: change else actions
                    Toast.makeText(ExploreActivity.this, "user data does not exist", Toast.LENGTH_LONG).show();
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
                    ArrayList<UserListRow> tempList = new ArrayList<>();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        String s = (String) d.getValue();
                        UserListRow newRow = new UserListRow(s.split("\\n")[0], (String) d.getKey(), s.split("\\n")[1]);
                        tempList.add(newRow);
                        if (!showFriends_ofUser.equals("Show friends of all users")) {
                            for (int i = 0; i < tempList.size(); i++) {
                                if (!userIdlist.contains(tempList.get(i).getUserID())) {
                                    tempList.remove(i);
                                }
                            }
                        }
                        userRowsList = tempList;
                    }
                } else {
                    // TODO: change else actions
                    Toast.makeText(ExploreActivity.this, "user list data does not exist", Toast.LENGTH_LONG).show();
                }
                adapter = new UsersListAdapter(ExploreActivity.this, userRowsList);
                listView.setAdapter(adapter);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapter.getFilter().filter(newText.trim());
                        return false;
                    }

                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }
}

