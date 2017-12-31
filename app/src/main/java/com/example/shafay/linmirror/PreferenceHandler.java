package com.example.shafay.linmirror;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHandler {
    private SharedPreferences prefs;
    PreferenceHandler(Context context){
        prefs = context.getSharedPreferences(Constants.app, Context.MODE_PRIVATE);
    }
    public void setIP(String ip){
        prefs.edit().putString(Constants.IP_KEY, ip).apply();
    }
    public void setPort(String port){
        prefs.edit().putString(Constants.PORT_KEY, port).apply();
    }
    public void putSettingSaved(boolean b){
        prefs.edit().putBoolean(Constants.SETTINGS_SAVED, b).apply();
    }

    public boolean isSettingsSaved() {
        return prefs.getBoolean(Constants.SETTINGS_SAVED, false);
    }

    public String getIP() {
        return prefs.getString(Constants.IP_KEY, "");
    }

    public String getPort() {
        return prefs.getString(Constants.PORT_KEY,"");
    }
}
