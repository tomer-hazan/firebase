package com.example.firebase.objects;

import static com.example.firebase.ui.activitys.Community.initCommunity;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.HashMap;

public class CommunityDB {
    HashMap<String,String> followers;//the followers
    //List<String> images;
    HashMap<String,String> admins;//the admins
    Context context;
    Location gpslocation;//the root gps gpslocation
    int radius;//radius of gps gpslocation in meters
    String name;//the name

    DatabaseReference communityRef;
    public CommunityDB(String name,HashMap<String,String> followers,HashMap<String,String> admins,Location GPSlocation,int radius){
        this.name =name;
        this.followers=followers;
        this.admins = admins;
        this.gpslocation = GPSlocation;
        this.radius = radius;
    }
    public CommunityDB(){}

    public static void createCommunityDB(Context context, DatabaseReference community, CommunityDB CDB){
        community
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String json = new Gson().toJson(snapshot.getValue());
                        CommunityDB temp =  new Gson().fromJson(json, CommunityDB.class);//toDO this line can crash because of lack of 0 followers, fix this
                        try {
                            CDB.clone(temp);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        CDB.setCommunityRef(community);
                        CDB.setContext(context);
                        //com.example.firebase.ui.activitys.Community.CDB = CDB;
                        initCommunity(CDB);
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
    public void setGpslocation(Location val){
        gpslocation = val;
    }
    public void setRadius(int val){
        radius = val;
    }
    public void setName(String val){
        name =val;
    }


    public Context getContext(){return context;}
    public HashMap<String,String> getFollowers(){
        return followers;
    }
    public HashMap<String,String> getAdmins(){
        return admins;
    }
    public Location getGpslocation(){
        return gpslocation;
    }
    public int getRadius(){
        return radius;
    }
    public String getName(){
        return name;
    }
    public void setCommunityRef(DatabaseReference dbrf){communityRef = dbrf;}
    public DatabaseReference getCommunityRef(){return communityRef;}
    public void clone(CommunityDB other) throws IllegalAccessException {
        for (Field f:other.getClass().getDeclaredFields()) {
            f.set(this,f.get(other));
        }
    }

}
