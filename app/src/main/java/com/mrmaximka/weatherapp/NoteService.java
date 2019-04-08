package com.mrmaximka.weatherapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class NoteService extends IntentService {

    private int messageId = 0;

    public NoteService() {
        super("NoteService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        makeNote("Изменение температуры");
    }

    private void makeNote(String message){

        NotificationCompat.Builder builder;
        NotificationChannel mChannel = null;
        String CHANNEL_ID = "my_channel_01";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {     // Для API Oreo и выше
            CharSequence name = "Test";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Main service notification")
                    .setContentText(message)
                    .setChannelId(CHANNEL_ID);
        }
        else {                              // Остальные API
            builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Main service notification")
                    .setContentText(message);
        }


        Intent resultIntent = new Intent(this, NoteService.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {   // Для API Oreo и выше
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(messageId++, builder.build());
        onDestroy();
    }

}
