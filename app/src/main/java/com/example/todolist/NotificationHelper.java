package com.example.todolist;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationHelper {
    private Context mContext;

    NotificationHelper(Context context) {
        mContext = context;
    }

    void SendNotification(String title,String message){
        Date myDate = new Date();
        int myNotificationId = Integer.parseInt(new SimpleDateFormat("ddhhmmss").format(myDate));
        Intent intent = new Intent(mContext,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,myNotificationId,intent,PendingIntent.FLAG_ONE_SHOT);

        String channelId = mContext.getString(R.string.default_notification_channel_id);
        String channelName = "todo";
        int imp = NotificationManager.IMPORTANCE_HIGH;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            NotificationChannel mChannel = new NotificationChannel(channelId,channelName,imp);
            notificationManager.createNotificationChannel(mChannel);

        }
        @SuppressLint("WrongConstant") NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext.getApplicationContext(),channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(defaultSoundUri)
                .setBadgeIconType(Notification.BADGE_ICON_SMALL)
                .setPriority(imp)
                .setContentIntent(pendingIntent);


        notificationManager.notify(myNotificationId,notification.build());
    }
}
