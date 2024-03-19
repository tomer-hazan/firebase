package com.example.firebase;

import com.example.firebase.objects.Community;
import com.example.firebase.objects.User;
import com.google.firebase.database.DatabaseReference;

import java.util.Map;

public final class Global {
    public static DatabaseReference userRef;
    public static User user;
    public static Community community;
}
