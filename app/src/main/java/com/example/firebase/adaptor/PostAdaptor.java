package com.example.firebase.adaptor;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;
import static com.example.firebase.ui.activitys.Community.initFollowersAndAdmins;
import static com.example.firebase.util.toast;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebase.R;
import com.example.firebase.objects.Image;
import com.example.firebase.objects.ImageDB;
import com.example.firebase.objects.Post;
import com.example.firebase.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.api.BackendOrBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostAdaptor extends RecyclerView.Adapter<PostAdaptor.ViewHolder> implements View.OnClickListener {
    private List<Post> data;
    private List<ViewHolder> postsHolders;
    private List<View> viewsList;
    private Context context;
    private boolean isOwner=false;
    private ViewGroup parent;

    public PostAdaptor(List<Post> data, Context context, boolean isOwner) {
        this.data = data;
        this.context = context;
        postsHolders= new ArrayList<>();
        this.isOwner=isOwner;
        this.viewsList = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        if(v.getClass()== ImageView.class){
            boolean isRemoved = false;
            for(int i=0;i<postsHolders.size();i++){
                if(postsHolders.get(i).trashCan.getId()==v.getId()){
                    data.get(i).selfDestruction();
                    //((ViewGroup)postsHolders.get(i).itemView.getParent()).removeView(postsHolders.get(i).itemView);
                    toast("the post has been removed",context);
                    isRemoved=true;
                }
                if(!isRemoved) toast("the post has already been removed",context);
            }
        }else if(v.getClass()== MaterialTextView.class){
            for(int i=0;i<postsHolders.size();i++){
                if(v.equals(postsHolders.get(i).content)||v.equals(postsHolders.get(i).title)||v.equals(postsHolders.get(i).poster)){
                    if(data.get(i).getPoster().phoneNumber==""){
                        toast("the user has no phone number",context);
                        return;
                    }
                    toast("the user has a phone number: "+data.get(i).getPoster().phoneNumber,context);
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+data.get(i).getPoster().phoneNumber));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    return;
                }
            }
        }


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
        viewsList.add(itemView);
        this.parent=parent;
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        postsHolders.add(holder);
        Post post = data.get(position);
        holder.title.setText(post.getTitle());
        holder.content.setText(post.getContent());
        if(isOwner){
            holder.trashCan.setVisibility(View.VISIBLE);
            holder.trashCan.setOnClickListener(this::onClick);
        } else holder.trashCan.setVisibility(View.INVISIBLE);
        holder.title.setOnClickListener(this::onClick);
        holder.content.setOnClickListener(this::onClick);
        initUser(post.getPoster(),holder);
        initImages(post,holder);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    private void initUser(User poster, ViewHolder holder){
        holder.poster.setText(poster.username);
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
    public void setTrashCanInvisible(boolean invisible){
        for (ViewHolder trashCan: postsHolders) {
            if(invisible) trashCan.trashCan.setVisibility(View.INVISIBLE);
            else{
                trashCan.trashCan.setVisibility(View.VISIBLE);
                trashCan.trashCan.setOnClickListener(this::onClick);
            }
        }
    }
    public void setOwnerTrue(){
        isOwner=true;
        setTrashCanInvisible(true);
    }
}

