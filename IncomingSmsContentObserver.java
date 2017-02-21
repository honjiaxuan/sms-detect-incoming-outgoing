package com.sms.detect;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Date;


public class IncomingSmsContentObserver extends Service {
    private ContentResolver contentResolver;
    private SmsObserver smsObserver;
    private AlarmManager alarmManager;
    private boolean initialized;  // to check is the service initialized 




    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SmsContentObserver","service started");
        super.onStartCommand(intent,flags,startId);
        if (!initialized) {
            initializeService();
            scheduleAlarm();
        }

        return START_STICKY;
    }


//to restart service 
    @Override
    public void onDestroy() {
        restartService();
        super.onDestroy();
    }

//to restart service 
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        restartService();
    }
    
//to initialize the service
    private void initializeService() {
        if (contentResolver == null && smsObserver == null) {
            this.contentResolver = getContentResolver();
            Handler handler = new Handler();
            SharedPreferences preferences = getSharedPreferences("sms_preferences", MODE_PRIVATE);
            SmsCursorParser smsCursorParser = new SmsCursorParser(preferences);
            this.smsObserver = new SmsObserver(contentResolver, handler, smsCursorParser,getApplicationContext());
        }
        Uri smsUri = Uri.parse("content://sms");
        contentResolver.registerContentObserver(smsUri, true, smsObserver);
        contentResolver.notifyChange(smsUri,smsObserver);
        initialized = true;
    }

    private void restartService() {
        contentResolver.unregisterContentObserver(smsObserver);
        Intent intent = new Intent(this, IncomingSmsContentObserver.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        long now = new Date().getTime();
        //2 second wait
        getAlarmManager().set(AlarmManager.RTC_WAKEUP, now + 2*1000, pendingIntent);
        initialized = false;

    }

    private AlarmManager getAlarmManager() {
        return alarmManager != null ? alarmManager : (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    private void scheduleAlarm(){
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), autoStart.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, 12,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        long firstMillis = java.lang.System.currentTimeMillis();
        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,firstMillis ,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, pIntent);
    }

}
