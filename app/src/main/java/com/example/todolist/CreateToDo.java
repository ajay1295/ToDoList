package com.example.todolist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Constants;

public class CreateToDo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_to_do);

        final EditText txtTask = findViewById(R.id.txtTask);
        Button btnCreate = findViewById(R.id.btnCreate);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("todo");

       // myRef.setValue("Hello");

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String taskName = txtTask.getText().toString();
                String todoId = myRef.push().getKey();
                TaskModel taskModel = new TaskModel(taskName,false);
                myRef.child(todoId).setValue(taskModel);
            }
        });
    }
}
