package com.example.touchgrass_finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class SavedPosts extends AppCompatActivity {

    private Realm realm;
    private RecyclerView savedPostList;
    private PostAdapter adapter;

    private ImageView returnPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saved_posts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        realm = Realm.getDefaultInstance();
        initViews();
    }

    public void initViews() {
        realm = Realm.getDefaultInstance();
        savedPostList = findViewById(R.id.savedPostList);
        returnPost = findViewById(R.id.goBackReturn);

        returnPost.setOnClickListener(v -> returnPost());


        SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
        String currentUserId = prefs.getString("uuid", null);
        User currentUser = null;

        if (currentUserId != null) {
            currentUser = realm.where(User.class).equalTo("uuid", currentUserId).findFirst();
        }

        if (currentUser == null) {
            finish();
            return;
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        savedPostList.setLayoutManager(layoutManager);

        RealmList<Post> savedPosts = currentUser.getSavedPosts();
        PostAdapter adapter = new PostAdapter(this, savedPosts, true);

        savedPostList.setAdapter(adapter);

        int spacingInPixels = 30;
        savedPostList.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
    }

    public void returnPost(){
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
