package com.example.todolist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class Notify extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        notificationHelper.SendNotification(title,message);
    }
}
