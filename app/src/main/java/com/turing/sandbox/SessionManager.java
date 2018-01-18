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
    /*
    DATOS DEL USUARIO DE LA API
    "id" : 3,
  "login" : "admin",
  "firstName" : "Administrator",
  "lastName" : "Administrator",
  "email" : "admin@localhost",
  "imageUrl" : "",
  "activated" : true,
  "langKey" : "en",
  "createdBy" : "system",
  "createdDate" : "2018-01-17T19:26:21.240Z",
  "lastModifiedBy" : "system",
  "lastModifiedDate" : null,
  "authorities" : [ "ROLE_USER", "ROLE_ADMIN" ]

     */
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE =0;

    /*
    *Listado de datos de el usuario a guardar
     */
    private static final String PREFER_NAME = "SandBox";
    private static final String IS_USER_LOGIN = "IsUserLogin";
    public static final String KEY_ID = "id";
    public static final String KEY_LOGIN = "Login";
    public static final String KEY_FIRSTNAME = "FirstName";
    public static final String KEY_LASTNAME = "LastName";
    public static final String KEY_EMAIL = "Email";
    public static final String KEY_IMAGEURL = "ImageUrl";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME,PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createUserLoginSession(String name, String email){
        editor.putBoolean(IS_USER_LOGIN,true);
    }

    public void createUserLoginSession(String id, String login, String firstName, String lastName, String email, String imageURL) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_LOGIN, login);
        editor.putString(KEY_FIRSTNAME, firstName);
        editor.putString(KEY_LASTNAME, lastName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_IMAGEURL, imageURL);
        editor.commit();
    }

    public boolean checkLogin(){
        if(!this.isUserLoggedIn()){
            Intent i = new Intent(_context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            return true;
        }
        return false;
    }


    /**
     * Get stored session data
     * */
    public HashMap<String,String> getUserDetails(){
        HashMap<String,String> user = new HashMap();

        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(KEY_LOGIN, pref.getString(KEY_LOGIN, null));
        user.put(KEY_FIRSTNAME, pref.getString(KEY_FIRSTNAME, null));
        user.put(KEY_LASTNAME, pref.getString(KEY_LASTNAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_IMAGEURL, pref.getString(KEY_IMAGEURL, null));

        return user;
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }


    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
}

