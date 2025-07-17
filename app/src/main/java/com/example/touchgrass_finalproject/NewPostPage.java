package com.example.touchgrass_finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.time.LocalDate;
import java.util.Date;

import io.realm.Realm;

public class NewPostPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_post_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        checkPermissions();
    }
    public void checkPermissions()
    {
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
                            initViews();
                        }
                        else
                        {
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

    private EditText description;
    private Button post;
    private ImageView image, cancel, home, profile;
    private TextView title;
    private Realm realm;
    private Post postDetail;
    private String imageUuid;
    private String originalDescription = "";
    private boolean imageChanged, isEditing, isEdited = false;

    public void initViews(){
        realm = Realm.getDefaultInstance();
        String postId = getIntent().getStringExtra("post_id");
        description = findViewById(R.id.description);
        post = findViewById(R.id.post);
        image = findViewById(R.id.imagePost);
        title = findViewById(R.id.postTitle);
        cancel = findViewById(R.id.cancelPost);
        home = findViewById(R.id.homeButton);
        profile = findViewById(R.id.profileButton);

        if(postId!=null) {
            postDetail = realm.where(Post.class).equalTo("uuid",postId).findFirst();
            if(postDetail!=null) {
                isEditing = true;
                description.setText(postDetail.getDescription());
                originalDescription = postDetail.getDescription();
                title.setText("EDIT");
                imageUuid = postId;
                File imageFile = new File(getExternalCacheDir(), imageUuid + ".jpeg");
                if (imageFile.exists()) {
                    refreshImageView(image, imageFile);
                }
            }
        }else {
            postDetail = new Post();
            imageUuid = postDetail.getUuid();
        }

        post.setOnClickListener(v -> post());
        image.setOnClickListener(v -> takePhoto());
        cancel.setOnClickListener(v -> cancel());
        home.setOnClickListener(v -> home());
        profile.setOnClickListener(v -> goToProfile());
    }
    public void home(){
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void goToProfile(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void post(){
        String text = description.getText().toString();

        if (text.isEmpty()) {
            Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs =  getSharedPreferences("data", MODE_PRIVATE);
        String uuid = prefs.getString("uuid", null);

        User currentUser = realm.where(User.class)
                .equalTo("uuid", uuid)
                .findFirst();

        if (currentUser == null) {
            Toast.makeText(this, "Current user not found", Toast.LENGTH_SHORT).show();
            return;
        }

        realm.beginTransaction();
        postDetail.setDescription(text);
        postDetail.setDate(new Date());
        postDetail.setUser(currentUser);
        realm.copyToRealmOrUpdate(postDetail);

        if (!text.equals(originalDescription) || imageChanged) {
            isEdited = true;
        }

        realm.commitTransaction();
        Toast.makeText(this, "Posted!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, PostDetailPage.class);
        intent.putExtra("post_id", postDetail.getUuid());
        intent.putExtra("edited",isEdited);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    public static int REQUEST_CODE_IMAGE_SCREEN = 0;

    public void takePhoto(){
        Intent i = new Intent(this, ImageActivity.class);
        startActivityForResult(i, REQUEST_CODE_IMAGE_SCREEN);
    }

    public void cancel(){
        finish();
    }

    public void onActivityResult(int requestCode, int responseCode, Intent data)
    {
        super.onActivityResult(requestCode, responseCode, data);


        if (requestCode==REQUEST_CODE_IMAGE_SCREEN)
        {
            if (responseCode==ImageActivity.RESULT_CODE_IMAGE_TAKEN)
            {
                byte[] jpeg = data.getByteArrayExtra("rawJpeg");

                try {

                    File savedImage = saveFile(jpeg, imageUuid+".jpeg");  // WHERE TO SAVE

                    refreshImageView(image, savedImage);
                    if (isEditing) {
                        imageChanged = true;
                    }
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
        File getImageDir = getExternalCacheDir();
        File savedImage = new File(getImageDir, filename);
        FileOutputStream fos = new FileOutputStream(savedImage);
        fos.write(jpeg);
        fos.close();
        return savedImage;
    }


    private void refreshImageView(ImageView imageView, File savedImage) {
        Picasso.get()
                .load(savedImage)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imageView);
    }
}