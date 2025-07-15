package com.example.touchgrass_finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmResults;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
    }

    private TextView welcome;
    private Realm realm;
    private ImageView add, refresh;
    RecyclerView postList;
    private Button logOut;


    public void initViews(){
        realm = Realm.getDefaultInstance();
        welcome=findViewById(R.id.welcome);
        add=findViewById(R.id.addButton);
        postList=findViewById(R.id.postList);
        logOut=findViewById(R.id.logOut);
        refresh=findViewById(R.id.homeButton);

        SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
        String uuid = prefs.getString("uuid",null);
        User userID = realm.where(User.class)
                .equalTo("uuid",uuid)
                .findFirst();

        welcome.setText(userID.getName() + "!");
        add.setOnClickListener(v -> addPost());
        logOut.setOnClickListener(v -> logOut());
        refresh.setOnClickListener(v -> refresh());

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        postList.setLayoutManager(mLayoutManager);

        RealmResults<Post> list = realm.where(Post.class).findAll();
        PostAdapter adapter = new PostAdapter(this, list, true);
        postList.setAdapter(adapter);
    }

    public void addPost(){
        Intent intent = new Intent(this, NewPostPage.class);
        startActivity(intent);
    }

    public void logOut(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void refresh(){
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}