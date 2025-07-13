package com.example.touchgrass_finalproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.realm.Realm;

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

    public void initViews(){
        realm = Realm.getDefaultInstance();
        welcome=findViewById(R.id.welcome);

        SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
        String uuid = prefs.getString("uuid",null);
        User userID = realm.where(User.class)
                .equalTo("uuid",uuid)
                .findFirst();

        welcome.setText(userID.getName() + "!");
    }

}