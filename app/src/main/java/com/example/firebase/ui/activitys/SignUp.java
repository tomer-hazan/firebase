package com.example.firebase.ui.activitys;

//import static io.grpc.Context.LazyStorage.storage;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
        import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    Button submit;
    Button login;
    TextView tv;
    FirebaseDatabase database;
    DatabaseReference myRef;
    EditText name;
    EditText email;
    EditText password;
    EditText phoneNumber;
    Intent Intent;
    Intent loginIntent;

    int i;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        submit = findViewById(R.id.submit);
        login = findViewById(R.id.LogIn);
        tv = findViewById(R.id.textView);
        submit.setOnClickListener(this::onClick);
        login.setOnClickListener(this::onClick);
        name = findViewById(R.id.editTextText);
        email = findViewById(R.id.editTextTextEmailAddress);
        password =  findViewById(R.id.editTextNumberPassword);
        phoneNumber = findViewById(R.id.phoneNumber);
        i=0;
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference("users");


    }

    @Override
    public void onClick(View v) {
        if (v.getId()==submit.getId()){
            handleUploadUser();

        }
        else if (v.getId()==login.getId()){
            Intent sendIntent = new Intent(this, Login.class);
            startActivity(sendIntent);
        }

    }

    public void handleUploadUser(){
        Query query = myRef.orderByChild("username").equalTo(name.getText().toString());
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                query.removeEventListener(this);
                if (dataSnapshot.hasChildren()){
                    toast("user name is taken");

                } else{
                    Global.userRef = uploadUser();
                    Intent sendIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(sendIntent);
                }


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                toast("The read failed: " + databaseError.getCode());
            }
        });
    }
    public DatabaseReference uploadUser(){
        User user = new User(name.getText().toString(),email.getText().toString(),password.getText().toString(),phoneNumber.getText().toString());
        name.setText("");
        email.setText("");
        password.setText("");
        phoneNumber.setText("");
        DatabaseReference userRef = myRef.push();
        userRef.setValue(user);
        return userRef;
    }
    private void toast(String text){
        Toast t = new Toast(getApplicationContext());
        t.setText(text);
        t.show();
    }
}