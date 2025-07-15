package com.example.touchgrass_finalproject;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
        text = findViewById(R.id.my_edit_text);
        profilePic = findViewById(R.id.profilePicDetail);
        image = findViewById(R.id.postDetailImage);
        date = findViewById(R.id.dateOfPost);
        username = findViewById(R.id.usernamePostDetail);
    }
}