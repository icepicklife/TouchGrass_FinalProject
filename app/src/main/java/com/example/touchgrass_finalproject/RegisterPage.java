package com.example.touchgrass_finalproject;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
        checkPermissions();
    }


    public void checkPermissions()
    {
        // REQUEST PERMISSIONS for Android 6+
        // THESE PERMISSIONS SHOULD MATCH THE ONES IN THE MANIFEST
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA

                )

                .withListener(new BaseMultiplePermissionsListener()
                {
                    public void onPermissionsChecked(MultiplePermissionsReport report)
                    {
                        if (report.areAllPermissionsGranted())
                        {
                            // all permissions accepted proceed
                            initViews();
                        }
                        else
                        {
                            // notify about permissions
                            toastRequirePermissions();
                        }
                    }
                })
                .check();
    }

    public void toastRequirePermissions()
    {
        Toast.makeText(this, "You must provide permissions for app to run", Toast.LENGTH_LONG).show();
        finish();
    }

    private EditText editUser2, editPassword2, editConfirmPassword;
    private Button save, cancel;
    private Realm realm;
    private TextView title;
    private ImageView userPhoto;
    private String currentUuid;
    private User newUser;



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
        userPhoto = findViewById(R.id.userPhoto);
        userPhoto.setOnClickListener(v -> takePhoto());

        if(userId != null){
            User user = realm.where(User.class).equalTo("uuid", userId).findFirst();
            if (user != null) {
                editUser2.setText(user.getName());
                editPassword2.setText(user.getPassword());
                editConfirmPassword.setText(user.getPassword());
                title.setText("EDIT");
                currentUuid = userId;
                File imageFile = new File(getExternalCacheDir(), currentUuid + ".jpeg");
                if (imageFile.exists()) {
                    refreshImageView(userPhoto, imageFile);
                }
            }
        }else{
            newUser = new User();
            currentUuid = newUser.getUuid();
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
            newUser.setName(name);
            newUser.setPassword(password);
            realm.copyToRealmOrUpdate(newUser);
            long totalCount = realm.where(User.class).count();
            Toast.makeText(this, "Saved. Total users: " + totalCount, Toast.LENGTH_LONG).show();
        }

        realm.commitTransaction();
        finish();
    }

    public void cancel(){
        finish();
    }

    public static int REQUEST_CODE_IMAGE_SCREEN = 0;


    public void takePhoto()
    {
        Intent i = new Intent(this, ImageActivity.class);
        startActivityForResult(i, REQUEST_CODE_IMAGE_SCREEN);
    }


    // SINCE WE USE startForResult(), code will trigger this once the next screen calls finish()
    public void onActivityResult(int requestCode, int responseCode, Intent data)
    {
        super.onActivityResult(requestCode, responseCode, data);

        if (requestCode==REQUEST_CODE_IMAGE_SCREEN)
        {
            if (responseCode==ImageActivity.RESULT_CODE_IMAGE_TAKEN)
            {
                // receieve the raw JPEG data from ImageActivity
                // this can be saved to a file or save elsewhere like Realm or online
                byte[] jpeg = data.getByteArrayExtra("rawJpeg");

                try {
                    // save rawImage to file
                    File savedImage = saveFile(jpeg, currentUuid+".jpeg");  // WHERE TO SAVE

                    // load file to the image view via picasso
                    refreshImageView(userPhoto, savedImage);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }


    private File saveFile(byte[] jpeg, String filename) throws IOException
    {
        // this is the root directory for the images
        File getImageDir = getExternalCacheDir();

        // just a sample, normally you have a diff image name each time
        File savedImage = new File(getImageDir, filename);


        FileOutputStream fos = new FileOutputStream(savedImage);
        fos.write(jpeg);
        fos.close();
        return savedImage;
    }


    private void refreshImageView(ImageView imageView, File savedImage) {

        // this will put the image saved to the file system to the imageview
        Picasso.get()
                .load(savedImage)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imageView);
    }
}