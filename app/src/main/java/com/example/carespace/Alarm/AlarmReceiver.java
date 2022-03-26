package com.example.carespace.Alarm;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.carespace.R;

public class AlarmReceiver extends BroadcastReceiver {

    //const vars
    private static final String WATER_ALARM = "water_alarm";
    private static final String MEDICINE_ALARM = "medicine_alarm";
    private static final String CUSTOM_ALARM = "custom_alarm";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        NotificationCompat.Builder builder = null;

        Intent i = new Intent(context,Alarm.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        String alarm_type = intent.getStringExtra("alarm_type");
        int pending_id = intent.getIntExtra("pending_ID",-1);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,pending_id,i,0);


        if (alarm_type.equals(WATER_ALARM))
        {
            //notification layout
            builder = new NotificationCompat.Builder(context, "foxandroid")
                    .setSmallIcon(R.drawable.ic_alarm)
                    .setContentTitle("CareSpace")
                    .setContentText("Time to get hydrated !! Drink !!")
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);
        }
        else if (alarm_type.equals(MEDICINE_ALARM))
        {
            //notification layout
            builder = new NotificationCompat.Builder(context, "foxandroid")
                    .setSmallIcon(R.drawable.ic_alarm)
                    .setContentTitle("CareSpace")
                    .setContentText("Don't forget to eat your medicine ~~")
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);
        }
        else if (alarm_type.equals(CUSTOM_ALARM))
        {
            String alarm_description = intent.getStringExtra("alarm_description");

            //notification layout
            builder = new NotificationCompat.Builder(context, "foxandroid")
                    .setSmallIcon(R.drawable.ic_alarm)
                    .setContentTitle("CareSpace")
                    .setContentText(alarm_description)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);
        }

        //call notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123,builder.build());
    }
}
