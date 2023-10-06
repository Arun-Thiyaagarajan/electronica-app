package com.example.electronica;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

public class SharedPref {
    private static SharedPreferences getSharedPref(@NonNull Context context){
        return context.getSharedPreferences("ElecSharedPref",Context.MODE_PRIVATE);
    }

    public static String getName(Context context){
        return getSharedPref(context).getString("name","");
    }

    public static void setName(Context context,String name){
        SharedPreferences.Editor editor = getSharedPref(context).edit();
        editor.putString("name",name);
        editor.apply();
    }

    public static String getEmail(Context context){
        return getSharedPref(context).getString("email","");
    }

    public static void setEmail(Context context,String email){
        SharedPreferences.Editor editor = getSharedPref(context).edit();
        editor.putString("email",email);
        editor.apply();
    }
}
