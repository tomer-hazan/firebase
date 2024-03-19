package com.example.firebase.ui.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.firebase.Global;
import com.example.firebase.R;
import com.example.firebase.objects.CommunityDB;
import com.example.firebase.objects.Location;
import com.example.firebase.objects.PostDB;
import com.example.firebase.ui.fragments.CommunityCreationMapsFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class PostsCreation extends AppCompatActivity implements View.OnClickListener {//toDO allow in app adding of images to posts
    static EditText title;
    static EditText content;
    static Button submit;
    static PostDB PDB;
    DatabaseReference communityRef;
    DatabaseReference postsRef;
    String userRef;
    boolean sendToCommunityCreation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_creation);
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        submit = findViewById(R.id.submit);
        submit.setBackgroundColor(Color.GREEN);
        submit.setOnClickListener(this::onClick);
        communityRef=Global.community.communityRef.child("posts");
        postsRef = FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("posts");
        Intent intent = getIntent();
        userRef = intent.getStringExtra("user");
    }

    public DatabaseReference uploadPost(){
        PDB = new PostDB(Global.userRef.getKey(),title.getText().toString(),content.getText().toString());
        DatabaseReference postRef = postsRef.push();
        postRef.setValue(PDB);
        communityRef.child(postRef.getKey()).setValue("");
        Global.userRef.child("posts").child(postRef.getKey()).setValue("");
        toast("uploaded successfully");
        return postRef;
    }

    @Override
    public void onClick(View v) {
        uploadPost();
        toast("uploaded successfully");
        Intent sendIntent = new Intent(getApplicationContext(), Community.class);
        startActivity(sendIntent);
    }
    private void toast(String text){
        Toast t = new Toast(getApplicationContext());
        t.setText(text);
        t.show();
    }
}