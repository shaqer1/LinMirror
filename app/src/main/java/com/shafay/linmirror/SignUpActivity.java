package com.shafay.linmirror;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;

import static com.shafay.linmirror.NotificationListeners.mAuth;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "LOG";

    private TextInputLayout emailLayoutText;
    private TextInputLayout passwordLayoutText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailLayoutText = findViewById(R.id.emailLayoutText);
        passwordLayoutText = findViewById(R.id.passwordLayoutText);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        signUpButton = findViewById(R.id.signupButton);


        // retrieve text that was left in the email field
        if(this.getIntent().getExtras() != null && this.getIntent().getExtras().getString("email") != null){
            emailEditText.setText(this.getIntent().getExtras().getString("email"));
        }

        signUpButton.setOnClickListener(view -> {

            // Check email format
            String emailInput = emailEditText.getText().toString();
            if(Utils.isNullOrEmpty(emailInput)){
                emailLayoutText.setError("provide an email address");
                return;
            }
            if(!Utils.isEmailFormat(emailInput)){
                emailLayoutText.setError("enter a valid email address");
                return;
            }
            new PreferenceHandler(this).setEmail(emailInput);



            // Check password format
            String passwordInput = passwordEditText.getText().toString();
            if(Utils.isNullOrEmpty(passwordInput)){
                passwordLayoutText.setError("provide a password");
                return;
            }
            if (!Utils.isPasswordFormat(passwordInput)){
                passwordLayoutText.setError("password must be at least 8 characters including one uppercase and one lowercase");
                return;
            }


            Utils.showToast(SignUpActivity.this, "Email and password are valid!");
            mAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
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


        //googleSignInButton = findViewById(R.id.googleSignInButton);
    }

}
