package com.hugoguillin.notifyme;

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

    private Button boton;
    private Button update;
    private Button cancel;
    private NotificationManager manager;
    private NotificationReceiver mReceiver = new NotificationReceiver();

    private static final String PRIMARY_CHANNEL_ID = "primary_notifications_channel";
    private static final int NOTIFICATION_ID = 0;
    private static final String ACTION_UPDATE_NOTIFICATION =
            "com.hugoguillin.notifyme.ACTION_UPDATE_NOTIFICATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boton = findViewById(R.id.notify);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });
        createNotificationChannel();

        update = findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNotification();
            }
        });

        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelNotification();
            }
        });

        setNotificationButtonState(true, false, false);
        registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private NotificationCompat.Builder getNotificationBuilder(){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("You've been notified!")
                .setContentText("This is your notification text")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_android);
        return  notifyBuilder;
    }

    public void sendNotification(){
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this,
                NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.addAction(R.drawable.ic_update, "Update Notification", updatePendingIntent);
        manager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false, true, true);
    }

    public void updateNotification(){
        Bitmap imagen = BitmapFactory.decodeResource(getResources(), R.drawable.mascot_1);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(imagen)
                        .setBigContentTitle("Notification Updated!"));
        manager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false, false, true);
    }

    public void cancelNotification(){
        manager.cancel(NOTIFICATION_ID);
        setNotificationButtonState(true, false, false);
    }

    public void createNotificationChannel(){
        manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Mascot Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            manager.createNotificationChannel(notificationChannel);
        }
    }

    private void setNotificationButtonState(Boolean notifyActivo, Boolean updateActivo, Boolean cancelActivo){
        boton.setEnabled(notifyActivo);
        update.setEnabled(updateActivo);
        cancel.setEnabled(cancelActivo);
    }

    public class NotificationReceiver extends BroadcastReceiver{

        public NotificationReceiver(){}

        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotification();
        }
    }
}
