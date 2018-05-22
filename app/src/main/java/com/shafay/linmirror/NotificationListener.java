
package com.shafay.linmirror;

import android.app.Notification;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class NotificationListener extends NotificationListenerService {

    private static final class ApplicationPackageNames {
        static final String FACEBOOK_PACK_NAME = "com.facebook.katana";
        static final String FACEBOOK_MESSENGER_PACK_NAME = "com.facebook.orca";
        static final String WHATSAPP_PACK_NAME = "com.whatsapp";
        static final String INSTAGRAM_PACK_NAME = "com.instagram.android";
    }


    public static final class InterceptedNotificationCode {
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

        /*if(notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE){
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
            if(Objects.requireNonNull(title).contains("Select keyboard")){
                return;
            }
            //firebase
            Map<String, Object> notification = new HashMap<>();
            notification.put("packageName", "\"" + packageName.replaceAll("-"," ") + "\"");
            notification.put("title","\"" +  Objects.requireNonNull(title).replaceAll("-"," ")+ "\"");
            notification.put("tickerText", "\"" + tickerText.replaceAll("-"," ")+ "\"");
            notification.put("text", "\"" + text.replaceAll("-"," ")+ "\"");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docref = db.collection("notifications")
                    .document(System.currentTimeMillis() + packageName.replaceAll("-"," "));
            docref.set(notification)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

            Map<String,Object> updates = new HashMap<>();
            updates.put("timestamp", FieldValue.serverTimestamp());

            docref.update(updates).addOnCompleteListener(task -> Log.d(TAG, "DocumentSnapshot successfully written!"));
            //firebase ---- end ----
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

        if(packageName.equals(ApplicationPackageNames.FACEBOOK_PACK_NAME)
                || packageName.equals(ApplicationPackageNames.FACEBOOK_MESSENGER_PACK_NAME)){
            return(InterceptedNotificationCode.FACEBOOK_CODE);
        }
        else if(packageName.equals(ApplicationPackageNames.INSTAGRAM_PACK_NAME)){
            return(InterceptedNotificationCode.INSTAGRAM_CODE);
        }
        else if(packageName.equals(ApplicationPackageNames.WHATSAPP_PACK_NAME)){
            return(InterceptedNotificationCode.WHATSAPP_CODE);
        }
        else{
            return(InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE);
        }
    }
}