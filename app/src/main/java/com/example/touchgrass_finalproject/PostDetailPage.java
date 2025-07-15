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
    private ImageView profilePic, image;
    private Button edit, delete;
    private TextView date, username;
    private Realm realm;
    private String postId;

    public void initViews(){
        realm = Realm.getDefaultInstance();
        text = findViewById(R.id.my_edit_text);
        profilePic = findViewById(R.id.profilePicDetail);
        image = findViewById(R.id.postDetailImage);
        date = findViewById(R.id.dateOfPost);
        username = findViewById(R.id.usernamePostDetail);
        edit = findViewById(R.id.editPost);
        delete = findViewById(R.id.deletePost);

        edit.setOnClickListener(v -> edit());
        delete.setOnClickListener(v -> delete());

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
        }

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
}