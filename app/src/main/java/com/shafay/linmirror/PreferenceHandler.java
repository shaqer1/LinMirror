package com.shafay.linmirror;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHandler {
    private SharedPreferences prefs;
    PreferenceHandler(Context context){
        prefs = context.getSharedPreferences(Constants.app, Context.MODE_PRIVATE);
    }
    public void setEmail(String email){
        prefs.edit().putString(Constants.EMAIL_KEY, email).apply();
    }

    public String getEmail() {
        return prefs.getString(Constants.EMAIL_KEY, "");
    }


}
