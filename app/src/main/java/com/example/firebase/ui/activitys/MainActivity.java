package com.example.firebase.ui.activitys;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;

import com.example.firebase.Global;
import com.example.firebase.R;
import com.example.firebase.objects.CommunityDB;
import com.example.firebase.objects.Location;
import com.example.firebase.objects.User;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsRequest;

import android.hardware.camera2.CameraManager;
import android.location.LocationManager;
import com.google.android.gms.location.LocationRequest;

import android.os.Bundle;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import android.Manifest;

import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private double maxDistance = 500;

    TextView tv;
    Map<String, TextView> communities;
    LinearLayout moreCommunitiesLayout;
    LinearLayout followedCommunitiesLayout;
    LinearLayout ownedCommunitiesLayout;
    private LocationRequest locationRequest;
    Button communityBtn;
    CameraManager cameraManager;
    Boolean haveCamera;
    ActivityResultLauncher<Intent>  cameraActivity;
    private Location location;
    private static final int REQUEST_CODE = 22;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;

    private static final int REQUEST_CHECK_SETTINGS = 10001;
    private static final String[] CAMERA_PERMISSIONS = {
            Manifest.permission.CAMERA,
    };
    private static final String[] GPS_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        communities = new HashMap<>();
        if (!arePermissionsGranted(CAMERA_PERMISSIONS))
            ActivityCompat.requestPermissions(this, CAMERA_PERMISSIONS, REQUEST_CAMERA_PERMISSION);
        if (!arePermissionsGranted(GPS_PERMISSIONS))
            ActivityCompat.requestPermissions(this, GPS_PERMISSIONS, MY_PERMISSIONS_REQUEST_LOCATION);
        tv = findViewById(R.id.text);
        moreCommunitiesLayout = findViewById(R.id.moreCommunitiesLayout);
        ownedCommunitiesLayout = findViewById(R.id.ownedCommunitiesLayout);
        followedCommunitiesLayout = findViewById(R.id.followedCommunitiesLayout);
        communityBtn = findViewById(R.id.community);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        getGPS();
        getUserIfNull();
        communityBtn.setOnClickListener(this::onClick);
        getOtherCommunities();
        getOwnedCommunities();
        getFollowedCommunities();
        //getRecomendedCommunities();


    }
    private void getUserIfNull() {
        if(Global.user==null){
            getUserFromRef();
        }
    }

    private boolean getUserFromRef() {
        Intent intent = getIntent();
        DatabaseReference userRef = Global.userRef;
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String json = new Gson().toJson(snapshot.getValue());
                Global.user = new Gson().fromJson(json, User.class);
                Global.userRef = userRef;
                userRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return (intent != null);
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View view) {
        if (view.getId() == communityBtn.getId()) {
            Intent sendIntent = new Intent(getApplicationContext(), CommunityCreation.class);
//            sendIntent.putExtra("user",user.keySet().toArray(new DatabaseReference[0])[0].getKey());
            startActivity(sendIntent);
        } else if (view.getClass().equals(TextView.class)) {
            Intent sendIntent = new Intent(this, Community.class);
            String[] CommunityRefArr = communities.keySet().toArray(new String[0]);
            TextView[] tvArr =  communities.values().toArray(new TextView[0]);
            for(int i=0;i<tvArr.length;i++){
                if(((TextView)view).getText().equals(tvArr[i].getText())){
                    sendIntent.putExtra("community",CommunityRefArr[i]);
                    startActivity(sendIntent);
                }
            }


        }

    }

    @SuppressLint("MissingPermission")
    private void getGPS(){
        if (arePermissionsGranted(GPS_PERMISSIONS) && isGPSEnabled()) {
            LocationServices.getFusedLocationProviderClient(MainActivity.this)
                    .requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);

                            LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                    .removeLocationUpdates(this);
                            if (locationResult!=null &&locationResult.getLocations().size()>0)location = new Location(locationResult.getLocations().get(locationResult.getLocations().size()-1).getLongitude(), locationResult.getLocations().get(locationResult.getLocations().size()-1).getLatitude());
                        }
                    }, Looper.getMainLooper());


        }else{
            if(!arePermissionsGranted(GPS_PERMISSIONS))toast("need GPS prmition to work");
            if(!isGPSEnabled()){
                toast("you need to enable GPS");
                turnOnGPS();
            }
        }
    }


    private void turnOnGPS() {


        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(MainActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have GPSlocation
                            break;
                    }
                }
            }
        });
    }

    private boolean isGPSEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void getOtherCommunities() {
        moreCommunitiesLayout.removeAllViews();
        DatabaseReference commRef = FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference("communities");
//        Query query = commRef.child("gpslocation").orderByChild("latitude").startAt(location.getMinLatBound(maxDistance)).endAt(location.getMaxLatBound(maxDistance)).orderByChild("longitude").startAt(location.getMinLonBound(maxDistance)).endAt(location.getMaxLonBound(maxDistance));
        Query query = commRef.orderByChild("name")
                .limitToFirst(100);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                query.removeEventListener(this);
                TextView tempTV;
                Map<String, Map<String,String>> tempCommunities = (Map<String, Map<String,String>>)dataSnapshot.getValue();
                String[] communitiesRefArry =  tempCommunities.keySet().toArray(new String[0]);
                Map<String,String>[] communitiesArry = tempCommunities.values().toArray(new Map[0]);
                for(int i=0;i<communitiesArry.length;i++){
                    tempTV =new TextView(getApplicationContext());
                    tempTV.setOnClickListener(MainActivity.this::onClick);
                    tempTV.setText(communitiesArry[i].get("name"));
                    tempTV.setTextSize(25);
                    tempTV.setGravity(Gravity.CENTER);
                    tempTV.setPadding(0,50,0,0);
                    communities.put(communitiesRefArry[i],tempTV);
                    moreCommunitiesLayout.addView(tempTV);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                toast("The read failed: " + databaseError.getCode());
            }
        });
    }
    private void getRecommendedCommunities() {
        moreCommunitiesLayout.removeAllViews();
        DatabaseReference commRef = FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference("communities");
//        Query query = commRef.child("gpslocation").orderByChild("latitude").startAt(location.getMinLatBound(maxDistance)).endAt(location.getMaxLatBound(maxDistance)).orderByChild("longitude").startAt(location.getMinLonBound(maxDistance)).endAt(location.getMaxLonBound(maxDistance));
        Query query = commRef.orderByChild("name")
                .limitToFirst(100);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                query.removeEventListener(this);
                TextView tempTV;
                Map<String, Map<String,String>> tempCommunities = (Map<String, Map<String,String>>)dataSnapshot.getValue();
                String[] communitiesRefArry =  tempCommunities.keySet().toArray(new String[0]);
                Map<String,String>[] communitiesArry = tempCommunities.values().toArray(new Map[0]);
                for(int i=0;i<communitiesArry.length;i++){
                    tempTV =new TextView(getApplicationContext());
                    tempTV.setOnClickListener(MainActivity.this::onClick);
                    tempTV.setText(communitiesArry[i].get("name"));
                    tempTV.setTextSize(25);
                    tempTV.setGravity(Gravity.CENTER);
                    tempTV.setPadding(0,50,0,0);
                    communities.put(communitiesRefArry[i],tempTV);
                    moreCommunitiesLayout.addView(tempTV);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                toast("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void getFollowedCommunities(){
        final String[] ret = new String[]{};
        try {
            Query query =  FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users_to_communities/"+Global.userRef.getKey()+"/followed").orderByKey();
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue()==null)return;
                    String[] temp =(String[]) ((HashMap)snapshot.getValue()).keySet().toArray(new String[0]);
                    for (int i=0;i<temp.length;i++){
                        int finalI = i;
                        FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference("communities/"+temp[i]).orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Map<String,String> community = (Map<String,String>)snapshot.getValue();
                                if(community==null){
                                    toast("invalid followed community");
                                    return;
                                }
                                query.removeEventListener(this);
                                TextView tempTV;

                                tempTV =new TextView(getApplicationContext());
                                tempTV.setOnClickListener(MainActivity.this::onClick);
                                tempTV.setText(community.get("name"));
                                tempTV.setTextSize(25);
                                tempTV.setGravity(Gravity.CENTER);
                                tempTV.setPadding(0,50,0,0);
                                communities.put(temp[finalI],tempTV);
                                followedCommunitiesLayout.addView(tempTV);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Error e){}
    }

    private void getOwnedCommunities(){
        final String[] ret = new String[]{};
        try {
            Query query =  FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users_to_communities/"+Global.userRef.getKey()+"/owned").orderByKey();
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue()==null)return;
                    String[] temp = (String[]) ((HashMap)snapshot.getValue()).keySet().toArray(new String[0]);
                    for (int i=0;i<temp.length;i++){
                        int finalI = i;
                        FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference("communities/"+temp[i]).orderByChild("name").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Map<String,String> community = (Map<String,String>)snapshot.getValue();
                                if(community==null){
                                    toast("invalid owned community");
                                    return;
                                }
                                query.removeEventListener(this);
                                TextView tempTV;
                                tempTV =new TextView(getApplicationContext());
                                tempTV.setOnClickListener(MainActivity.this::onClick);
                                tempTV.setText(community.get("name"));
                                tempTV.setTextSize(25);
                                tempTV.setGravity(Gravity.CENTER);
                                tempTV.setPadding(0,50,0,0);
                                communities.put(temp[finalI],tempTV);
                                ownedCommunitiesLayout.addView(tempTV);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Error e){}
    }


    private boolean arePermissionsGranted(String[] neededPermissions) {
        for (String permission : neededPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void toast(String text){
        Toast t = new Toast(getApplicationContext());
        t.setText(text);
        t.show();
    }


}