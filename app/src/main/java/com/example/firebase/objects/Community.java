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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import static com.example.firebase.ui.activitys.Community.initFollowersAndAdmins;
import static com.example.firebase.ui.activitys.Community.initPosts;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class Community {
    public HashMap<String,User> followers;//the followers
    public HashMap<String,User> admins;//the admins
    public List<ImageView> images;
    List<Post> posts;

    public Location GPSlocation;//the root gps gpslocation
    public Supplier<Integer> radius;//radius of gps gpslocation in meters
    public Supplier<String> name;//the name
    public Context context;
    public DatabaseReference communityRef;
    public Community(){}
//   public Community(List<User> followers, List<User> admins, List<ImageView> images, Location gpslocation, Context context){
//        this.admins = admins;
//        this.followers = followers;
//        this.images = images;
//        this.gpslocation = gpslocation;
//        this.context = context;
//   }
    public Community(CommunityDB CDB){
        communityRef = CDB.communityRef;
        name = ()-> CDB.name;
        radius = ()-> CDB.radius;
        GPSlocation = CDB.gpslocation;
        context = CDB.context;
        followers = new HashMap<>();
        admins = new HashMap<>();
        posts = new ArrayList<>();
        getPosts();
        toUsers(CDB.followers, followers);
        toUsers(CDB.admins, admins);
    }
    private void getPosts(){
        //postsLayout.removeAllViews();
//        Query query = FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference("posts").orderByChild("title");
        Query query = communityRef.child("posts").orderByChild("title");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                query.removeEventListener(this);
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    DatabaseReference postRef = FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference("posts").child(postSnapshot.getKey());
                    postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Post p = snapshot.getValue(Post.class);
                            p.setPostRef(snapshot.getRef());
                            posts.add(p);
                            initPosts(posts);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                util.toast("The read failed: " + databaseError.getCode(),context);
            }
        });
    }
    private void toUsers(HashMap<String,String> usersList, HashMap<String,User> Users){
        for (String user : usersList.keySet()) {
            FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users").child(user)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String json = new Gson().toJson(snapshot.getValue());
                            LinkedTreeMap<String, String> newUser = (LinkedTreeMap<String, String>)new Gson().fromJson(json, Object.class);
                            User tempUser = new User(newUser.get("username"),newUser.get("email"),newUser.get("password"));
                            Users.put(user,tempUser);
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
    public void setFollowers(HashMap<String,User> val){
        followers = val;
    }
    public void setAdmins(HashMap<String,User> val){
        admins = val;
    }
    public void setGPSlocation(Location val){
        GPSlocation = val;
    }
    public void setRadius(int val){
        radius = () ->val;
    }
    public void setName(String val){
        name = ()->val;
    }


    public Context getContext(){return context;}
    public HashMap<String, User> getFollowers(){
        return followers;
    }
    public HashMap<String, User> getAdmins(){
        return admins;
    }
    public Location getGPSlocation(){
        return GPSlocation;
    }
    public int getRadius(){
        return radius.get();
    }
    public String getName(){
        return name.get();
    }
}
