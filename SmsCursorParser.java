package com.sms.detect;


import android.content.SharedPreferences;
import android.database.Cursor;
import java.util.Date;

class SmsCursorParser {

    private SharedPreferences preferences;

       SmsCursorParser(SharedPreferences preferences) {
        if (preferences == null) {
            throw new IllegalArgumentException("SharedPreferences param can't be null");
        }
        this.preferences = preferences;
    }

    boolean parse(Cursor cursor) {

        if (!canHandleCursor(cursor) || !cursor.moveToNext()) {
            return false;
        }

        int smsId = cursor.getInt(cursor.getColumnIndex("_id"));
        String date = cursor.getString(cursor.getColumnIndex("date"));
        Date smsDate = new Date(Long.parseLong(date));

        if (shouldParseSms(smsId, smsDate)) {
            updateLastSmsParsed(smsId);
        } else {
            return false;
        }
        return true;
    }

    private void updateLastSmsParsed(int smsId) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("last_sms_parsed", smsId);
        editor.apply();
    }

    private boolean shouldParseSms(int smsId, Date smsDate) {
        boolean isFirstSmsParsed = isFirstSmsParsed();
        boolean isOld = isOld(smsDate);
        boolean shouldParseId = shouldParseSmsId(smsId);
        return (isFirstSmsParsed && !isOld) || (!isFirstSmsParsed && shouldParseId);
    }

    private boolean isOld(Date smsDate) {
        Date now = new Date();
        return now.getTime() - smsDate.getTime() > 5000;
    }

    private boolean shouldParseSmsId(int smsId) {
        if (getLastSmsIntercepted() == -1) {
            return false;
        }
        int lastSmsIdIntercepted = getLastSmsIntercepted();
        return smsId > lastSmsIdIntercepted;
    }

    private int getLastSmsIntercepted() {
        return preferences.getInt("last_sms_parsed", -1);
    }

    private boolean isFirstSmsParsed() {
        return getLastSmsIntercepted() == -1;
    }

    private boolean canHandleCursor(Cursor cursor) {
        return cursor != null && cursor.getCount() > 0;
    }
}
