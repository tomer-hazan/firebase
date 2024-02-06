package com.example.firebase.objects;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class Post {
    User poster;
    public String title;
    public String content;

    DatabaseReference postRef;
    Context context;
    public Post(PostDB postDB){
       this.title = postDB.getTitle();
       this.content=postDB.getContent();
       initPoster(postDB.poster,this);
       this.poster = poster;
    }
    public Post(){}

    private void initPoster(String posterName,Post post){
        postRef.getRoot().child("users").child(posterName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String json = new Gson().toJson(snapshot.getValue());
                        User poster =  new Gson().fromJson(json, User.class);
                        post.setPoster(poster);
                        //initCommunity(PDB);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
    public void setContext(Context context){this.context=context;}
    public void setPoster(User val){
        poster = val;
    }
    public void setContent(String val){
        content = val;
    }
    public void setTitle(String val){
        title =val;
    }


    public Context getContext(){return context;}
    public User getPoster(){
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
