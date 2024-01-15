package com.example.firebase.objects;

import static com.example.firebase.ui.activitys.Community.initCommunity;
import static com.example.firebase.ui.fragments.CommunityMapsFragment.setPos;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.firebase.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class CommunityDB {
    HashMap<String,String> followers;//the followers
    //List<String> images;
    HashMap<String,String> admins;//the admins
    Context context;
    public Location location;//the root gps location
    Supplier<Integer> radius;//radius of gps location in meters
    Supplier<String> name;//the name

    DatabaseReference communityRef;
    public CommunityDB(){}
    public CommunityDB(Context context, DatabaseReference community){
        communityRef = community;
        this.context = context;
        //name = communityRef.child("name").get;
        communityRef
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap<String, Object> map = (HashMap<String, Object>)snapshot.getValue();
                        name = () ->(String)map.get("name");
                        followers = (HashMap<String,String>)map.get("followers");
                        admins = (HashMap<String,String>)map.get("admins");
                        radius = () ->Math.toIntExact((Long) map.get("radius"));
                        HashMap<String,Double> temoLocation = (HashMap<String,Double>)map.get("GPSlocation");
                        location = new Location(temoLocation.get("longitude"),temoLocation.get("latitude"));
                        initCommunity();
                        setPos(location,radius.get());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
    public void setContext(Context context){this.context=context;}
    public void setFollowers(HashMap<String,String> val){
        followers = val;
    }
    public void setAdmins(HashMap<String,String> val){
        admins = val;
    }
    public void setLocation(Location val){
        location = val;
    }
    public void setRadius(int val){
        radius = () ->val;
    }
    public void setName(String val){
        name = ()->val;
    }


    public Context getContext(){return context;}
    public HashMap<String,String> getFollowers(){
        return followers;
    }
    public HashMap<String,String> getAdmins(){
        return admins;
    }
    public Location getLocation(){
        return location;
    }
    public int getRadios(){
        return radius.get();
    }
    public String getName(){
        return name.get();
    }

}
