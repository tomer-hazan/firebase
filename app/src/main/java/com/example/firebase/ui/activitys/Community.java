package com.example.firebase.ui.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.firebase.R;
import com.example.firebase.objects.CommunityDB;
import com.example.firebase.objects.User;
import com.example.firebase.ui.fragments.CommunityMapsFragment;
import com.example.firebase.ui.fragments.MapsFragmentPositionTracker;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Community extends AppCompatActivity {
    static com.example.firebase.objects.Community currentCommunity;
    static TextView comName;
    static LinearLayout imagesLayout;
    static LinearLayout followersLayout;
    static LinearLayout adminsLayout;
    static CommunityDB CDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        comName = findViewById(R.id.name);
        imagesLayout = findViewById(R.id.ImagesLayout);
        followersLayout = findViewById(R.id.followers);
        adminsLayout = findViewById(R.id.admins);

        createMapFragment();
        CDB = new CommunityDB(getApplicationContext(), FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference("communities").child("rand"));

        System.out.println("temp");
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
    public static void initCommunity(){
        currentCommunity = new com.example.firebase.objects.Community(CDB);
        comName.setText(currentCommunity.name.get());

    }
    public static void initImages(){
        imagesLayout.removeAllViews();
        for (ImageView image : currentCommunity.images) {
            imagesLayout.addView(image);
        }
    }
    public static void initFollowersAndAdmins(){
        TextView temp;
        followersLayout.removeAllViews();
        if(currentCommunity.followers.size()!=0){
            for (User follower : currentCommunity.followers) {
                temp =  new TextView(currentCommunity.context);
                temp.setText(follower.username);
                followersLayout.addView(temp);
            }
        }
        adminsLayout.removeAllViews();
        if(currentCommunity.admins.size()!=0){
            for (User admin : currentCommunity.admins) {
                temp =  new TextView(currentCommunity.context);
                temp.setText(admin.username);
                adminsLayout.addView(temp);
            }
        }
    }
}