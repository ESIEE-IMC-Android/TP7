package com.example.nadar.velibservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.concurrent.atomic.AtomicLong;

public class ServiceStation extends Service {
    private NotificationManager mNM;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.local_service_started;
    private static Thread ticker;
    final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        ticker = new Thread(new Ticker());

        ticker.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNM.cancel(NOTIFICATION);

    }


    private class Ticker implements Runnable {

        @Override
        public void run() {
            Log.i("Ticker Interrupted?", ticker.isInterrupted() + "");
            while (!ticker.isInterrupted()) {
                try {
                    Thread.sleep(1000);
//                    callNotification();
                    showNotification();
                } catch (Exception e) {
                    return;
                }
            }
        }
    }


    public class LocalBinder extends Binder {
        ServiceStation getService() {
            Log.i("ServiceStation", "getService");
            return ServiceStation.this;
        }
    }

    private void showNotification() {
        SharedPreferences sharedPreferences = getSharedPreferences("ahmad", 0);

        Log.e("callNotification", sharedPreferences.getString("slots", "please choose station"));

        CharSequence text = sharedPreferences.getString("slots", "please choose station");


        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            /* Create or update. */
            NotificationChannel channel = new NotificationChannel("my_channel_01",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNM.createNotificationChannel(channel);
        }
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this,"my_channel_01")
                .setSmallIcon(R.mipmap.ic_launcher)  // the status icon
                .setContentTitle("Velib service")  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  ;// The intent to send when the entry is clicked


        // Send the notification.
        mNM.notify(R.string.local_service_started, notification.build());
    }
}
