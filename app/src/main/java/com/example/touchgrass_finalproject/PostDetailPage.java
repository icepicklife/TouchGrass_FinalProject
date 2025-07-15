package com.example.touchgrass_finalproject;

import android.content.SharedPreferences;
import android.os.Bundle;
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
    private TextView date, username;
    private Realm realm;

    public void initViews(){
        realm = Realm.getDefaultInstance();
        text = findViewById(R.id.my_edit_text);
        profilePic = findViewById(R.id.profilePicDetail);
        image = findViewById(R.id.postDetailImage);
        date = findViewById(R.id.dateOfPost);
        username = findViewById(R.id.usernamePostDetail);
        String postId = getIntent().getStringExtra("post_id");
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        if(postId != null){
            Post post = realm.where(Post.class).equalTo("uuid", postId).findFirst();
            if (post != null) {
                username.setText(post.getUser().getName());
                date.setText(sdf.format(post.getDate()));
                text.setText(post.getDescription());
                File profilePhoto = new File(getExternalCacheDir(), post.getUser().getUuid() + ".jpeg");
                if (profilePhoto.exists())
                {
                    Picasso.get()
                            .load(profilePhoto)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .into(profilePic);
                }
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
        }
    }
}