package com.example.shafay.linmirror;


import android.app.Notification;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
        if(new PreferenceHandler(this).isSettingsSaved()){
            Notification notif = sbn.getNotification();
            String title = notif.extras.getString("android.title");
            String text = Objects.requireNonNull(notif.extras.getCharSequence("android.text")).toString();
            String packageName = sbn.getPackageName().substring(sbn.getPackageName().lastIndexOf(".")+1);
            String tickerText = (notif.tickerText != null)?notif.tickerText.toString():"";
            Icon bmp = notif.getLargeIcon();
            Notification.Action[] act = notif.actions;
            try {
                Socket socket = new Socket(new PreferenceHandler(this).getIP(), Integer.parseInt(new PreferenceHandler(this).getPort()));
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                pw .println(String.format("%s-%s-%s-%s",packageName.replaceAll("-"," "),
                        Objects.requireNonNull(title).replaceAll("-"," "), tickerText.replaceAll("-"," ")
                        ,text.replaceAll("-"," ")));
                br.close();
                pw.close();
            } catch (IOException e) {//TODO: exception testing in app
                //Utils.showToast(this.getBaseContext(), e.getMessage());
                e.printStackTrace();
            }
        }
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