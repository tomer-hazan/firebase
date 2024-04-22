package com.example.firebase.objects;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import static com.example.firebase.ui.activitys.Community.initFollowersAndAdmins;
import static com.example.firebase.ui.activitys.Community.initPosts;

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
import java.util.function.Supplier;

import kotlin.Suppress;

public class Post {
    private User poster;
    public String title;
    public String content;
    public Context context;
    List<ImageView> images;
    private DatabaseReference communityRef;
    private Supplier<Boolean> isImageInit;
    private Supplier<Boolean> isPosterInit;
    private int initedImages;
    private int amountOfImages;

    DatabaseReference postRef;
    public Post(PostDB postDB){
       this.title = postDB.getTitle();
       this.content=postDB.getContent();
       context = postDB.context;
       this.postRef=postDB.postRef;
       this.images = new ArrayList<>();
        initImages(postDB.images);
        initPoster(postDB.poster);
        isImageInit=()->false;
        isPosterInit=()->false;
        amountOfImages=0;
        initedImages=0;
    }
    public Post(PostDB postDB,DatabaseReference communityRef){
        this.title = postDB.getTitle();
        this.content=postDB.getContent();
        context = postDB.context;
        this.postRef=postDB.postRef;
        this.communityRef=communityRef;
        this.images = new ArrayList<>();
        isImageInit=()->false;
        isPosterInit=()->false;
        initImages(postDB.images);
        initPoster(postDB.poster);
    }
    private void initImages(List<String> imagesRef){
        if(imagesRef==null){
            isImageInit=()->true;
            if(isImageInit.get()&&isPosterInit.get())Community.initPosts();
            return;
        }
        amountOfImages = imagesRef.size();
        for (String ref: imagesRef) {
            StorageReference imageRef = FirebaseStorage.getInstance("gs://th-grade-34080.appspot.com").getReference(ref);
            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    ImageView tempImg = new ImageView(context);
                    Picasso.get().load(uri).into(tempImg);
                    images.add(tempImg);
                    util.toast("the image has been inited",context);
                    initedImages++;
                    if(initedImages==amountOfImages)isImageInit=()->true;
                    if(isImageInit.get()&&isPosterInit.get())Community.initPosts();
                }
            });
        }
    }
    public Post(){}
    public void setPoster(String val){
        initPoster(val);
    }
    public void setCommunity(String val){content=val;}
    public void setContent(String val){
        content = val;
    }
    public void setTitle(String val){
        title =val;
    }

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
    public List<ImageView> getImages(){return images;}
    public DatabaseReference getPostRef(){return postRef;}
    public void selfDestruction(){
        String postsKey = postRef.getKey();
        postRef.removeValue();
        postRef.getRoot().child("users").child(poster.getRefKey()).child("posts").child(postsKey).removeValue();
        if(communityRef!=null)communityRef.child("posts").child(postsKey).removeValue();
    }

    private void initPoster(String userKey){
        FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users").child(userKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String json = new Gson().toJson(snapshot.getValue());
                        User newUser = new Gson().fromJson(json, User.class);
                        poster=newUser;
                        poster.setRefKey(userKey);
                        isPosterInit=()->true;
                        if(isImageInit.get()&&isPosterInit.get())Community.initPosts();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

    }
}
