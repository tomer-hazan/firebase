package com.example.firebase.ui.activitys;

import static android.Manifest.permission_group.CAMERA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;

import com.example.firebase.R;
import com.example.firebase.objects.User;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsRequest;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.location.LocationManager;
import com.google.android.gms.location.LocationRequest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import android.Manifest;

import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv;
    StorageReference storageRef;
    DatabaseReference userRef;
    SurfaceView surfaceView;
    EditText imgName;
    LinearLayout linearLayout;
    User user;
    CameraDevice.StateCallback stateCallback;
    String cameraId;
    Button submit;
    private LocationRequest locationRequest;
    Button communityBtn;
    CameraManager cameraManager;
    StorageReference imagesRef;
    Boolean haveCamera;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private double longitude;
    private double latitude;

    private static final int REQUEST_CODE = 22;
    private String[] neededPermissions = new String[]{CAMERA};

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;

    private static final int REQUEST_CHECK_SETTINGS = 10001;
    private static final String[] CAMERA_PERMISSIONS = {
            Manifest.permission.CAMERA,
            //Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final String[] GPS_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        haveCamera = true;

        if (!arePermissionsGranted(CAMERA_PERMISSIONS))
            ActivityCompat.requestPermissions(this, CAMERA_PERMISSIONS, REQUEST_CAMERA_PERMISSION);
        if (!arePermissionsGranted(GPS_PERMISSIONS))
            ActivityCompat.requestPermissions(this, GPS_PERMISSIONS, MY_PERMISSIONS_REQUEST_LOCATION);
        imgName = findViewById(R.id.imgName);
        tv = findViewById(R.id.text);
        linearLayout = findViewById(R.id.ImagesLayout);
        submit = findViewById(R.id.submit);
        communityBtn = findViewById(R.id.community);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        getGPS();
        getUser();
        storageRef = FirebaseStorage.getInstance("gs://th-grade-34080.appspot.com").getReference().child(userRef.getKey());
        submit.setOnClickListener(this::onClick);
        communityBtn.setOnClickListener(this::onClick);
        imagesRef = storageRef.child("/images/");
        getImages();


    }

    private boolean getUser() {
        Intent intent = getIntent();
        String UserID = intent.getStringExtra("UserRef");
        userRef = FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users").child(UserID);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String json = new Gson().toJson(snapshot.getValue());
                user = new Gson().fromJson(json, User.class);
                //user = new User("username",  "email", 11111);
                writeToTV();
                userRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return (intent != null);
    }

    private void writeToTV(){
        tv.setText(user.toString() + "longitude: " + longitude + "latitude: " + latitude);
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View view) {
        if (view.getId() == submit.getId()) {
            if (arePermissionsGranted(CAMERA_PERMISSIONS)) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CODE);
            } else {
                toast("need camera to work");
            }
            getImages();
            getGPS();
        } else if (view.getId() == communityBtn.getId()) {
            Intent sendIntent = new Intent(getApplicationContext(), Community.class);
            startActivity(sendIntent);
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
                            if (locationResult!=null &&locationResult.getLocations().size()>0){

                                latitude = locationResult.getLocations().get(locationResult.getLocations().size()-1).getLatitude();
                                longitude = locationResult.getLocations().get(locationResult.getLocations().size()-1).getLongitude();
                            }
                            writeToTV();
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
                            //Device does not have location
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

    @Override
    protected void onActivityResult(int requestCode,int resultCode,@NonNull Intent data) {
        if(REQUEST_CODE==requestCode && resultCode == RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            // Create a reference to the desired location in Firebase Storage
            StorageReference imageRef = storageRef.child(userRef.getKey() + "/images/" + imgName.getText().toString());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            // Upload the byte array to Firebase Storage
            UploadTask uploadTask = imageRef.putBytes(byteArray);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                toast("the image has been loaded");
                // You can get the download URL of the image from taskSnapshot.getDownloadUrl()
            }).addOnFailureListener(e -> {
                toast("failed to load img");
            }).addOnCompleteListener(taskSnapshot -> {
                getImages();
            });

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getImage(StorageReference imageRef){
        ImageView image = new ImageView(getApplicationContext());
        image.setImageDrawable(getDrawable(R.drawable.null_img));
        linearLayout.addView(image);
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
        });
    }
    private void getImages() {
        linearLayout.removeAllViews();
        imagesRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            getImage(item);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors that occur during listing
                    }
                });
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