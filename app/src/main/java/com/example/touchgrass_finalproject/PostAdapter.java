package com.example.touchgrass_finalproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
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

    private final Context context;

    public PostAdapter(Context context, @Nullable OrderedRealmCollection<Post> data, boolean autoUpdate) {
        super(data, autoUpdate);
        this.context = context;
    }

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

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        Post post = getItem(position);

        if (post == null) return;

        if (post.getUser() != null) {
            holder.name.setText(post.getUser().getName());
            File profileImage = new File(context.getExternalCacheDir(), post.getUser().getUuid() + ".jpeg");
            if (profileImage.exists()) {
                Picasso.get()
                        .load(profileImage)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(holder.profilePic);
            }
        } else {
            holder.name.setText("Deleted User");
            holder.profilePic.setImageResource(R.drawable.ic_launcher_foreground);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        holder.date.setText(sdf.format(post.getDate()));

        File photo = new File(context.getExternalCacheDir(), post.getUuid() + ".jpeg");
        if (photo.exists()) {
            Picasso.get()
                    .load(photo)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.image);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailPage.class);
            intent.putExtra("post_id", post.getUuid());
            context.startActivity(intent);
        });
    }
}
