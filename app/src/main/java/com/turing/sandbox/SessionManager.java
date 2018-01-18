package com.turing.sandbox;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by smp_3 on 17/01/2018.
 */

public class SessionManager
{
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE =0;

    private static final String PREFER_NAME = "Denarius";
    private static final String IS_USER_LOGIN = "IsUserLogin";
    public static final String KEY_ID = "idUsuario";
    public static final String KEY_USER = "Usuario";
    public static final String KEY_NAME = "Nombre";
    public static final String KEY_EMAIL = "Correo";
    public static final String KEY_SEX = "Sexo";
    public static final String KEY_DATE = "Fecha_Nac";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME,PRIVATE_MODE);
        editor = pref.edit();
    }

    /*public void createUserLoginSession(String name, String email){
        editor.putBoolean(IS_USER_LOGIN,true);
    }*/
    public void createUserLoginSession(String idUsuario, String user, String name, String email, String sex, String date) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_ID, idUsuario);
        editor.putString(KEY_USER, user);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_SEX, sex);
        editor.putString(KEY_DATE, date);
        editor.commit();
    }

    public boolean checkLogin(){
        /*if(!this.isUserLoggedIn()){
            Intent i = new Intent(_context, ClaseDestino.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            return true;
        }*/
        return false;
    }


    /**
     * Get stored session data
     * */
    public HashMap<String,String> getUserDetails(){
        HashMap<String,String> user = new HashMap();

        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(KEY_USER, pref.getString(KEY_USER, null));
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_SEX, pref.getString(KEY_SEX, null));
        user.put(KEY_DATE, pref.getString(KEY_DATE, null));

        return user;
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
        /*Intent i = new Intent(_context, claseDestino.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);*/
    }


    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
}

