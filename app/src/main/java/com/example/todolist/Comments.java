package com.example.todolist;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Comments extends AppCompatActivity {

    ArrayList<String> commentArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Intent intent = getIntent();
        final String key = intent.getStringExtra("key");
        String task = intent.getStringExtra("task");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(task);
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("todo").child(key).child("Comments");

        final EditText txtComment = findViewById(R.id.txtComment);
        FloatingActionButton btnSend = findViewById(R.id.btnSend);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        final CommentAdapter commentAdapter = new CommentAdapter();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(commentAdapter);

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String comment = (String) dataSnapshot.getValue();
                String key = dataSnapshot.getKey();
                commentArrayList.add(comment);
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = txtComment.getText().toString();
                String commentKey = myRef.push().getKey();
                myRef.child(commentKey).setValue(comment);
            }
        });

    }

    private class CommentAdapter extends RecyclerView.Adapter<CommentAdapterViewHolder>{

        @NonNull
        @Override
        public CommentAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment,viewGroup,false);
            return new CommentAdapterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CommentAdapterViewHolder commentAdapterViewHolder, int i) {
            String comment = commentArrayList.get(i);

            commentAdapterViewHolder.txtComment.setText(i+1+". "+comment);
        }

        @Override
        public int getItemCount() {
            return commentArrayList.size();
        }
    }

    private class CommentAdapterViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtComment;
        public CommentAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            txtComment = itemView.findViewById(R.id.txtComment);
        }
    }
}
