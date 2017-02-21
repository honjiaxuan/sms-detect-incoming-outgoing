# sms-detect-incoming-outgoing
This is the code to detect outgoing sms and incoming sms in android OS.

For incoming sms, I use broadcast receiver and also content observer.

Broadcast receiver will show the incoming sms in log when system received the incoming sms.

For outgoing sms, I created a service which will detect the latest sent sms by using content observer.

Content observer will manually read the sms database and show the sms in log when it detected new incoming or outgoing sms.

Remember to request following permission in android manifest.

<uses-permission android:name="android.permission.RECEIVE_SMS"/> 
<uses-permission android:name="android.permission.READ_SMS"/> 

