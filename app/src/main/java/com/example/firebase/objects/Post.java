package com.example.firebase.objects;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.firebase.R;
import com.example.firebase.util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.example.firebase.ui.activitys.Community;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Post {
    String poster;
    String community;
    public String title;
    public String content;
    public Context context;
    List<ImageView> images;

    DatabaseReference postRef;
    public Post(PostDB postDB){
       this.title = postDB.getTitle();
       this.content=postDB.getContent();
       this.poster=postDB.poster;
       context = postDB.context;
       this.images = new ArrayList<>();
        initImages(postDB.images);

    }
    private void initImages(List<String> imagesRef){
        if(imagesRef==null)return;
        for (String ref: imagesRef) {
            StorageReference imageRef = FirebaseStorage.getInstance("gs://th-grade-34080.appspot.com").getReference(ref);
            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    ImageView tempImg = new ImageView(context);
                    Picasso.get().load(uri).into(tempImg);
                    images.add(tempImg);
                    util.toast("the image has been inited",context);
                    Community.initPosts();
                }
            });
        }
    }
    public Post(){}
    public void setPoster(String val){
        poster = val;
    }
    public void setCommunity(String val){content=val;}
    public void setContent(String val){
        content = val;
    }
    public void setTitle(String val){
        title =val;
    }

    public String getPoster(){
        return poster;
    }
    public String getCommunity(){return community;}
    public String getContent(){
        return content;
    }
    public String getTitle(){
        return title;
    }
    public void setPostRef(DatabaseReference dbrf){postRef = dbrf;}
    public List<ImageView> getImages(){return images;}
    public DatabaseReference getPostRef(){return postRef;}

}
