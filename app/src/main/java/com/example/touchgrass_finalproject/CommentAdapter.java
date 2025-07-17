package com.example.touchgrass_finalproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class CommentAdapter extends RealmRecyclerViewAdapter<Comment, CommentAdapter.ViewHolder> {

    private Context context;

    public CommentAdapter(Context context, @Nullable OrderedRealmCollection<Comment> data, boolean autoUpdate) {
        super(data, autoUpdate);
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username, comment_content;
        ImageView profilePic;
        Button EditButton;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            username = itemView.findViewById(R.id.CommentUserName);
            comment_content = itemView.findViewById(R.id.CommentDisplay);
            profilePic = itemView.findViewById(R.id.UserPhotoRecycle);

            EditButton = itemView.findViewById(R.id.EditButton);
        }
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.commentbox_layout, parent, false);
        return new CommentAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {

        Comment comment = getItem(position);

        if (comment != null) {

            holder.username.setText(comment.getUsername());
            holder.comment_content.setText(comment.getCommentContent());

            String userID = comment.getUserUUID();
            File profilePhoto = new File(context.getExternalCacheDir(), userID + ".jpeg");

            if (profilePhoto.exists()){

                Picasso.get()
                        .load(comment.getUserProfPic_url())
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(holder.profilePic);

            } else {

                holder.profilePic.setImageResource(R.drawable.ic_launcher_foreground);
            }


            holder.EditButton.setOnClickListener(v -> OnEditButtonClick(comment));
        }



    }

    public void OnEditButtonClick(Comment comment){

        String activeUserName = CurrentUserSession.getUsername(context);

        if (comment.getUsername().equals(activeUserName)){

            Intent edit_intent = new Intent(context, AddEditCommentActivity.class);
            edit_intent.putExtra("commentID", comment.getCommentUuid());
            edit_intent.putExtra("postID", comment.getPostUUID());
            context.startActivity(edit_intent);

        } else {

            Toast user_owner_prompt = Toast.makeText(context, "", Toast.LENGTH_SHORT);
            user_owner_prompt.show();

        }
    }


}
