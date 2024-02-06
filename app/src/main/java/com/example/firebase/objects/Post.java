package com.example.firebase.objects;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

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
import java.util.List;

public class Post {//toDO make an activity that shows all the posts for testing
    User poster;
    public String title;
    public String content;
    List<ImageView> images;

    DatabaseReference postRef;
    Context context;
    public Post(PostDB postDB){
       this.title = postDB.getTitle();
       this.content=postDB.getContent();
       initPoster(postDB.poster,this);
       images = new ArrayList<>();
       initImages();
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
    private List<ImageView> initImages(){//@toDO make this work
        //String id = communityRef.getRoot();
        StorageReference imageRef = FirebaseStorage.getInstance("gs://th-grade-34080.appspot.com").getReference().child(postRef.getKey()).child("/images/");
        imageRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            images.add( getImage(item));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors that occur during listing
                    }

                });
        //.addOnCompleteListener()

        return null;
    }
    private ImageView getImage(StorageReference imageRef){
        ImageView image = new ImageView(context);
        image.setImageDrawable(getDrawable(context, R.drawable.null_img));
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .into(image);
                //toast("succses");
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("the error1",exception.toString());
                Log.e("the error2",exception.getMessage());
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>(){
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                com.example.firebase.ui.activitys.Community.initImages();
            }
        });
        return image;
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
