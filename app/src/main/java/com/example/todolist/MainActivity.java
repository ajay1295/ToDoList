package com.example.todolist;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ArrayList<TodoModel> taskModelArrayList = new ArrayList<>();

    public static String PRIMARY_CHANNEL_ID;
    public static NotificationManager mNotifyManager;

    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("todo");

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
                startActivity(new Intent(MainActivity.this, CreateTask.class));
            }
        });

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String task = (String) dataSnapshot.child("task").getValue();
                boolean isCompleted = (boolean) dataSnapshot.child("isCompleted").getValue();
                String key = dataSnapshot.getKey();
                TodoModel todoModel = new TodoModel(key,task,isCompleted);
                taskModelArrayList.add(todoModel);
                todoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                boolean isCompleted = (boolean) dataSnapshot.child("isCompleted").getValue();
                String key = dataSnapshot.getKey();
                for (TodoModel todoModel : taskModelArrayList){
                    if (todoModel.getKey().equals(key)){
                        todoModel.setCompelted(isCompleted);
                    }
                }
                todoAdapter.notifyDataSetChanged();
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
            boolean isCompleted = taskModelArrayList.get(i).isCompelted();

            todoAdapterViewHolder.txtTaskName.setText(taskName);
            if (isCompleted){
                todoAdapterViewHolder.chkTask.setChecked(true);
            }else {
                todoAdapterViewHolder.chkTask.setChecked(false);
            }
        }

        @Override
        public int getItemCount() {
            return taskModelArrayList.size();
        }
    }

    private class TodoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        private final TextView txtTaskName;
        private final ImageButton btnRemind;
        private final ImageButton btnComment;
        private final ImageButton btnAddAlarm;
        private final CheckBox chkTask;
        public TodoAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTaskName = itemView.findViewById(R.id.txtTaskName);
            btnRemind = itemView.findViewById(R.id.btnRemind);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnAddAlarm = itemView.findViewById(R.id.btnTime);
            chkTask = itemView.findViewById(R.id.chkTask);
            btnRemind.setOnClickListener(this);
            btnComment.setOnClickListener(this);
            btnAddAlarm.setOnClickListener(this);
            chkTask.setOnCheckedChangeListener(this);
        }

        @Override
        public void onClick(View view) {
            final int position = getAdapterPosition();
            Date myDate = new Date();
            final int requestCode = Integer.parseInt(new SimpleDateFormat("ddhhmmss").format(myDate));
            if (view.getId()==R.id.btnRemind){

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 10);
                calendar.set(Calendar.MINUTE, 45);

                Intent intent = new Intent(getApplicationContext(), Notify.class);
                intent.putExtra("title","Daily Reminder");
                intent.putExtra("message",taskModelArrayList.get(position).getTask());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),requestCode, intent, 0);
                AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarms.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                Toast.makeText(MainActivity.this, "Daily Reminder Set", Toast.LENGTH_SHORT).show();
            }else if (view.getId() == R.id.btnComment){
                Intent intent = new Intent(MainActivity.this,Comments.class);
                intent.putExtra("key",taskModelArrayList.get(position).getKey());
                intent.putExtra("task",taskModelArrayList.get(position).getTask());
                startActivity(intent);
            }else if (view.getId() == R.id.btnTime){
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY,selectedHour);
                        cal.set(Calendar.MINUTE,selectedMinute);
                        Intent intent = new Intent(getApplicationContext(), Notify.class);
                        intent.putExtra("title","Custom Time Reminder");
                        intent.putExtra("message",taskModelArrayList.get(position).getTask());
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),requestCode, intent, 0);
                        AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        alarms.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                        Toast.makeText(MainActivity.this, "Custom Reminder Set", Toast.LENGTH_SHORT).show();
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            int position = getAdapterPosition();
            String key = taskModelArrayList.get(position).getKey();
            if (isChecked){
                myRef.child(key).child("isCompleted").setValue(true);
                chkTask.setText("Completed");
            }else {
                myRef.child(key).child("isCompleted").setValue(false);
                chkTask.setText("Not Completed");
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
        boolean isCompelted;

        public TodoModel(String key, String task, boolean isCompelted) {
            this.key = key;
            this.task = task;
            this.isCompelted = isCompelted;
        }

        public String getKey() {
            return key;
        }

        public String getTask() {
            return task;
        }

        public boolean isCompelted() {
            return isCompelted;
        }

        public void setCompelted(boolean compelted) {
            isCompelted = compelted;
        }
    }
}
