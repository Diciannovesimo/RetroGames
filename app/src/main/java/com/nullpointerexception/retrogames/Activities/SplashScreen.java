package com.nullpointerexception.retrogames.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.Components.AuthenticationManager;

public class SplashScreen extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Inizializza Room Database
        App.initializeRoomDatabase( getApplicationContext());

        SharedPreferences prefs = getSharedPreferences(App.APP_VARIABLES, MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean(App.APP_FIRST_OPENING, true);

        if(firstStart)
        {
            AuthenticationManager.get().initialize(this);
            prefs.edit().putBoolean(App.APP_FIRST_OPENING, false).apply();

            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        }
        else
        {
            AuthenticationManager.get().initialize(this);

            startActivity(new Intent(SplashScreen.this, HomeActivity.class));
            finish();
        }
    }
}
