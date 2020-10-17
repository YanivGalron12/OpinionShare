package com.example.opinionshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ItemsList extends AppCompatActivity {
    ListView lv;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private DatabaseReference forSaleListRef;

    private static final String USER_TO_DISPLAY = "USER_TO_DISPLAY";

    private static final String ITEMS_TO_TAG = "ITEMS_TO_TAG";
    private static final String POST_LOCATION = "POST_LOCATION";
    private static final String TAG = "TAG";
    private static final String ORIGIN = "ORIGIN";

    String postUri, postType,postCategory,postCaption,postDescription;
    String memberId;
    Member member;
    ArrayList<Item> ItemsInPost = new ArrayList<>();
    ArrayList<String> Typelist = new ArrayList<>();
    ArrayList<String> Companylist=new ArrayList<>();
    ArrayList<Item> Itemlist=new ArrayList<>();
    ArrayList<PostForSale> forSaleList=new ArrayList<>();
    SearchView searchView;
    ListView listView;
    Button finish_button;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    private DatabaseReference usersRef;
    Posts post = new Posts();
    List<Posts> PostList = new ArrayList<Posts>();
    ArrayList<String> descriptionList = new ArrayList<>();
    ItemListAdapter adapter;
    ArrayList<String> ItemsUri=new ArrayList<>();
    ArrayList<String> ItemsCaption=new ArrayList<>();
    ArrayList<String> ItemsDescription=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Intent intent = getIntent();
//        postType = intent.getStringExtra("POST_TYPE");
//        postUri = intent.getStringExtra("URI_SELECTED");
//        postCaption=intent.getStringExtra("POST_CAPTION");
//        postCategory=intent.getStringExtra("POST_CATEGORY");
//        postDescription=intent.getStringExtra("POST_DESCRIPTION");

        setContentView(R.layout.activity_items_list);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        usersRef = mFirebaseDatabase.getReference().child("users");
        forSaleListRef = mRef.child("ForSaleList");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        memberId = user.getUid();
        member = new Member();
        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);
        finish_button=findViewById(R.id.finish);

        adapter = new ItemListAdapter(getApplicationContext(),R.layout.items_for_sale_listview,Itemlist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // go to profile activity and pass to that activity witch user as been selected and is ID
                // over there show the profile according to the selected user ID
                ItemsInPost.add(Itemlist.get(position));
                ItemsUri.add(Itemlist.get(position).getImage());
                ItemsCaption.add(Itemlist.get(position).getType());
                ItemsDescription.add(Itemlist.get(position).getCompany());
                descriptionList.add(Itemlist.get(position).getType());



            }
        });
        finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(ItemsList.this, postType + " Upload", Toast.LENGTH_SHORT).show();
//
//                post.setCreationDate(java.time.LocalDate.now().toString());
//                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//                post.setTimeStamp(Long.toString(timestamp.getTime()));
//                post.setPostType(postType);
//                post.setCaption(postCaption);
//                post.setCategory(postCategory);
//                post.setDescription(postDescription);
//                post.setPostUri(postUri);
//                post.setItems(ItemsInPost);//TODO: change link to be the link to the storage and not internal location
//                post.setLikeCounter(0);
//                if (member.getPostList()!=null) {
//                    PostList  = member.getPostList();
//                }
//                PostList.add(post);
//                PostForSale postForSale = new PostForSale();
//                postForSale.setOwnerId(memberId);
//                postForSale.setPost(post);
//                postForSale.setTimeStamp(post.getTimeStamp());
//                member.setPostList(PostList);
//                addPostToDatabase(member);
//                if(post.getForSale()){
//                    forSaleList.add(postForSale);
//                    forSaleListRef.setValue(forSaleList);
//                }
//
//                // Notification for all friends of logged in user
//                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//                finish();
                Intent intent = new Intent(ItemsList.this, UploadPostActivity.class);
                intent.putExtra("ITEMS_URI",ItemsUri);
                intent.putExtra("ITEMS_CAPTION",ItemsCaption);
                intent.putExtra("ITEMS_DESCRIPTION",ItemsDescription);
                intent.putExtra(ORIGIN,"ItemsList");
                setResult(UploadPostActivity.RESULT_OK,intent);
                finish();
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
                    Toast.makeText(ItemsList.this, "data does not exist", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        forSaleListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this Location is updated
                Log.d(TAG, "onDataChange: Added information to database: \n" +
                        dataSnapshot.getValue());
                if (dataSnapshot.exists()) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        PostForSale postForSale=d.getValue(PostForSale.class);
                        Item item =new Item();
                        item.setCompany("test company");
                        item.setType("test type");
                        item.setImage(postForSale.getPost().getPostUri());
                        Itemlist.add(item);




                        }
                    }

                 else {
                    Toast.makeText(ItemsList.this, "nothing for sale", Toast.LENGTH_LONG).show();
                }
                listView.setAdapter(adapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});
    }
//    private void addPostToDatabase(Member member) {
//        String memberId = member.getUserId();
//        usersRef.child(memberId).setValue(member);
//    }

}
