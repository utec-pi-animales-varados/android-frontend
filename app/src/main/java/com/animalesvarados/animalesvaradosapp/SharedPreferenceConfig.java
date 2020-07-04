package com.animalesvarados.animalesvaradosapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceConfig {
    private SharedPreferences sharedPreferences;
    private Context context;

    public SharedPreferenceConfig(Context context){
        this.context=context;
        sharedPreferences=context.getSharedPreferences(context.getResources().getString(R.string.login_shared_preference), Context.MODE_PRIVATE);
    }

    public void login_status(boolean status){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getResources().getString(R.string.login_status_shared_preference),status);
        editor.apply();
    }

    public boolean read_login_status(){
        boolean status=false;
        status=sharedPreferences.getBoolean(context.getResources().getString(R.string.login_status_shared_preference),false);
        //status=sharedPreferences.getBoolean("login_status_shared_preference",false);
        return status;
    }

    public void saveUserName(String userName){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_name",userName);
        editor.apply();
    }

    public void saveUserEmail(String userEmail){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_email",userEmail);
        editor.apply();
    }

    public String getUserName(){
        String name = "";
        name = sharedPreferences.getString("user_name","!");
        return name;
    }

    public String getUserEmail(){
        String email = "";
        email =sharedPreferences.getString("user_email","");
        return email;
    }

}
