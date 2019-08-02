package com.example.todolist;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> taskModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("todo");

        FloatingActionButton btnCreate = findViewById(R.id.btnCreate);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        final TodoAdapter todoAdapter = new TodoAdapter();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(todoAdapter);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,CreateToDo.class));
            }
        });

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String task = (String) dataSnapshot.child("task").getValue();
                taskModelArrayList.add(task);
                todoAdapter.notifyDataSetChanged();
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
    }

    private class TodoAdapter extends RecyclerView.Adapter<TodoAdapterViewHolder>{

        @NonNull
        @Override
        public TodoAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_todo,viewGroup,false);
            return new TodoAdapterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TodoAdapterViewHolder todoAdapterViewHolder, int i) {
            String taskName = taskModelArrayList.get(i);

            todoAdapterViewHolder.txtTaskName.setText(taskName);
        }

        @Override
        public int getItemCount() {
            return taskModelArrayList.size();
        }
    }

    private class TodoAdapterViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtTaskName;
        public TodoAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTaskName = itemView.findViewById(R.id.txtTaskName);
        }
    }
}
