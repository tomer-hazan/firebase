package com.example.firebase.ui.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.firebase.Global;
import com.example.firebase.R;
import com.example.firebase.objects.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class Login extends AppCompatActivity implements View.OnClickListener {

    FirebaseDatabase database;
    DatabaseReference myRef;

    Button signUp;
    EditText name;
    EditText password;
    User me;
    Button submit;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        name = findViewById(R.id.name);
        password =  findViewById(R.id.password);
        submit = findViewById(R.id.submit);
        signUp = findViewById(R.id.signUp);
        submit.setOnClickListener(this::onClick);
        signUp.setOnClickListener(this::onClick);
        database = FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference().child("users");
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==submit.getId()) {
            Query query = myRef.orderByChild("username").equalTo(name.getText().toString());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    query.removeEventListener(this);
                    if (dataSnapshot.hasChildren()){
                        boolean correctPassword = false;
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            if(userSnapshot.child("password").getValue().toString().equals(password.getText().toString())){
                                Intent sendIntent = new Intent(getApplicationContext(), MainActivity.class);
                                Global.user = new Gson().fromJson( new Gson().toJson(userSnapshot.getValue()),User.class);
                                Global.userRef=userSnapshot.getRef();
                                startActivity(sendIntent);
                                correctPassword=true;
                            }
                        }
                        if(!correctPassword){
                            toast("wrong password, try again or if you want to create a new one, go to sign up");
                        }
                    }
                    else{
                        toast("wrong user name, if you want to create a new one, go to sign up");
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    toast("The read failed: " + databaseError.getCode());
                }
            });
        }
        else if (v.getId()== signUp.getId()){
            Intent sendIntent = new Intent(this, SignUp.class);
            startActivity(sendIntent);
        }

    }
    private void toast(String text){
        Toast t = new Toast(getApplicationContext());
        t.setText(text);
        t.show();
    }
}