package com.example.firebase.ui.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import com.example.firebase.R;
import com.example.firebase.adaptor.PostAdaptor;
import com.example.firebase.objects.Post;
import com.example.firebase.objects.PostDB;
import com.example.firebase.objects.User;
import com.example.firebase.util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class postsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        // Inside your activity or fragment
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getPosts();

    }
    private void initPosts(List<Post> posts){
        PostAdaptor adapter = new PostAdaptor(posts,this.getApplicationContext());
        recyclerView.setAdapter(adapter);
    }
    private void getPosts(){
        recyclerView.removeAllViews();
        Query query = FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference("posts").orderByChild("title");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Post> posts = new ArrayList<>();
                query.removeEventListener(this);
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    Post p = postSnapshot.getValue(Post.class);
//                    p.setPostRef(postSnapshot.getRef());
//                    posts.add(p);
                    PostDB pDB = postSnapshot.getValue(PostDB.class);//toDO make sure that works
                    Post p = new Post(pDB);
                    p.setPostRef(postSnapshot.getRef());
                    posts.add(p);
                }
                initPosts(posts);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                util.toast("The read failed: " + databaseError.getCode(),getApplicationContext());
            }
        });
    }
}