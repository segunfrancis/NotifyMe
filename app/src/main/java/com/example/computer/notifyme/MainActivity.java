package com.example.computer.notifyme;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button buttonNotify;
    private Button buttonCancel;
    private Button buttonUpdate;
    private Button buttonInboxStyleUpdate;

    private NotificationManager mNotifyManager;
    private NotificationReceiver mReceiver = new NotificationReceiver();

    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final int NOTIFICATION_ID = 0;
    private static final String ACTION_UPDATE_NOTIFICATION = "com.example.computer.notifyme.ACTION_UPDATE_NOTIFICATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonNotify = findViewById(R.id.notify);
        buttonNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });

        buttonCancel = findViewById(R.id.cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelNotification();
            }
        });

        buttonUpdate = findViewById(R.id.update);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNotification();
            }
        });

        buttonInboxStyleUpdate = findViewById(R.id.inbox_style_update);
        buttonInboxStyleUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inboxStyleUpdateNotification();
            }
        });

        // register broadcast receiver
        registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));

        createNotificationChannel();
        setNotificationButtonState(true, false, false, false);
    }

    // Toggle button state
    void setNotificationButtonState(Boolean isNotifyEnabled, Boolean isUpdateEnabled, Boolean isCancelEnabled, Boolean isInboxStyleEnabled) {
        buttonNotify.setEnabled(isNotifyEnabled);
        buttonUpdate.setEnabled(isUpdateEnabled);
        buttonCancel.setEnabled(isCancelEnabled);
        buttonInboxStyleUpdate.setEnabled(isInboxStyleEnabled);
    }

    public void sendNotification() {
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this,
                NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.addAction(R.drawable.ic_update, "Update Notification", updatePendingIntent);
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false, true, true, true);
    }

    public void createNotificationChannel() {
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // check if device API version is greater or equal to 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create notification channel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Application Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification for Application");
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("You've been notified!")
                .setContentText("This is your notification text.")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(notificationPendingIntent)
                /*  HIGH or MAX priority notifications are delivered as "heads up" notifications,
                 which drop down on top of the user's active screen */
                /*  The high-priority notification will not drop down in front of the active
                screen unless both the priority and the defaults are set.
                Setting the priority alone is not enough. */
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                // setting autoCancel to true closes notification when user taps on it
                .setAutoCancel(true);
        return notifyBuilder;
    }

    public void updateNotification() {
        Bitmap androidImage = BitmapFactory.decodeResource(getResources(), R.drawable.mascot_1);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage).setBigContentTitle("Notification Updated!"));
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false, false, true, false);
    }

    public void inboxStyleUpdateNotification() {
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.setStyle(new NotificationCompat.InboxStyle()
                .addLine("First Line").addLine("Second Line").addLine("Third Line")
                .setBigContentTitle("Notification Updated!")
                .setSummaryText("The inbox style notification looks like"));
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false, false, true, false);
    }

    public void cancelNotification() {
        mNotifyManager.cancel(NOTIFICATION_ID);
        setNotificationButtonState(true, false, false, true);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public class NotificationReceiver extends BroadcastReceiver {
        public NotificationReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotification();
        }
    }

}
