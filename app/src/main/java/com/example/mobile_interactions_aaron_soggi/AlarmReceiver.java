package com.example.mobile_interactions_aaron_soggi;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "SAMPLE_CHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {

       // getting Id and message from intent.
        int notificationId = intent.getIntExtra("notificationId",0);
        String message = intent.getStringExtra("message");


        //call alarm activity when notification is tapped.
        Intent mainIntent = new Intent(context, alarm_activity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                context,0,mainIntent,0
        );

        //notificationManager
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            CharSequence channel_name = "My Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,channel_name,importance);
            notificationManager.createNotificationChannel(channel);


        }

        //prepare the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Medication due")
                .setContentText(message)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        //notify
        notificationManager.notify(notificationId, builder.build());


    }
}
