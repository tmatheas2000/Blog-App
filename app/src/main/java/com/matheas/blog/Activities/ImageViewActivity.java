package com.matheas.blog.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.matheas.blog.Data.BlogRecyclerAdapter;
import com.matheas.blog.Model.Blog;
import com.matheas.blog.R;
import com.squareup.picasso.Picasso;

import java.security.cert.Extension;
import java.util.Collections;
import java.util.List;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ImageViewActivity extends AppCompatActivity {

    private static final int WRITE_PERMISSION = 1001;
    private ImageView postImagedet;
    private String user;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String imgUrl;
    private StorageReference mStorage;
    private DatabaseReference mDeleteDatabase;
    private String id;
    private String title;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        title=(String) getIntent().getSerializableExtra("title");
        setTitle(title);
        mAuth= FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        user=(String) getIntent().getSerializableExtra("user");
        mDeleteDatabase=FirebaseDatabase.getInstance().getReference().child("Blog");
        id=(String) getIntent().getSerializableExtra("id");
        postImagedet=(ImageView) findViewById(R.id.postImagedet);
        imgUrl= (String) getIntent().getSerializableExtra("imageUrl");
        mStorage=FirebaseStorage.getInstance().getReferenceFromUrl(imgUrl);
        Picasso.get().load(imgUrl).into(postImagedet);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(user.equals(mUser.getUid()))
        {
            getMenuInflater().inflate(R.menu.post_menu,menu);
        }
        else
        {
            getMenuInflater().inflate(R.menu.download_menu,menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_delete:
                AlertDialog.Builder alert = new AlertDialog.Builder(ImageViewActivity.this);
                alert.setTitle("Delete");
                alert.setMessage("Please confirm to Delete ?");
                alert.setNegativeButton("No", null);
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDeleteDatabase.child(id).removeValue();
                        mStorage.delete();
                        Toast.makeText(ImageViewActivity.this, "Deleted Successfully !!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ImageViewActivity.this, PostListActivity.class));
                        finish();
                    }
                });
                alert.show();
                break;
            case R.id.action_download:
                mStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if(ContextCompat.checkSelfPermission(ImageViewActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                            {
                                url=uri.toString();
                                downloadFile(ImageViewActivity.this,title,".jpg",url);
                            }else
                            {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_PERMISSION);
                            }
                        }
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void downloadFile(Context context,String fileName,String fileExtension,String url) {

        DownloadManager downloadManager=(DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri=Uri.parse(url);
        DownloadManager.Request request=new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS,fileName+ fileExtension);
        downloadManager.enqueue(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==WRITE_PERMISSION)
        {
            if(grantResults.length>0&& grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Permissison Denied !!",Toast.LENGTH_LONG).show();
            }
        }
    }
}