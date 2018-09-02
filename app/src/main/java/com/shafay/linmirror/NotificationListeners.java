
package com.shafay.linmirror;

import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class NotificationListeners extends NotificationListenerService {
    public static FirebaseAuth mAuth;

    @Override
    public IBinder onBind(Intent intent) {
        mAuth = FirebaseAuth.getInstance();
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            Notification notif = sbn.getNotification();
            String title = notif.extras.getString("android.title");
            String text = sbn.getNotification() != null && sbn.getNotification().extras != null && sbn.getNotification().extras.getCharSequence("android.text") != null ?
                    sbn.getNotification().extras.getCharSequence("android.text").toString() : "";

            String packageName;
            PackageManager packageManager= getApplicationContext().getPackageManager();
            try {
                packageName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(sbn.getPackageName(), PackageManager.GET_META_DATA));
            } catch (PackageManager.NameNotFoundException e) {
                packageName = "";
            }
            Icon bmp = notif.getLargeIcon();
            Notification.Action[] act = notif.actions;
            if (title == null || notif.visibility <= Notification.VISIBILITY_SECRET || title.contains("Select keyboard") || title.contains("charging")) {
                return;
            }
            //firebase
            Map<String, Object> notification = new HashMap<>();
            notification.put("packageName", packageName);
            notification.put("title", (!title.equals(packageName))?title:"");
            notification.put("text", text);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docref = db.collection(currentUser.getUid()).document("userNotifications").collection("notifications")
                    .document(System.currentTimeMillis() + packageName);
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
        super.onNotificationRemoved(sbn);
    }
}