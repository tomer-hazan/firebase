package com.example.firebase.ui.activitys;

import static com.example.firebase.util.arePermissionsGranted;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PostsCreation extends AppCompatActivity implements View.OnClickListener {//toDO allow in app adding of images to posts
    static EditText title;
    static EditText content;
    static Button submit;
    LinearLayout imagesLayout;
    Button addImage;
    ActivityResultLauncher<Intent> cameraActivity;
    static PostDB PDB;
    DatabaseReference communityRef;
    DatabaseReference postsRef;
    String userRef;
    boolean sendToCommunityCreation;
    List imagesRefPath;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    CameraManager cameraManager;
    private static final String[] CAMERA_PERMISSIONS = {
            Manifest.permission.CAMERA,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagesRefPath = new ArrayList();
        setContentView(R.layout.activity_posts_creation);
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        submit = findViewById(R.id.submit);
        submit.setBackgroundColor(Color.GREEN);
        submit.setOnClickListener(this::onClick);
        imagesLayout = findViewById(R.id.imagesLayout);
        addImage = findViewById(R.id.addImage);
        addImage.setOnClickListener(this::onClick);
        communityRef=Global.community.communityRef.child("posts");
        postsRef = FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("posts");
        Intent intent = getIntent();
        userRef = intent.getStringExtra("user");
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (!arePermissionsGranted(this.getApplicationContext(),CAMERA_PERMISSIONS))
            ActivityCompat.requestPermissions(this, CAMERA_PERMISSIONS, REQUEST_CAMERA_PERMISSION);
        initCameraActivityResult();
    }

    public DatabaseReference uploadPost(){
        PDB = new PostDB(Global.userRef.getKey(),title.getText().toString(),content.getText().toString(),imagesRefPath);
        DatabaseReference postRef = postsRef.push();
        postRef.setValue(PDB);
        communityRef.child(postRef.getKey()).setValue("");
        Global.userRef.child("posts").child(postRef.getKey()).setValue("");
        toast("uploaded successfully");
        return postRef;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == submit.getId()) {
            uploadPost();
            toast("uploaded successfully");
            Intent sendIntent = new Intent(getApplicationContext(), Community.class);
            sendIntent.putExtra("community",Global.community.communityRef.getKey());
            startActivity(sendIntent);
        }
        else if (view.getId() == addImage.getId()) {
                if (arePermissionsGranted(this.getApplicationContext(),CAMERA_PERMISSIONS)) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraActivity.launch(intent);
                } else {
                    toast("need camera to work");
                }
        }
    }
    private void toast(String text){
        Toast t = new Toast(getApplicationContext());
        t.setText(text);
        t.show();
    }

    private void initCameraActivityResult(){
        cameraActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if((result.getResultCode() == RESULT_OK) && (result.getData() != null)){
                            Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();
                            int imageID = new Random().nextInt(Integer.MAX_VALUE);
                            StorageReference imageRef = storageRef.child(Global.userRef.getKey() + "/images/"+imageID);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte[] byteArray = stream.toByteArray();

                            // Upload the byte array to Firebase Storage
                            UploadTask uploadTask = imageRef.putBytes(byteArray);

                            uploadTask.addOnSuccessListener(taskSnapshot -> {
                            }).addOnFailureListener(e -> {
                                toast("failed to load img");
                            }).addOnCompleteListener(taskSnapshot -> {
                                toast("the image has been loaded");
                                ImageView tempImage = new ImageView(imagesLayout.getContext());
                                tempImage.setImageBitmap(photo);
                                imagesLayout.addView(tempImage);
                                imagesRefPath.add(imageRef.getPath());
                            });
                        }
                    }
                });
    }
}