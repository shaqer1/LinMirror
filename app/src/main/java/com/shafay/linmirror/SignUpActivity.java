package com.shafay.linmirror;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;

import static com.shafay.linmirror.NotificationListeners.mAuth;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "LOG";

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passText);
        if(this.getIntent().getExtras() != null && this.getIntent().getExtras().getString("email") != null){
            emailEditText.setText(this.getIntent().getExtras().getString("email"));
        }
        signUpButton = findViewById(R.id.signupButton);
        signUpButton.setOnClickListener(view -> {
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
            Utils.showToast(SignUpActivity.this, "Email and password are valid!");
            mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
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
                    });
            Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(i);
        });
    }

}
