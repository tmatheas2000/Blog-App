package com.matheas.blog.Data;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.matheas.blog.Activities.ImageViewActivity;
import com.matheas.blog.Model.Blog;
import com.matheas.blog.Model.Users;
import com.matheas.blog.R;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import static androidx.core.content.ContextCompat.startActivities;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Blog> blogList;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabase;

    public BlogRecyclerAdapter(Context context, List<Blog> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mAuth= FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.post_row,parent,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Blog blog=blogList.get(position);
        String user=blog.getUserid();
        String imageUrl=null;
        holder.title.setText("Title : "+blog.getTitle());
        holder.desc.setText("Description : "+blog.getDescription());
        java.text.DateFormat dateFormat=java.text.DateFormat.getDateInstance();
        String formattedDate=dateFormat.format(new Date(Long.valueOf(blog.getTimestamp())).getTime());
        holder.timestamp.setText("Posted On : "+formattedDate);
        imageUrl=blog.getImage();
        Picasso.get().load(imageUrl).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView desc;
        public TextView timestamp;
        public ImageView image;
        public TextView owner;
        String userid;

        public ViewHolder(@NonNull final View view, final Context ctx) {
            super(view);
            context=ctx;
            title=(TextView) view.findViewById(R.id.postTitleList);
            desc=(TextView) view.findViewById(R.id.postTextList);
            image=(ImageView) view.findViewById(R.id.postImageList);
            timestamp=(TextView) view.findViewById(R.id.timestampList);
            userid=null;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Blog blog=blogList.get(getAdapterPosition());
                    String img=blog.getImage();
                    Intent i=new Intent(ctx, ImageViewActivity.class);
                    Bundle mBundle=new Bundle();
                    mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
                    mBundle.putSerializable("imageUrl",img);
                    mBundle.putSerializable("title",blog.getTitle());
                    mBundle.putSerializable("user",blog.getUserid());
                    mBundle.putSerializable("id",blog.getBlogid());

                    i.putExtras(mBundle);
                    ctx.startActivity(i);
                }
            });

        }
    }
}
