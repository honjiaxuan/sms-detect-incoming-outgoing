# sms-detect-incoming-outgoing
This is the code to detect outgoing sms and incoming sms in android OS.

For incoming sms, I use broadcast receiver and also content observer.

Broadcast receiver will start the service when system received the incoming sms.

For outgoing sms, I created a service which will detect the latest sent sms by using content observer.

Content observer will manually read the sms database and start the service when it detected new incoming or outgoing sms.
