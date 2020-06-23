package com.matheas.blog.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.matheas.blog.Model.Blog;
import com.matheas.blog.R;

import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    private ImageButton mPostImage;
    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mSubmitButton;
    private StorageReference mStorage;
    private DatabaseReference mPostDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog mProgress;
    private Uri mImageUri;
    private static final int GALLERY_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        mProgress=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();mUser=mAuth.getCurrentUser();
        mStorage= FirebaseStorage.getInstance().getReference();
        mPostDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");

        mPostImage=(ImageButton) findViewById(R.id.imageButton);
        mPostTitle=(EditText) findViewById(R.id.postTitleEt);
        mPostDesc=(EditText) findViewById(R.id.descriptionEt);
        mSubmitButton=(Button) findViewById(R.id.submitPost);

        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_CODE);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Posting to our database
                startPosting();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_CODE && resultCode==RESULT_OK)
        {
            mImageUri=data.getData();
            mPostImage.setImageURI(mImageUri);
            mPostImage.setBackground(null);
        }
    }

    private void startPosting() {
        mProgress.setMessage("Posting to blog...");

        final String titleVal=mPostTitle.getText().toString().trim();
        final String descVal=mPostDesc.getText().toString().trim();

        if(!TextUtils.isEmpty(titleVal)
                && !TextUtils.isEmpty(descVal)
                &&  mImageUri !=null){
            // start the uploading

            mProgress.show();
            final StorageReference filepath=mStorage.child("Blog_images").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri>  then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        DatabaseReference newPost =  mPostDatabase.push();

                        Map<String,String> dataToSave = new HashMap<>();
                        dataToSave.put("title",titleVal);
                        dataToSave.put("desc",descVal);
                        dataToSave.put("image",downloadUri.toString());
                        dataToSave.put("timestamp",String.valueOf(System.currentTimeMillis()));
                        dataToSave.put("userid",mUser.getUid());
                        newPost.setValue(dataToSave);
                        mProgress.dismiss();
                        startActivity(new Intent(AddPostActivity.this,PostListActivity.class));
                        finish();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(getApplicationContext(),"All fields are required !",Toast.LENGTH_LONG).show();
        }
    }


}