package com.example.touchgrass_finalproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

public class DailyPost extends AppCompatActivity {

    private Realm realm;
    private RecyclerView dailyPostList;
    private TextView selectedDateText;

    private ImageView returnPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daily_post);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        realm = Realm.getDefaultInstance();
        initViews();
    }

    private void initViews() {
        dailyPostList = findViewById(R.id.dailyPostList);
        selectedDateText = findViewById(R.id.selectedDateText);
        returnPost = findViewById(R.id.goBackReturn);

        returnPost.setOnClickListener(v -> returnPost());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        dailyPostList.setLayoutManager(layoutManager);

        long selectedMillis = getIntent().getLongExtra("selectedDate", -1);
        if (selectedMillis == -1) return;

        Date selectedDate = new Date(selectedMillis);
        selectedDateText.setText("Posts on " + new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(selectedDate));

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(selectedDate);
        calendarStart.set(Calendar.HOUR_OF_DAY, 0);
        calendarStart.set(Calendar.MINUTE, 0);
        calendarStart.set(Calendar.SECOND, 0);
        calendarStart.set(Calendar.MILLISECOND, 0);

        Calendar calendarEnd = (Calendar) calendarStart.clone();
        calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
        calendarEnd.set(Calendar.MINUTE, 59);
        calendarEnd.set(Calendar.SECOND, 59);
        calendarEnd.set(Calendar.MILLISECOND, 999);

        SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
        String currentUserId = prefs.getString("uuid", null);

        RealmResults<Post> dailyPosts = realm.where(Post.class)
                .equalTo("user.uuid", currentUserId)
                .greaterThanOrEqualTo("date", calendarStart.getTime())
                .lessThanOrEqualTo("date", calendarEnd.getTime())
                .findAll();

        PostAdapter adapter = new PostAdapter(this, dailyPosts, true);
        dailyPostList.setAdapter(adapter);
        dailyPostList.addItemDecoration(new SpaceItemDecoration(30));
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
