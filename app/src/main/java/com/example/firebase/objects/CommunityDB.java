package com.example.firebase.objects;

import static com.example.firebase.ui.activitys.Community.initCommunity;
import static com.example.firebase.ui.activitys.Community.CDB;

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
    public Location GPSlocation;//the root gps GPSlocation
    int radius;//radius of gps GPSlocation in meters
    String name;//the name

    DatabaseReference communityRef;
    public CommunityDB(){}

    public static void toCommunityDB(Context context, DatabaseReference community){
        community
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String json = new Gson().toJson(snapshot.getValue());
                        CommunityDB CDB =  new Gson().fromJson(json, CommunityDB.class);
                        CDB.setCommunityRef(community);
                        CDB.setContext(context);
                        com.example.firebase.ui.activitys.Community.CDB = CDB;
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
    public void setGPSlocation(Location val){
        GPSlocation = val;
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
    public Location getGPSlocation(){
        return GPSlocation;
    }
    public int getRadios(){
        return radius;
    }
    public String getName(){
        return name;
    }
    public void setCommunityRef(DatabaseReference dbrf){communityRef = dbrf;}
    public DatabaseReference getCommunityRef(){return communityRef;}

}
