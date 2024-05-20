package com.example.firebase.adaptor;

import static com.example.firebase.util.toast;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebase.R;
import com.example.firebase.objects.Post;
import com.example.firebase.objects.User;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostAdaptor extends RecyclerView.Adapter<PostAdaptor.ViewHolder> implements View.OnClickListener {
    private List<Post> data;
    private ViewHolder[] postsHolders;
    private Context context;
    private boolean isOwner=false;
    public ViewGroup viewGroupParent;

    public PostAdaptor(List<Post> data, Context context, boolean isOwner) {
        this.data = data;
        this.context = context;
        postsHolders= new ViewHolder[data.size()];
        this.isOwner=isOwner;
    }

    @Override
    public void onClick(View v) {
        if(postsHolders==null)return;
        if(v.getClass()== AppCompatImageView.class){

            boolean isRemoved = false;
            for(int i=0;i<postsHolders.length;i++){
                if(postsHolders[i].trashCan.equals(v)){
                    toast("the post has started self destructing",context);
                    data.get(i).selfDestruction();
                    data.remove(i);
                    //((ViewGroup)postsHolders.get(i).itemView.getParent()).removeView(postsHolders.get(i).itemView);
                    int wirter=0;
                    PostAdaptor.ViewHolder[] temp = postsHolders;
                    postsHolders = new ViewHolder[temp.length-1];
                    for(int j=0;j<temp.length;j++){
                        if(j!=i){
                            postsHolders[wirter]=temp[j];
                            wirter++;
                        }
                    }
                    toast("the post has been removed",context);
                    isRemoved=true;
                    break;
                }
            }
            refresh();
            if(!isRemoved) toast("the post has already been removed",context);
        }else if(v.getClass()== MaterialTextView.class){
            for(int i=0;i<postsHolders.length;i++){
                if(v.equals(postsHolders[i].content)||v.equals(postsHolders[i].title)||v.equals(postsHolders[i].poster)){
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
        viewGroupParent=parent;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        postsHolders[position]=(holder);
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

    private void refresh(){
        viewGroupParent.removeAllViews();
        for(int i=0;i<postsHolders.length;i++){
            LayoutInflater.from(viewGroupParent.getContext()).inflate(R.layout.post, viewGroupParent, false);
        }
    }
}

