package com.example.firebase;

import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.firebase.objects.CommunityDB;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class util {
    public static CommunityDB toCommunityDB(Context context, DatabaseReference community){
        CommunityDB temp = new CommunityDB();
        temp.setContext(context);
        CommunityDB[] ret ={temp};
        community
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String json = new Gson().toJson(snapshot.getValue());
                        Context temp = ret[0].getContext();
                        ret[0] =  new Gson().fromJson(json, CommunityDB.class);
                        ret[0].setContext(temp);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        return ret[0];
    }
    public static void toast(String text,Context context){
        Toast t = new Toast(context);
        t.setText(text);
        t.show();
    }

    public static boolean arePermissionsGranted(Context context, String[] neededPermissions) {
        for (String permission : neededPermissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
