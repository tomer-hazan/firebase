package com.example.firebase.objects;


import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.firebase.R;
import com.example.firebase.ui.activitys.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ImageDB {
    String imageRef;//the url of the image
    Context context;
    public ImageDB(){}
    public ImageDB(String ref,Context context){
        this.imageRef = ref;
        this.context = context;
    }
    public void setContext(Context context){this.context=context;}
    public String getImageRef(){return imageRef;}
    public void setImageRef(String imageRef){this.imageRef = imageRef;}
    public ImageView toImage(){
        ImageView image = new ImageView(context);
        image.setImageDrawable(context.getDrawable(R.drawable.null_img));
        StorageReference ImageStorageREf = FirebaseStorage.getInstance(imageRef).getReference();
        ImageStorageREf.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .into(image);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("the error1",exception.toString());
                Log.e("the error2",exception.getMessage());
            }
        });
        return image;
    }
}
