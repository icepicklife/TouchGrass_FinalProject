package com.example.touchgrass_finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;

public class EditProfile extends AppCompatActivity {

    private EditText editUsername, editPassword, editConfirmPassword;
    private Button saveButton, cancelButton;
    private ImageView userImage;
    private Realm realm;
    private String currentUuid;
    private User currentUser;

    public static final int REQUEST_CODE_IMAGE_SCREEN = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkPermissions();
    }

    private void checkPermissions() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                )
                .withListener(new BaseMultiplePermissionsListener() {
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            initViews();
                        } else {
                            Toast.makeText(EditProfile.this, "Permissions required.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }).check();
    }

    private void initViews() {
        realm = Realm.getDefaultInstance();

        SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
        currentUuid = prefs.getString("uuid", null);

        if (currentUuid == null) {
            Toast.makeText(this, "No logged-in user found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentUser = realm.where(User.class).equalTo("uuid", currentUuid).findFirst();
        if (currentUser == null) {
            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        editUsername = findViewById(R.id.editUser3);
        editPassword = findViewById(R.id.editPassword3);
        editConfirmPassword = findViewById(R.id.editConfirmPassword3);
        saveButton = findViewById(R.id.save2);
        cancelButton = findViewById(R.id.cancel2);
        userImage = findViewById(R.id.userPhoto2);

        editUsername.setText(currentUser.getName());
        editPassword.setText(currentUser.getPassword());
        editConfirmPassword.setText(currentUser.getPassword());

        File imageFile = new File(getExternalCacheDir(), currentUuid + ".jpeg");
        if (imageFile.exists()) {
            refreshImageView(userImage, imageFile);
        }

        userImage.setOnClickListener(v -> takePhoto());
        saveButton.setOnClickListener(v -> saveChanges());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void saveChanges() {
        String name = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        realm.executeTransaction(r -> {
            currentUser.setName(name);
            currentUser.setPassword(password);
        });

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void takePhoto() {
        Intent i = new Intent(this, ImageActivity.class);
        startActivityForResult(i, REQUEST_CODE_IMAGE_SCREEN);
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);

        if (requestCode == REQUEST_CODE_IMAGE_SCREEN && responseCode == ImageActivity.RESULT_CODE_IMAGE_TAKEN) {
            byte[] jpeg = data.getByteArrayExtra("rawJpeg");
            try {
                File savedImage = saveFile(jpeg, currentUuid + ".jpeg");
                refreshImageView(userImage, savedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private File saveFile(byte[] jpeg, String filename) throws IOException {
        File dir = getExternalCacheDir();
        File savedImage = new File(dir, filename);
        try (FileOutputStream fos = new FileOutputStream(savedImage)) {
            fos.write(jpeg);
        }
        return savedImage;
    }

    private void refreshImageView(ImageView imageView, File savedImage) {
        Picasso.get()
                .load(savedImage)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imageView);
    }

    @Override
    protected void onDestroy() {
        if (!realm.isClosed()) {
            realm.close();
        }
        super.onDestroy();
    }
}
