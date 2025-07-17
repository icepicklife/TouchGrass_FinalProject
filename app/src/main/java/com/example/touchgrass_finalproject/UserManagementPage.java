package com.example.touchgrass_finalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmResults;

public class UserManagementPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_management_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
    }

    Button add, clear, back;
    RecyclerView userList;
    Realm realm;


    public void onDestroy() {
        super.onDestroy();

        if (!realm.isClosed()) {
            realm.close();
        }
    }


    public void initViews(){
        realm = Realm.getDefaultInstance();
        add = findViewById(R.id.add);
        clear = findViewById(R.id.remove);
        userList = findViewById(R.id.recyclerView);
        back = findViewById(R.id.back);
        add.setOnClickListener(v -> add());
        clear.setOnClickListener(v -> clear());
        back.setOnClickListener(v -> back());

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        userList.setLayoutManager(mLayoutManager);

        realm = Realm.getDefaultInstance();
        RealmResults<User> list = realm.where(User.class).findAll();
        UserAdapter adapter = new UserAdapter(this, list, true);
        userList.setAdapter(adapter);
        int spacingInPixels = 16;
        userList.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
    }


    public void add(){
        Intent intent = new Intent(this, RegisterPage.class);
        startActivity(intent);
    }


    public void clear(){
        new AlertDialog.Builder(this)
                .setTitle("Clear all users.")
                .setMessage("Are you sure you want to clear all users?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        RealmResults<User> results = realm.where(User.class).findAll();
                        realm.beginTransaction();
                        for (User user:results){
                            user.deleteFromRealm();
                        }
                        realm.commitTransaction();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .show();
    }


    public void delete(User user)
    {
        if (user.isValid())
        {
            realm.beginTransaction();
            user.deleteFromRealm();
            realm.commitTransaction();
        }
    }


    public void back(){
        finish();
    }
}