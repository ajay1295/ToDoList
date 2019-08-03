package com.example.todolist;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private ArrayList<TodoModel> taskModelArrayList = new ArrayList<>();

    public static String PRIMARY_CHANNEL_ID;
    public static NotificationManager mNotifyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("todo");

        PRIMARY_CHANNEL_ID = getString(R.string.default_notification_channel_id);

        NotificationChannel();

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
                String key = dataSnapshot.getKey();
                TodoModel todoModel = new TodoModel(key,task);
                taskModelArrayList.add(todoModel);
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
            String taskName = taskModelArrayList.get(i).getTask();

            todoAdapterViewHolder.txtTaskName.setText(taskName);
        }

        @Override
        public int getItemCount() {
            return taskModelArrayList.size();
        }
    }

    private class TodoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txtTaskName;
        private final ImageButton btnRemind;
        private final ImageButton btnComment;
        public TodoAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTaskName = itemView.findViewById(R.id.txtTaskName);
            btnRemind = itemView.findViewById(R.id.btnRemind);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnRemind.setOnClickListener(this);
            btnComment.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (view.getId()==R.id.btnRemind){

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.HOUR_OF_DAY, 9);

                Date myDate = new Date();
                int requestCode = Integer.parseInt(new SimpleDateFormat("ddhhmmss").format(myDate));

                Intent intent = new Intent(MainActivity.this, Notify.class);
                intent.putExtra("title","Daily Reminder");
                intent.putExtra("message",taskModelArrayList.get(position).getTask());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,
                        requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarms = (AlarmManager) getSystemService(
                        Context.ALARM_SERVICE);
                alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                Toast.makeText(MainActivity.this, "Daily Reminder Set", Toast.LENGTH_SHORT).show();
            }else if (view.getId() == R.id.btnComment){
                startActivity(new Intent(MainActivity.this,Comments.class).putExtra("key",taskModelArrayList.get(position).getKey()));
            }
        }
    }


    private void NotificationChannel(){
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            NotificationChannel mChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,"Digimkey", NotificationManager.IMPORTANCE_HIGH);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.CYAN);
            mChannel.setDescription("Notification From To Do List");
            mNotifyManager.createNotificationChannel(mChannel);
        }
    }

    private class TodoModel{
        String key;
        String task;

        public TodoModel(String key, String task) {
            this.key = key;
            this.task = task;
        }

        public String getKey() {
            return key;
        }

        public String getTask() {
            return task;
        }
    }
}
