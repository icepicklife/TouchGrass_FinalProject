package com.example.touchgrass_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmResults;

public class PostCommentScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_comment_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();

    }
    private RecyclerView commentRecycle;
    private CommentAdapter adapter;
    private Realm realm;
    private String postID;
    private ImageButton CommentButton;

    public void initViews(){

        postID = getIntent().getStringExtra("postID");

        realm = Realm.getDefaultInstance();
        CommentButton = findViewById(R.id.CommentButton);
        commentRecycle = findViewById(R.id.CommentRecycler);

        commentRecycle.setLayoutManager(new LinearLayoutManager(this));

        RealmResults<Comment> comments = realm.where(Comment.class)
                .equalTo("postID", postID)
                .findAllAsync();

        adapter = new CommentAdapter(this, comments, true);
        commentRecycle.setAdapter(adapter);

        CommentButton.setOnClickListener(v -> OnCommentClick());
    }

    public void OnCommentClick(){

        Intent newComment_intent = new Intent(this, AddEditCommentActivity.class);
        newComment_intent.putExtra("postID", postID);
        startActivity(newComment_intent);

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        realm.close();

    }
}