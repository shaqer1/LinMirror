package com.shafay.linmirror;

import android.content.Context;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    private static final String PORT_PATTERN = "[0-9]{1,5}$";
    public static void showToast(Context c, String m){
        Toast.makeText(c, m,Toast.LENGTH_SHORT).show();
    }

    public static boolean isNotNullOrEmpty(String s) {
        return s!=null && !s.equals("");
    }

    public static boolean isPortFormat(String s) {
        return Pattern.compile(PORT_PATTERN).matcher(s).matches();
    }

    public static boolean isIPFormat(String s) {
        return Pattern.compile(IPADDRESS_PATTERN).matcher(s).matches();
    }
}
