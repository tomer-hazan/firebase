package com.example.firebase.ui.activitys;

import static com.example.firebase.ui.fragments.CommunityMapsFragment.setPos;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.firebase.Global;
import com.example.firebase.R;
import com.example.firebase.VerticalSpaceItemDecoration;
import com.example.firebase.adaptor.PostAdaptor;
import com.example.firebase.objects.CommunityDB;
import com.example.firebase.objects.Post;
import com.example.firebase.objects.User;
import com.example.firebase.ui.fragments.CommunityMapsFragment;
import com.example.firebase.util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Community extends AppCompatActivity implements View.OnClickListener {
    static com.example.firebase.objects.Community currentCommunity;
    static TextView comName;
    static RecyclerView postsLayout;
    static LinearLayout followersLayout;
    static LinearLayout adminsLayout;
    static CommunityDB CDB;
    static Button postCreation;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        comName = findViewById(R.id.name);
        postsLayout = findViewById(R.id.postsRecycleView);
        followersLayout = findViewById(R.id.followers);
        adminsLayout = findViewById(R.id.admins);
        postCreation = findViewById(R.id.postCreation);
        postCreation.setOnClickListener(this::onClick);
        CDB = new CommunityDB();

        postsLayout.setLayoutManager(new LinearLayoutManager(this));
        createMapFragment();
        Intent intent = getIntent();
        if(intent.hasExtra("community")) CommunityDB.toCommunityDB(getApplicationContext(), FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference("communities").child(intent.getStringExtra("community")),CDB);
        else CommunityDB.toCommunityDB(getApplicationContext(), FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference("communities").child("rand"),CDB);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(postsLayout.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.divider));
        postsLayout.addItemDecoration(dividerItemDecoration);
    }
    public static void initPosts(){
        PostAdaptor adapter = new PostAdaptor(currentCommunity.getPosts(), currentCommunity.context);
        postsLayout.setAdapter(adapter);
    }

    private void createMapFragment(){
        // Create fragment instance
        CommunityMapsFragment myFragment = new CommunityMapsFragment();

        // Get the FragmentManager and start a transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the content of the container with the fragment
        fragmentTransaction.replace(R.id.fragment_maps, myFragment);

        // Commit the transaction
        fragmentTransaction.commit();
    }
    public static void initCommunity(CommunityDB CDB){
        currentCommunity = new com.example.firebase.objects.Community(CDB);
        comName.setText(currentCommunity.name.get());
        setPos(currentCommunity.GPSlocation,currentCommunity.radius.get());
        Global.community = currentCommunity;

    }
    public static void initFollowersAndAdmins(){
        TextView temp;
        followersLayout.removeAllViews();
        if(currentCommunity.followers.size()!=0){
            for (User follower : currentCommunity.followers.values()) {
                temp =  new TextView(currentCommunity.context);
                temp.setText(follower.username);
                temp.setGravity(View.TEXT_ALIGNMENT_CENTER);
                followersLayout.addView(temp);
            }
        }
        adminsLayout.removeAllViews();
        if(currentCommunity.admins.size()!=0){
            for (User admin : currentCommunity.admins.values()) {
                temp =  new TextView(currentCommunity.context);
                temp.setText(admin.username);
                adminsLayout.addView(temp);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent sendIntent = new Intent(getApplicationContext(), PostsCreation.class);
        startActivity(sendIntent);
    }
}