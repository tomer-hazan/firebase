package com.example.firebase.objects;

import android.widget.ImageView;

public class Image {
    ImageView image;
    User user;
    String root;
    public Image(){}
    public Image(String root, User user){
        this.root = root;
        this.user = user;
    }
    public String getRoot(){return root;}
    public User getUser(){return user;}
    public void setRoot(String root){this.root = root;}
    public void setUSer(User user){this.user = user;}
}
