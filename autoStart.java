package com.sms.detect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;


public class autoStart extends BroadcastReceiver {
    public autoStart() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() != null) {
            String intentAction = intent.getAction();
            Log.d("tag1autostart1", intent.getAction() + ".");
            if (intentAction.equals("android.provider.Telephony.SMS_RECEIVED")) {
                for (SmsMessage message : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    if (message != null) {
                        add = message.getDisplayOriginatingAddress();
                        String msg = message.getDisplayMessageBody();
                        String date1 = DateFormat.format("yyMMdd,HH:mm:ss", message.getTimestampMillis()).toString();
                        String smsContent = "received sms from " + add + ". Content: " + msg + "." + "Date:" + date1 +".";
                        Log.d("incomingSms",smsContent);
                    }
                }
            } 
        }
    }
}

