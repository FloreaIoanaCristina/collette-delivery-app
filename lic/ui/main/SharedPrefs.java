package com.dam.lic.ui.main;

import static android.content.Context.MODE_PRIVATE;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs  {
    public static final String SHARED_PREFS ="sharedPrefs";
    private static final String REMEMBER_ME_KEY = "rememberMe";
    private static final String NIGHT_MODE = "nightMode";
    private static final String NOTIFICATIONS ="notificari";


    SharedPreferences sharedPreferences;
    Context context;

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public SharedPrefs(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
    }

    public void setDarkTheme(boolean b) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NIGHT_MODE, b);
        editor.apply();

    }
    public boolean getDarkTheme()
    {

        return sharedPreferences.getBoolean(NIGHT_MODE,false);
    }

    public void setRememberMeKey(boolean b)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(REMEMBER_ME_KEY,b);
        editor.apply();
    }
    public boolean getRememberMeKey()
    {
        return sharedPreferences.getBoolean(REMEMBER_ME_KEY,false);
    }

    public void setNotifications(boolean b)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NOTIFICATIONS, b);
        editor.apply();
    }
    public boolean getNotifications()
    {
        return sharedPreferences.getBoolean(NOTIFICATIONS,true);
    }






}
