package com.shafay.linmirror;

import android.content.Context;
import android.widget.Toast;

import java.util.regex.Pattern;

public class Utils {
    private static final String EMAIL_PATTERN =
            "^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$";
    private static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$";
    public static void showToast(Context c, String m){
        Toast.makeText(c, m,Toast.LENGTH_SHORT).show();
    }

    public static boolean isNotNullOrEmpty(String s) {
        return s!=null && !s.equals("");
    }

    public static boolean isPasswordFormat(String s) {
        return Pattern.compile(PASSWORD_PATTERN).matcher(s).matches();
    }

    public static boolean isEmailFormat(String s) {
        return Pattern.compile(EMAIL_PATTERN).matcher(s).matches();
    }
}
