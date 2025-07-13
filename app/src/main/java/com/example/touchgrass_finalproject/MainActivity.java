package com.example.touchgrass_finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_main);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            initViews();
        }

        private EditText editUser, editPassword;
        private Button signIn, admin, clear, register;
        private CheckBox checkBox;
        private Realm realm;


        public void onDestroy() {
            super.onDestroy();

            if (!realm.isClosed()) {
                realm.close();
            }
        }


        public void initViews() {
            realm = Realm.getDefaultInstance();
            editUser = findViewById(R.id.editUser);
            editPassword = findViewById(R.id.editPassword);
            signIn = findViewById(R.id.signIn);
            admin = findViewById(R.id.admin);
            clear = findViewById(R.id.clear);
            register = findViewById(R.id.register);
            checkBox = findViewById(R.id.checkBox);
            SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
            boolean remember = prefs.getBoolean("rememberMe", false);
            if (remember) {
                String savedUser = prefs.getString("username", "");
                String savedPass = prefs.getString("password", "");

                editUser.setText(savedUser);
                editPassword.setText(savedPass);
                checkBox.setChecked(true);
            }
            admin.setOnClickListener(v -> admin());
            register.setOnClickListener(v -> register());
            signIn.setOnClickListener(v -> signIn());
            clear.setOnClickListener(v -> clear());
        }


        public void signIn() {
            String name = editUser.getText().toString();
            String password = editPassword.getText().toString();
            User existingUser = realm.where(User.class)
                    .equalTo("name", name)
                    .findFirst();

            if (existingUser == null) {
                Toast.makeText(this, "No User found", Toast.LENGTH_LONG).show();
                return;
            }

            if (!existingUser.getPassword().equals(password)) {
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_LONG).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("uuid", existingUser.getUuid());
            edit.putString("username", name);
            edit.putString("password", password);
            edit.putBoolean("rememberMe", checkBox.isChecked());
            edit.apply();

            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
        }


        public void admin() {
            Intent intent = new Intent(this, UserManagementPage.class);
            startActivity(intent);
        }

        public void register() {
            Intent intent = new Intent(this, RegisterPage.class);
            startActivity(intent);
        }


        public void clear() {
            editUser.setText("");
            editPassword.setText("");
            checkBox.setChecked(false);
            SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();
            edit.clear();
            edit.apply();
            Toast.makeText(this, "Preferences Cleared", Toast.LENGTH_LONG).show();
        }
}
