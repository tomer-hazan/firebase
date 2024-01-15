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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import static com.example.firebase.ui.activitys.Community.initFollowersAndAdmins;
import static com.example.firebase.ui.activitys.Community.initImages;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class Community {
    public List<User> followers;//the followers
    public List<User> admins;//the admins
    public List<ImageView> images;
    public Location location;//the root gps location
    public Supplier<Integer> radius;//radius of gps location in meters
    public Supplier<String> name;//the name
    public Context context;
    public DatabaseReference communityRef;
    public Community(){}
//   public Community(List<User> followers, List<User> admins, List<ImageView> images, Location location, Context context){
//        this.admins = admins;
//        this.followers = followers;
//        this.images = images;
//        this.location = location;
//        this.context = context;
//   }
    public Community(CommunityDB CDB){
        communityRef = CDB.communityRef;
        name = CDB.name;
        radius = CDB.radius;
        location = CDB.location;
        context = CDB.context;
        followers = new ArrayList<>();
        admins = new ArrayList<>();
        images = new ArrayList<>();
        initImages();
        toUsers(CDB.followers, followers);
        toUsers(CDB.admins, admins);
    }
    private List<ImageView> initImages(){//@toDO make this work
        //String id = communityRef.getRoot();
        StorageReference imageRef = FirebaseStorage.getInstance("gs://th-grade-34080.appspot.com").getReference().child(communityRef.getKey()).child("/images/");
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
    private void toUsers(HashMap<String,String> usersList, List<User> Users){
        for (String user : usersList.keySet()) {
            FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users").child(user)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String json = new Gson().toJson(snapshot.getValue());
                            LinkedTreeMap<String, String> newUser = (LinkedTreeMap<String, String>)new Gson().fromJson(json, Object.class);
                            User tempUser = new User(newUser.get("username"),newUser.get("email"),newUser.get("password"));
                            Users.add(tempUser);
                            initFollowersAndAdmins();//toDO this func runs on every user (follower or admin) make it run only when all the users arrived
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            System.out.println("tenp");
                        }
                    });
        }
    }

    public void setContext(Context context){this.context=context;}
    public void setFollowers(HashMap<String,String> val){//toDO
        followers = val;
    }
    public void setAdmins(HashMap<String,String> val){//toDO
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
    public List<User> getFollowers(){
        return followers;
    }
    public List<User> getAdmins(){
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
