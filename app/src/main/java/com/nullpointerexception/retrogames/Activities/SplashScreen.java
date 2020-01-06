package com.nullpointerexception.retrogames.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.Components.App;
import com.nullpointerexception.retrogames.Components.AuthenticationManager;
import com.nullpointerexception.retrogames.Components.User;

public class SplashScreen extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        User currentUser = new User();
        /*
                TODO Initialization Firebase and other components
                TODO Login user

         */

        //Inizializza Room Database
        App.initializeRoomDatabase(getApplicationContext());

        SharedPreferences prefs = getSharedPreferences(App.APP_VARIABLES, MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean(App.APP_FIRST_OPENING, true);

        if(firstStart) {
            AuthenticationManager.get().initialize(this);
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        }else {
            AuthenticationManager.LoginAttempt loginAttempt = AuthenticationManager.get().initialize(this);

            if (loginAttempt != null) {

                startActivity(new Intent(SplashScreen.this, HomeActivity.class));
                finish();
            }else{
                startActivity(new Intent(SplashScreen.this, WelcomeActivity.class));
                finish();
            }
        }
    }
}
