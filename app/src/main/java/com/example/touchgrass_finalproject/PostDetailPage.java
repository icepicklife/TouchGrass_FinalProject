package com.example.touchgrass_finalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.Realm;

public class PostDetailPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_detail_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
    }

    private EditText text;
    private ImageView profilePic, image, home, add, returnPost, save, profile;
    private Button edit, delete;
    private TextView date, username, edited;
    private Realm realm;
    private String postId;
    private Boolean isEdited = false;

    public void initViews(){
        realm = Realm.getDefaultInstance();
        isEdited = getIntent().getBooleanExtra("edited",false);
        text = findViewById(R.id.my_edit_text);
        profilePic = findViewById(R.id.profilePicDetail);
        image = findViewById(R.id.postDetailImage);
        date = findViewById(R.id.dateOfPost);
        username = findViewById(R.id.usernamePostDetail);
        edit = findViewById(R.id.editPost);
        delete = findViewById(R.id.deletePost);
        home = findViewById(R.id.homeButton);
        add = findViewById(R.id.addButton);
        profile = findViewById(R.id.profileButton);
        save = findViewById(R.id.savePost);
        returnPost = findViewById(R.id.goBackReturn);
        edited = findViewById(R.id.edited);
        edited.setText("");

        if(isEdited){
            edited.setText("(edited)");
        }

        edit.setOnClickListener(v -> edit());
        delete.setOnClickListener(v -> delete());
        home.setOnClickListener(v -> home());
        add.setOnClickListener(v -> add());
        profile.setOnClickListener(v -> goToProfile());
        save.setOnClickListener(v -> save());
        returnPost.setOnClickListener(v -> returnPost());

        postId = getIntent().getStringExtra("post_id");
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        if(postId != null){
            Post post = realm.where(Post.class).equalTo("uuid", postId).findFirst();
            if (post != null) {
                User postUser = post.getUser();
                if (postUser != null){
                    username.setText(post.getUser().getName());
                    File profilePhoto = new File(getExternalCacheDir(), post.getUser().getUuid() + ".jpeg");
                    if (profilePhoto.exists())
                    {
                        Picasso.get()
                                .load(profilePhoto)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(profilePic);
                    }
                }else{
                    username.setText("Deleted User");
                    profilePic.setImageResource(R.drawable.ic_launcher_foreground);
                }

                date.setText(sdf.format(post.getDate()));
                text.setText(post.getDescription());

                File photo = new File(getExternalCacheDir(), postId + ".jpeg");
                if (photo.exists())
                {
                    Picasso.get()
                            .load(photo)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .into(image);
                }
            }

            SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
            String currentUserId = prefs.getString("uuid", null);

            if (currentUserId != null &&  post.getUser() != null && currentUserId.equals(post.getUser().getUuid())) {
                edit.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
            } else {
                edit.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
            }

            User currentUser = realm.where(User.class).equalTo("uuid", currentUserId).findFirst();
            if (currentUser != null) {
                updateSaveIcon(currentUser, post);
            }
        }

    }

    public void returnPost(){
        finish();
    }

    public void home(){
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void add(){
        Intent intent = new Intent(this, NewPostPage.class);
        startActivity(intent);
    }

    public void goToProfile(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void edit(){
        Intent intent = new Intent(this, NewPostPage.class);
        intent.putExtra("post_id", postId);
        startActivity(intent);
    }

    public void delete(){
        new AlertDialog.Builder(this)
                    .setTitle("Delete Post")
                    .setMessage("Are you sure you want to delete this Post?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(postId != null) {
                                Post post = realm.where(Post.class).equalTo("uuid", postId).findFirst();
                                realm.beginTransaction();
                                post.deleteFromRealm();
                                realm.commitTransaction();
                            }
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .show();
    }

    public void save() {
        if (postId != null) {
            Post post = realm.where(Post.class).equalTo("uuid", postId).findFirst();

            if (post != null) {
                SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
                String currentUserId = prefs.getString("uuid", null);

                if (currentUserId != null) {
                    User user = realm.where(User.class).equalTo("uuid", currentUserId).findFirst();

                    if (user != null) {
                        realm.executeTransaction(r -> {
                            if (user.getSavedPosts().contains(post)) {
                                user.getSavedPosts().remove(post);
                                Toast.makeText(this, "Post unsaved.", Toast.LENGTH_SHORT).show();
                            } else {
                                user.getSavedPosts().add(post);
                                Toast.makeText(this, "Post saved!", Toast.LENGTH_SHORT).show();
                            }
                            updateSaveIcon(user, post);
                        });
                    }
                }
            }
        }
    }

    private void updateSaveIcon(User user, Post post) {
        if (user.getSavedPosts().contains(post)) {
            save.setImageResource(R.drawable.saved_photo);
        } else {
            save.setImageResource(R.drawable.unsaved_photo);
        }
    }
}