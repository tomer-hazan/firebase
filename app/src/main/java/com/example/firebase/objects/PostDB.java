package com.example.firebase.objects;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;
import static com.example.firebase.ui.activitys.Community.initCommunity;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.firebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostDB {
    String poster;
    String title;
    String content;

    DatabaseReference postRef;
    Context context;
    public PostDB(String poster,String title,String content){
       this.title = title;
       this.content=content;
       this.poster = poster;

    }
    public PostDB(){}


    public void toPostDB(Context context, DatabaseReference postRef){
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
