package com.example.shafay.linmirror;


import android.app.Notification;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.util.Objects;

public class NotificationListener extends NotificationListenerService {

    /*
        These are the package names of the apps. for which we want to
        listen the notifications
     */
    private static final class ApplicationPackageNames {
        static final String FACEBOOK_PACK_NAME = "com.facebook.katana";
        static final String FACEBOOK_MESSENGER_PACK_NAME = "com.facebook.orca";
        static final String WHATSAPP_PACK_NAME = "com.whatsapp";
        static final String INSTAGRAM_PACK_NAME = "com.instagram.android";
    }

    /*
        These are the return codes we use in the method which intercepts
        the notifications, to decide whether we should do something or not
     */
    static final class InterceptedNotificationCode {
        static final int FACEBOOK_CODE = 1;
        static final int WHATSAPP_CODE = 2;
        static final int INSTAGRAM_CODE = 3;
        static final int OTHER_NOTIFICATIONS_CODE = 4; // We ignore all notification with code == 4
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        int notificationCode = matchNotificationCode(sbn);
/*
        if(notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE){
            Intent intent = new  Intent("com.github.chagall.notificationlistenerexample");
            intent.putExtra("Notification Code", notificationCode);
            sendBroadcast(intent);

        }*/
    //TODO:call socket here
        String packageName = sbn.getPackageName();//com.(package)
        Notification notif = sbn.getNotification();
        String category = notif.category;
        Icon bmp = notif.getLargeIcon();
        Notification.Action[] act = notif.actions;
        String message = Objects.requireNonNull(notif.extras.getCharSequence("android.text")).toString();

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        int notificationCode = matchNotificationCode(sbn);

        if(notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {

            StatusBarNotification[] activeNotifications = this.getActiveNotifications();

            if(activeNotifications != null && activeNotifications.length > 0) {
                for (StatusBarNotification activeNotification : activeNotifications) {
                    if (notificationCode == matchNotificationCode(activeNotification)) {
                        Intent intent = new Intent("com.github.chagall.notificationlistenerexample");
                        intent.putExtra("Notification Code", notificationCode);
                        sendBroadcast(intent);
                        break;
                    }
                }
            }
        }
    }

    private int matchNotificationCode(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();

        switch (packageName) {
            case ApplicationPackageNames.FACEBOOK_PACK_NAME:
            case ApplicationPackageNames.FACEBOOK_MESSENGER_PACK_NAME:
                return (InterceptedNotificationCode.FACEBOOK_CODE);
            case ApplicationPackageNames.INSTAGRAM_PACK_NAME:
                return (InterceptedNotificationCode.INSTAGRAM_CODE);
            case ApplicationPackageNames.WHATSAPP_PACK_NAME:
                return (InterceptedNotificationCode.WHATSAPP_CODE);
            default:
                return (InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE);
        }
    }
}