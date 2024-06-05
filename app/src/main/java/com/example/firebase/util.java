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
    public static void toast(String text,Context context){
        Toast t = new Toast(context);
        t.setText(text);
        t.setDuration(Toast.LENGTH_LONG);
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
    public static boolean isCorrectInput(String s,Context context){
//        if(s.indexOf('.')!=-1){
//            toast("you cant use '.'",context);
//            return false;
//        }
//        if(s.indexOf('$')!=-1){
//            toast("you cant use '$'",context);
//            return false;
//        }
//        if(s.indexOf('#')!=-1){
//            toast("you cant use '#'",context);
//            return false;
//        }
//        if(s.indexOf('[')!=-1){
//            toast("you cant use '['",context);
//            return false;
//        }
//        if(s.indexOf(']')!=-1){
//            toast("you cant use ']'",context);
//            return false;
//        }
//        if(s.indexOf('/')!=-1){
//            toast("you cant use '/'",context);
//            return false;
//        }
        for (int i=0;i<32;i++){
            if(s.indexOf((char)i)!=-1){
                toast("illegal character",context);
                return false;
            }
        }
        if(s.indexOf((char)127)!=-1){
            toast("illegal character",context);
            return false;
        }
        try{
            final byte[] utf8Bytes = s.getBytes("UTF-8");
            if(utf8Bytes.length>768){
                toast("your text is too long",context);
                return false;
            }
        }catch (Exception e){
            toast("this text couldn't be encoded to UTF-8",context);
            return false;
        }
        return true;
    }
}
