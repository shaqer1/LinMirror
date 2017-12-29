package com.example.shafay.linmirror;

import android.content.Context;
import android.widget.Toast;

public class Utils {
    public static void showToast(Context c, String m){
        Toast.makeText(c, m,Toast.LENGTH_SHORT).show();
    }
}
