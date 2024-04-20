package com.example.firebase.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebase.R;
import com.example.firebase.objects.Post;
import com.example.firebase.objects.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.List;

public class PostAdaptorTest extends RecyclerView.Adapter<PostAdaptorTest.ViewHolder> {
    private List<Post> data;
    private Context context;
    private Boolean isOwner;
    private View.OnClickListener onClickListener;

    public PostAdaptorTest(List<Post> data, Context context, boolean isOwner, View.OnClickListener onClickListener) {
        this.data = data;
        this.context = context;
        this.isOwner=isOwner;
        this.onClickListener=onClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView poster;
        public TextView content;
        public LinearLayout linearLayout;
        public ImageView trashCan;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (itemView).findViewById(R.id.title);
            poster = (itemView).findViewById(R.id.poster);
            content = (itemView).findViewById(R.id.content);
            linearLayout =itemView.findViewById(R.id.linear);
            trashCan = itemView.findViewById(R.id.trashCanImage);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = data.get(position);
        holder.title.setText(post.getTitle());
        holder.content.setText(post.getContent());
        initUser(post.getPoster(),holder);
        initImages(post,holder);
        if(isOwner)holder.trashCan.setOnClickListener(onClickListener);
        else holder.trashCan.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    private User initUser(String userID, ViewHolder holder){
        User userOBJ = new User();
        FirebaseDatabase.getInstance("https://th-grade-34080-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users").child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String json = new Gson().toJson(snapshot.getValue());
                        User newUser = new Gson().fromJson(json, User.class);
                        setUser(holder,newUser.username);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("tenp");
                    }
                });
        return userOBJ;
    }
    private void setUser(ViewHolder holder,String userName){
        holder.poster.setText(userName);
    }
    private void initImages(Post post,ViewHolder holder){
        if(post.getImages()==null)return;

        for (ImageView img:post.getImages()) {
            if(img.getParent() != null) {
                ((ViewGroup)img.getParent()).removeView(img);
            }
            holder.linearLayout.addView(img);
        }
    }
}

