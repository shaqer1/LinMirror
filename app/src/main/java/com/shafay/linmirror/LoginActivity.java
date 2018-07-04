package com.shafay.linmirror;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;

import static com.shafay.linmirror.NotificationListeners.mAuth;

public class LoginActivity extends AppCompatActivity {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private static final String TAG = "LOG";

    //private ImageChangeBroadcastReceiver imageChangeBroadcastReceiver;
    private AlertDialog enableNotificationListenerAlertDialog;
    private Button loginButton;
    private EditText passwordEditText;
    private EditText emailEditText;
    private Button signUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If the user did not turn the notification listener service on we prompt him to do so
        if(!isNotificationServiceEnabled()){
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        }
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signupButton);
        signUpButton.setOnClickListener(view -> {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                if(Utils.isNotNullOrEmpty(emailEditText.getText().toString()) && Utils.isEmailFormat(emailEditText.getText().toString()))
                    i.putExtra("email", emailEditText.getText().toString());
                startActivity(i);

                }
        );
        loginButton.setOnClickListener((view) -> {
            if(!Utils.isNotNullOrEmpty(passwordEditText.getText().toString()) || !Utils.isPasswordFormat(passwordEditText.getText().toString())){
                passwordEditText.setError("Password must be at least 8 characters, one uppercase, and one lowercase.");
                return;
            }
            if(Utils.isNotNullOrEmpty(emailEditText.getText().toString()) && Utils.isEmailFormat(emailEditText.getText().toString()))
                new PreferenceHandler(this).setEmail(emailEditText.getText().toString());
            else {
                emailEditText.setError("Email has incorrect format or is empty");
                return;
            }
            Utils.showToast(LoginActivity.this, "Email and password are valid!");
            mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        /*TODO
                        updateUI(user);*/
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Utils.showToast(LoginActivity.this, "Authentication failed.");
                            /*TODO:updateUI(null);*/
                        }

                        // ...
                    });


        });
        /*mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Utils.showToast(this, "Authentication failed.");
                    }

                    // ...
                });*/
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passText);
        if(!new PreferenceHandler(this).getEmail().equals("")){
            emailEditText.setText(new PreferenceHandler(this).getEmail());
        }

/*        // Finally we register a receiver to tell the LoginActivity when a notification has been received
        imageChangeBroadcastReceiver = new ImageChangeBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.github.chagall.notificationlistenerexample");
        registerReceiver(imageChangeBroadcastReceiver,intentFilter);*/
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();/*
        unregisterReceiver(imageChangeBroadcastReceiver);*/
    }

   /**
     * Change Intercepted Notification Image
     * Changes the LoginActivity image based on which notification was intercepted
     * @param notificationCode The intercepted notification code
     *//*
    private void changeInterceptedNotificationImage(int notificationCode){
        switch(notificationCode){
            case NotificationListeners.InterceptedNotificationCode.FACEBOOK_CODE:
                interceptedNotificationImageView.setImageResource(R.drawable.facebook_logo);
                break;
            case NotificationListeners.InterceptedNotificationCode.INSTAGRAM_CODE:
                interceptedNotificationImageView.setImageResource(R.drawable.instagram_logo);
                break;
            case NotificationListeners.InterceptedNotificationCode.WHATSAPP_CODE:
                interceptedNotificationImageView.setImageResource(R.drawable.whatsapp_logo);
                break;
            case NotificationListeners.InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE:
                interceptedNotificationImageView.setImageResource(R.drawable.other_notification_logo);
                break;
        }
    }
*/
    /**
     * Is Notification Service Enabled.
     * Verifies if the notification listener service is enabled.
     * @return True if eanbled, false otherwise.
     */
    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Image Change Broadcast Receiver.
     * We use this Broadcast Receiver to notify the Main Activity when
     * a new notification has arrived, so it can properly change the
     * notification image
     * *//*
    public class ImageChangeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int receivedNotificationCode = intent.getIntExtra("Notification Code",-1);
            changeInterceptedNotificationImage(receivedNotificationCode);
        }
    }*/


    /**
     * Build Notification Listener Alert Dialog.
     * Builds the alert dialog that pops up if the user has not turned
     * the Notification Listener Service on yet.
     * @return An alert dialog which leads to the notification enabling screen
     */
    private AlertDialog buildNotificationServiceAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.notification_listener_service);
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation);
        alertDialogBuilder.setPositiveButton(R.string.yes,
                (dialog, id) -> startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS)));
        alertDialogBuilder.setNegativeButton(R.string.no,
                (dialog, id) -> {
                    // If you choose to not enable the notification listener
                    // the app. will not work as expected
                });
        return(alertDialogBuilder.create());
    }
}
