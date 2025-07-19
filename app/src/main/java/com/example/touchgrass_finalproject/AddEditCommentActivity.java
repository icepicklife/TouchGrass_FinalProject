package com.example.touchgrass_finalproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.UUID;

import io.realm.Realm;

public class AddEditCommentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_comment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();

    }

    private EditText CommentBoxInput;
    private Realm realm;
    private String CommentIDtoEdit;
    private String PostID;
    private ImageButton PostButton;
    private ImageView back;

    public void initViews(){
        CommentBoxInput = findViewById(R.id.CommentBoxInput);
        realm = Realm.getDefaultInstance();

        PostID = getIntent().getStringExtra("post_id");
        CommentIDtoEdit = getIntent().getStringExtra("commentID");

        if (CommentIDtoEdit != null) {

            Comment existingComment = realm.where(Comment.class)
                    .equalTo("uuid", CommentIDtoEdit)
                    .findFirst();

            if (existingComment != null) {

                CommentBoxInput.setText(existingComment.getCommentContent());

            }
        }
        back = findViewById(R.id.goBackReturn2);
        PostButton = findViewById(R.id.CommentPostButton);
        PostButton.setOnClickListener(v -> OnPostCommentClick());
        back.setOnClickListener(v -> goBack());
    }

    public void OnPostCommentClick() {

        String comment_content = CommentBoxInput.getText().toString();
        if (TextUtils.isEmpty(comment_content)) {

            Toast empty_comment_prompt = Toast.makeText(this, "Comment can't be empty.",Toast.LENGTH_SHORT);
            empty_comment_prompt.show();
            return;

        }

        realm.executeTransaction(r -> {
            Comment comment;
            if (CommentIDtoEdit != null) {
                comment = r.where(Comment.class)
                        .equalTo("uuid", CommentIDtoEdit)
                        .findFirst();
            } else {
                comment = r.createObject(Comment.class, UUID.randomUUID().toString());
                comment.setPostID(PostID);
                comment.setUserID(CurrentUserSession.getUserID(this));
                comment.setUsername(CurrentUserSession.getUsername(this));
                comment.setUserProfPic_url(CurrentUserSession.getUserID(this) + ".jpeg");
            }
            if (comment != null) {
                comment.setCommentContent(comment_content);
            }
        });

        Toast comment_posted_prompt = Toast.makeText(this, "Comment posted!", Toast.LENGTH_SHORT);
        comment_posted_prompt.show();

        finish();
    }

    public void goBack(){
        finish();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        realm.close();

    }
}