package com.sms.detect;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.Date;


/**
 * ContentObserver created to handle the sms content provider changes. This entity will be called each time the
 * system changes the sms content provider state.
 * <p/>
 * SmsObserver will analyze the sms inbox and sent content providers to get the sms information and will notify
 * SmsListener.
 * <p/>
 * The content observer will be called each time the sms content provider be updated. This means that all
 * the sms state changes will be notified. For example, when the sms state change from SENDING to SENT state.
 *
 */
class SmsObserver extends ContentObserver {



    private final Uri SMS_URI = Uri.parse("content://sms/");
    private final ContentResolver contentResolver;
    private final SmsCursorParser smsCursorParser;


    SmsObserver(ContentResolver contentResolver, Handler handler, SmsCursorParser smsCursorParser, Context context) {
        super(handler);
        this.contentResolver = contentResolver;
        this.smsCursorParser = smsCursorParser;
    }


    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(SMS_URI, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                processSms(cursor);
            }
        } finally {
            close(cursor);
        }
    }

    private void processSms(Cursor cursor) {
        Cursor smsCursor = null;
        try {
            String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
            smsCursor = getSmsDetailsCursor(protocol);
            smsCursor.moveToFirst();
            String add = smsCursor.getString(cursor.getColumnIndex("address"));
            String date = smsCursor.getString(cursor.getColumnIndex("date"));
            String date1 = DateFormat.format("yyyyMMdd,HH:mm:ss", new Date(Long.parseLong(date))).toString();
            String msg = smsCursor.getString(cursor.getColumnIndex("body"));
            int type = Integer.valueOf(cursor.getString(smsCursor.getColumnIndex("type")));
            boolean boolean1 = smsCursorParser.parse(cursor);
            if (boolean1) {
                String smsContent;
                if (type == 1) {
                    smsContent = "received from " + add + ". Content is " + msg + "." ;
                } else if (type == 2 || type == 4){
                    smsContent = "sent to " + add + ". Content is " + msg + "." ;
                } else {
                    smsContent = "related to " + add + ". Content is " + msg + "." ;
                }
                Log.d("SmsObserver", "content is " + smsContent + ". Date is " + date1);
               
            }
        } finally {
            close(smsCursor);
        }
    }

    private Cursor getSmsDetailsCursor(String protocol) {
        Cursor smsCursor;
        if (protocol == null) {
            //SMS Sent
            smsCursor = getSmsDetailsCursor(Uri.parse("content://sms/sent"));
        } else {
            //SMSReceived
            smsCursor = getSmsDetailsCursor(Uri.parse("content://sms/inbox"));
        }
        return smsCursor;
    }

    private Cursor getSmsDetailsCursor(Uri smsUri) {
        return smsUri != null ? this.contentResolver.query(smsUri, null, null, null, "date DESC") : null;
    }

    private void close(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }
}
