package com.example.touchgrass_finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Calendar;

import io.realm.Realm;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profilePic, returnPost, editProfileButton;
    private TextView usernameText, passwordText;
    private Button savedPostsBtn;
    private CalendarView calendarView;

    private Realm realm;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        realm = Realm.getDefaultInstance();
        initViews();
        loadUserData();
    }

    private void initViews() {
        profilePic = findViewById(R.id.userProfilePic);
        usernameText = findViewById(R.id.usernameText);
        passwordText = findViewById(R.id.passwordText);
        savedPostsBtn = findViewById(R.id.savedPostsBtn);
        returnPost = findViewById(R.id.goBackReturn);
        calendarView = findViewById(R.id.calendarView);
        editProfileButton = findViewById(R.id.editProfileButton);

        savedPostsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SavedPosts.class);
            startActivity(intent);
        });

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfile.class);
            startActivity(intent);
        });

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);

            long selectedTimeInMillis = selectedDate.getTimeInMillis();

            Intent intent = new Intent(ProfileActivity.this, DailyPost.class);
            intent.putExtra("selectedDate", selectedTimeInMillis);
            startActivity(intent);
        });

        returnPost.setOnClickListener(v -> returnPost());
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
        String userId = prefs.getString("uuid", null);

        if (userId != null) {
            currentUser = realm.where(User.class).equalTo("uuid", userId).findFirst();

            if (currentUser != null) {
                usernameText.setText(currentUser.getName());
                passwordText.setText(currentUser.getPassword());

                File profilePhoto = new File(getExternalCacheDir(), userId + ".jpeg");
                if (profilePhoto.exists()) {
                    Picasso.get()
                            .load(profilePhoto)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .into(profilePic);
                } else {
                    profilePic.setImageResource(R.drawable.ic_launcher_foreground);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    public void returnPost() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
