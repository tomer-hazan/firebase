package com.example.firebase.ui.activitys;

import static com.example.firebase.ui.fragments.CommunityMapsFragment.setPos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.firebase.Global;
import com.example.firebase.R;
import com.example.firebase.adaptor.PostAdaptor;
import com.example.firebase.objects.CommunityDB;
import com.example.firebase.objects.User;
import com.example.firebase.ui.fragments.CommunityMapsFragment;
import com.example.firebase.util;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Community extends AppCompatActivity implements View.OnClickListener {
    static com.example.firebase.objects.Community currentCommunity;
    static TextView comName;
    static RecyclerView postsLayout;
    static LinearLayout followersLayout;
    static LinearLayout adminsLayout;
    static CommunityDB CDB;
    static Button postCreation;
    static Button followButton;
    Button homeButton;
    static ConstraintLayout activityLayout;
    static DatabaseReference currentCommunityRef;
    static boolean isFollowing=false;
    static boolean isOwner=false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        comName = findViewById(R.id.name);
        activityLayout = findViewById(R.id.layoutID);
        postsLayout = findViewById(R.id.postsRecycleView);
        followersLayout = findViewById(R.id.followers);
        adminsLayout = findViewById(R.id.admins);
        postCreation = findViewById(R.id.postCreation);
        followButton = findViewById(R.id.followButton);
        homeButton = findViewById(R.id.home);
        postCreation.setOnClickListener(this::onClick);
        followButton.setOnClickListener(this::onClick);
        homeButton.setOnClickListener(this::onClick);
        CDB = new CommunityDB();
        isFollowing=false;
        isOwner=false;

        postsLayout.setLayoutManager(new LinearLayoutManager(this));
        createMapFragment();
        Intent intent = getIntent();
        if(intent.hasExtra("community")) currentCommunityRef = FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference("communities").child(intent.getStringExtra("community"));
        else currentCommunityRef = FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference("communities").child("amits community");
        CommunityDB.createCommunityDB(getApplicationContext(), currentCommunityRef,CDB);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(postsLayout.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.divider));
        postsLayout.addItemDecoration(dividerItemDecoration);
    }
    public static void initPosts(){
        PostAdaptor adapter = new PostAdaptor(currentCommunity.getPosts(), currentCommunity.context,isOwner);
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
    private static void renderFollowButton(){
        if(isFollowing){
            followButton.setBackgroundColor(Color.GRAY);
            followButton.setText("unfollow");
        }else{
            followButton.setBackgroundColor(Color.RED);
            followButton.setText("follow");
        }
    }
    public static void initFollowersAndAdmins(){
        TextView tv;
        followersLayout.removeAllViews();
        String[] keys = currentCommunity.followers.keySet().toArray(new String[0]);
        User[] users = currentCommunity.followers.values().toArray(new User[0]);
        for(int i=0;i<keys.length;i++){
            tv =  new TextView(currentCommunity.context);
            tv.setText(users[i].username);
            tv.setTextColor(Color.GRAY);
            tv.setGravity(View.TEXT_ALIGNMENT_CENTER);
            followersLayout.addView(tv);
            if(keys[i].equals(Global.userRef.getKey())){
                isFollowing=true;
                renderFollowButton();
            }
        }

        adminsLayout.removeAllViews();
        keys = currentCommunity.admins.keySet().toArray(new String[0]);
        users = currentCommunity.admins.values().toArray(new User[0]);
        for(int i=0;i<keys.length;i++){
            tv =  new TextView(currentCommunity.context);
            tv.setText(users[i].username);
            tv.setTextColor(Color.GRAY);
            tv.setGravity(View.TEXT_ALIGNMENT_CENTER);
            adminsLayout.addView(tv);
            if(keys[i].equals(Global.userRef.getKey())){
                isOwner=true;
                activityLayout.removeView(followButton);
            }
        }
        if(isOwner&&postsLayout.getAdapter()!=null){
            ((PostAdaptor)postsLayout.getAdapter()).setOwnerTrue();
            util.toast("owner", currentCommunity.context);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==postCreation.getId()){
            Intent sendIntent = new Intent(getApplicationContext(), PostsCreation.class);
            startActivity(sendIntent);
        } else if (v.getId()==followButton.getId()) {
            if(isFollowing){
                currentCommunityRef.child("followers").child(Global.userRef.getKey()).removeValue();
                FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("/users_to_communities").child(Global.userRef.getKey()).child("followed").child(currentCommunityRef.getKey()).removeValue();
                for (int i=0;i<followersLayout.getChildCount();i++){
                    if(((TextView)followersLayout.getChildAt(i)).getText().equals(Global.user.username))followersLayout.removeViewAt(i);
                }
            }else {
                currentCommunityRef.child("followers").child(Global.userRef.getKey()).setValue("");
                FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("/users_to_communities").child(Global.userRef.getKey()).child("followed").child(currentCommunityRef.getKey()).setValue("");
                TextView temp =  new TextView(currentCommunity.context);
                temp.setText(Global.user.username);
                temp.setGravity(View.TEXT_ALIGNMENT_CENTER);
                followersLayout.addView(temp);
            }
            isFollowing=!isFollowing;
            renderFollowButton();
        } else if (v.getId()==homeButton.getId()) {
            Intent sendIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(sendIntent);
            finish();
        }

    }
}