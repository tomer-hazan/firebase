package com.example.firebase.objects;

import com.google.firebase.database.IgnoreExtraProperties;

import java.lang.reflect.Field;

@IgnoreExtraProperties
public class User {

    public String username;
    public String email;

    public String password;



    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString(){
        return "username: " + username + " " + "email: " + email + " " + "password: " + password;
    }
    public void clone(User other) throws IllegalAccessException {
        for (Field f:other.getClass().getDeclaredFields()
        ) {
            f.set(this,f.get(other));
        }
    }

}