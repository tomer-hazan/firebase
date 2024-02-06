package com.example.firebase.objects;

import static com.example.firebase.ui.activitys.Community.initCommunity;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;

public class PostDB {
    String poster;
    public String title;
    public String content;

    DatabaseReference postRef;
    Context context;
    public PostDB(String poster,String title,String content){
       this.title = title;
       this.content=content;
       this.poster = poster;
    }
    public PostDB(){}

    public static void toPostDB(Context context, DatabaseReference postRef){
        postRef
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String json = new Gson().toJson(snapshot.getValue());
                        PostDB PDB =  new Gson().fromJson(json, PostDB.class);
                        PDB.setPostRef(postRef);
                        PDB.setContext(context);
                        //initCommunity(PDB);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
    public void setContext(Context context){this.context=context;}
    public void setPoster(String val){
        poster = val;
    }
    public void setContent(String val){
        content = val;
    }
    public void setTitle(String val){
        title =val;
    }


    public Context getContext(){return context;}
    public String getPoster(){
        return poster;
    }
    public String getContent(){
        return content;
    }
    public String getTitle(){
        return title;
    }
    public void setPostRef(DatabaseReference dbrf){postRef = dbrf;}
    public DatabaseReference getPostRef(){return postRef;}

}
