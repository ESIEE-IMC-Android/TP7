package com.example.nadar.velibservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.atomic.AtomicLong;

public class ServiceStation extends Service {

    private static Thread ticker;
    private static AtomicLong count;
    final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return  binder;
    }

    @Override
    public void onCreate(){
            count = new AtomicLong();
            ticker = new Thread( new Ticker());
            ticker.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    public long getCount(){
        return count.get();
    }

    private class Ticker implements Runnable{

        @Override
        public void run() {
            Log.i("Ticker Interrupted?",ticker.isInterrupted()+"");
            while(!ticker.isInterrupted()){
                try{
                    Thread.sleep(1000);
                    count.set(count.longValue()+1L);
                }catch(Exception e){
                    return;
                }
            }
        }
    }

    public class LocalBinder extends Binder {
        ServiceStation getService() {
            Log.i("ServiceStation","getService");
            return ServiceStation.this;
        }
    }

}
