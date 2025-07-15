package com.example.touchgrass_finalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class PostAdapter extends RealmRecyclerViewAdapter<Post, PostAdapter.ViewHolder> {


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, date;
        ImageView image, profilePic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.usernamePost);
            date = itemView.findViewById(R.id.datePosted);
            image = itemView.findViewById(R.id.imagePosted);
            profilePic = itemView.findViewById(R.id.profilePic);
        }
    }


    HomePage activity;
    public PostAdapter(HomePage activity, @Nullable OrderedRealmCollection<Post> data, boolean autoUpdate) {
        super(data, autoUpdate);
        this.activity = activity;
    }


    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = activity.getLayoutInflater().inflate(R.layout.post_layout, parent, false);  // VERY IMPORTANT TO USE THIS STYLE
        PostAdapter.ViewHolder vh = new PostAdapter.ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        Post post = getItem(position);
        holder.name.setText(post.getUser().getName());
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        holder.date.setText(sdf.format(post.getDate()));

        File cacheDir = activity.getExternalCacheDir();
        File photo = new File(cacheDir, post.getUuid()+".jpeg");
        if (photo.exists())
        {
            Picasso.get()
                    .load(photo)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.image);
        }

        if (post.getUser() != null) {
            File profileImage = new File(activity.getExternalCacheDir(), post.getUser().getUuid() + ".jpeg");
            if (profileImage.exists()) {
                Picasso.get()
                        .load(profileImage)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(holder.profilePic);
            }
        }

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PostDetailPage.class);
                intent.putExtra("post_id", post.getUuid());
                activity.startActivity(intent);
            }
        });
    }
}