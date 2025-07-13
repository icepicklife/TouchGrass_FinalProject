package com.example.touchgrass_finalproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.realm.Realm;

public class RegisterPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
    }


    private EditText editUser2, editPassword2, editConfirmPassword;
    private Button save, cancel;
    private Realm realm;
    private TextView title;


    public void onDestroy() {
        super.onDestroy();

        if (!realm.isClosed()) {
            realm.close();
        }
    }


    public void initViews(){
        realm = Realm.getDefaultInstance();
        String userId = getIntent().getStringExtra("user_id");
        editUser2 = findViewById(R.id.editUser2);
        editPassword2 = findViewById(R.id.editPassword2);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
        title = findViewById(R.id.REGISTERTitle);
        if(userId != null){
            User user = realm.where(User.class).equalTo("uuid", userId).findFirst();
            if (user != null) {
                editUser2.setText(user.getName());
                editPassword2.setText(user.getPassword());
                editConfirmPassword.setText(user.getPassword());
                title.setText("EDIT");
            }
        }
        save.setOnClickListener(v -> save());
        cancel.setOnClickListener(v -> cancel());
    }


    public void save(){
        String name = editUser2.getText().toString();
        String password = editPassword2.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();


        if (name.isEmpty()){
            Toast.makeText(this,"Name must not be blank.",Toast.LENGTH_LONG).show();
            return;
        }

        if (password.isEmpty() || confirmPassword.isEmpty()){
            Toast.makeText(this,"Password must not be blank.",Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(confirmPassword)){
            Toast.makeText(this,"Confirm password does not match.",Toast.LENGTH_LONG).show();
            return;
        }

        String userId = getIntent().getStringExtra("user_id");
        realm.beginTransaction();
        if (userId != null) {
            User user = realm.where(User.class).equalTo("uuid", userId).findFirst();

            if (user != null) {
                user.setName(name);
                user.setPassword(password);

                Toast.makeText(this, "User updated", Toast.LENGTH_SHORT).show();
            }
        } else {
            User existingUser = realm.where(User.class)
                    .equalTo("name", name)
                    .findFirst();
            if (existingUser != null) {
                Toast.makeText(this, "User already exists", Toast.LENGTH_LONG).show();
                return;
            }
            User newUser = new User();
            newUser.setName(name);
            newUser.setPassword(password);
            realm.copyToRealm(newUser);
            long totalCount = realm.where(User.class).count();
            Toast.makeText(this, "Saved. Total users: " + totalCount, Toast.LENGTH_LONG).show();
        }

        realm.commitTransaction();
        finish();
    }

    public void cancel(){
        finish();
    }
}