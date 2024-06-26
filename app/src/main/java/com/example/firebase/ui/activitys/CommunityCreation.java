package com.example.firebase.ui.activitys;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.firebase.Global;
import com.example.firebase.R;
import com.example.firebase.objects.CommunityDB;
import com.example.firebase.objects.Location;
import com.example.firebase.objects.User;
import com.example.firebase.ui.fragments.CommunityCreationMapsFragment;
import com.example.firebase.ui.fragments.CommunityMapsFragment;
import com.example.firebase.util;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.slider.Slider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;

public class CommunityCreation extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    static EditText name;
    static TextView longitude;
    static TextView latitude;
    static EditText radius;
    static Button submit;
    Button homeButton;
    static CommunityDB CDB;
    DatabaseReference myRef;
    CommunityCreationMapsFragment myFragment;
    String userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_creation);
        name = findViewById(R.id.name);
        longitude = findViewById(R.id.longitude);
        latitude = findViewById(R.id.latitude);
        radius = findViewById(R.id.radius);
        submit = findViewById(R.id.submit);
        homeButton = findViewById(R.id.home);
        homeButton.setOnClickListener(this::onClick);
        submit.setBackgroundColor(Color.GRAY);
        myRef = FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("communities");
        createMapFragment();
        Intent intent = getIntent();
        userRef = Global.userRef.getKey();
        radius.addTextChangedListener(this);


    }
    private void createMapFragment(){
        // Create fragment instance
        myFragment = new CommunityCreationMapsFragment(this::onClick);

        // Get the FragmentManager and start a transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the content of the container with the fragment
        fragmentTransaction.replace(R.id.fragment_maps, myFragment);

        // Commit the transaction
        fragmentTransaction.commit();
    }

    public static void setGPSCords(double lat, double lon, View.OnClickListener onClickListener){
        latitude.setText(String.valueOf(lat));
        longitude.setText(String.valueOf(lon));
        submit.setOnClickListener(onClickListener);
        submit.setBackgroundColor(Color.GREEN);
    }
    public DatabaseReference uploadCommunity(){
        HashMap<String,String> followers = new HashMap<>();
        HashMap<String,String> admins = new HashMap<>();
        admins.put(userRef,"");
        followers.put(userRef,"");
        CDB = new CommunityDB(name.getText().toString(),followers,admins,new Location(Double.valueOf( longitude.getText().toString()),Double.valueOf( latitude.getText().toString())),Integer.valueOf( radius.getText().toString()));
        DatabaseReference communityRef = myRef.push();
        communityRef.setValue(CDB);
        try {
            DatabaseReference userToCommunitie = FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users_to_communities/"+userRef);
            userToCommunitie.child("owned").child(communityRef.getKey()).setValue("");
            userToCommunitie.child("followed").child(communityRef.getKey()).setValue("");
        }catch (Exception e){}
        toast("uploaded successfully");
        return communityRef;
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==submit.getId()) {
            if (!(util.isCorrectInput(name.getText().toString(), getApplicationContext()) && util.isCorrectInput(radius.getText().toString(), getApplicationContext()) && util.isCorrectInput(longitude.getText().toString(), getApplicationContext()) && util.isCorrectInput(latitude.getText().toString(), getApplicationContext())))
                return;
            radius.setTextColor(Color.BLACK);
            name.setTextColor(Color.BLACK);
            try {
                Integer.valueOf(radius.getText().toString());
            } catch (Exception e) {
                radius.setTextColor(Color.RED);
                toast("invalid community radius");
                return;
            }
            Query query = myRef.orderByChild("name").equalTo(name.getText().toString());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    query.removeEventListener(this);
                    if (dataSnapshot.hasChildren()) {
                        toast("this community name already exists, please choose another one");
                        name.setTextColor(Color.RED);
                    } else {
                        uploadCommunity();
                        Intent sendIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(sendIntent);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    toast("The read failed: " + databaseError.getCode());
                }
            });
        }else if (v.getId()==homeButton.getId()) {
            Intent sendIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(sendIntent);
            finish();
        }
    }
    private void toast(String text){
        Toast t = new Toast(getApplicationContext());
        t.setText(text);
        t.show();
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        int r;
        double lon;
        double lat;
        try{
            r =Integer.valueOf( s.toString());
            lon =Double.valueOf( longitude.getText().toString());
            lat = Double.valueOf(latitude.getText().toString());
        }catch (Exception e){
            return;
        }
        myFragment.drawCircle(new LatLng(lat,lon),r);
        myFragment.zoom(r);

    }

}