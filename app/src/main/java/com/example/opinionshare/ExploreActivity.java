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

    private ArrayList<String> userNamelist = new ArrayList<>();
    private ArrayList<String> userIdlist = new ArrayList<>();
    private ArrayList<String> usersProfilePhoto = new ArrayList<>();

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

        UsersListAdapter adapter = new UsersListAdapter(ExploreActivity.this, userIdlist, userNamelist, usersProfilePhoto);
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // go to profile activity and pass to that activity witch user as been selected and is ID
                // over there show the profile according to the selected user ID
                Intent intent = new Intent(ExploreActivity.this, ProfileActivity.class);
                intent.putExtra(USER_TO_DISPLAY, userIdlist.get(position));
                startActivity(intent);

                userNamelist = new ArrayList<>();
                usersProfilePhoto = new ArrayList<>();
                userIdlist = new ArrayList<>();
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

    class UsersListAdapter extends ArrayAdapter<String> implements Filterable {
        Context context;
        ArrayList<String> rUsersProfileImageView;
        ArrayList<String> rUserNameList;
        ArrayList<String> rUserIdList;
        ArrayList<String> mUsersProfileImageView;
        ArrayList<String> mUserNameList;
        ArrayList<String> mUserIdList;
        CustomFilter cs;

        UsersListAdapter(Context c, ArrayList<String> userIdlist, ArrayList<String> userNamelist, ArrayList<String> usersProfilePhoto) {
            super(c, R.layout.user_list_display, R.id.UserNameTextView1, userNamelist);
            this.context = c;
            this.rUsersProfileImageView = usersProfilePhoto;
            this.rUserIdList = userIdlist;
            this.rUserNameList = userNamelist;
            this.mUsersProfileImageView = usersProfilePhoto;
            this.mUserIdList = userIdlist;
            this.mUserNameList = userNamelist;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View user_list_display = layoutInflater.inflate(R.layout.user_list_display, parent, false);
            CircleImageView userProfileImageView = user_list_display.findViewById(R.id.UserProfileImageView1);
            TextView userNameTextView = user_list_display.findViewById(R.id.UserNameTextView1);

            String rName = rUserNameList.get(position);
            userNameTextView.setText(rName);
            Picasso.get().load(rUsersProfileImageView.get(position)).into(userProfileImageView);
            return user_list_display;
        }

        @Override
        public Filter getFilter() {
            if (cs == null) {
                cs = new CustomFilter();
            }
            return cs;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return rUserNameList.size();
        }

        class CustomFilter extends Filter {
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                rUserNameList.clear();
                rUsersProfileImageView.clear();
                rUserIdList.clear();
                ArrayList<String> filters = (ArrayList<String>) filterResults.values;
                for (int i = 0; i < filterResults.count; i++) {
                    String[] s = filters.get(i).split("\\n");
                    rUserNameList.add(s[0]);
                    rUsersProfileImageView.add(s[1]);
                    rUserIdList.add(s[2]);
                }
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constrains) {
                FilterResults results = new FilterResults();
                ArrayList<String> filters = new ArrayList<>();
                if (constrains != null && constrains.length() > 0) {
                    constrains = constrains.toString().toUpperCase();
                }else{
                    constrains = "";
                }
                for (int i = 0; i < mUserNameList.size(); i++) {
                    if (mUserNameList.get(i).toUpperCase().contains(constrains)) {
                        filters.add(mUserNameList.get(i) + "\n" + mUsersProfileImageView.get(i) + "\n" + mUserIdList.get(i));
                    }
                }
                results.count = filters.size();
                results.values = filters;
                return results;
            }
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
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        if (showFriends_ofUser.equals("Show friends of all users")) {
                            String s = (String) d.getValue();
                            userNamelist.add(s.split("\\n")[0]);
                            usersProfilePhoto.add(s.split("\\n")[1]);
                            userIdlist.add((String) d.getKey());
                        } else {
                            if (userIdlist.contains((String) d.getKey())) {
                                String s = (String) d.getValue();
                                userNamelist.add(s.split("\\n")[0]);
                                usersProfilePhoto.add(s.split("\\n")[1]);
                            }
                        }
                    }
                } else {
                    // TODO: change else actions
                    Toast.makeText(ExploreActivity.this, "user list data does not exist", Toast.LENGTH_LONG).show();
                }
                UsersListAdapter adapter = new UsersListAdapter(ExploreActivity.this, userIdlist, userNamelist, usersProfilePhoto);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }
}

