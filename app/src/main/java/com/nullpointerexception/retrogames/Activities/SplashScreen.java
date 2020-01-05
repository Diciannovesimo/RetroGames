package com.nullpointerexception.retrogames.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.Components.App;
import com.nullpointerexception.retrogames.MainActivity;

public class SplashScreen extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /*
                TODO Initialization Firebase and other components
                TODO Login user

         */

        App.initializeRoomDatabase(getApplicationContext());

        SharedPreferences prefs = getSharedPreferences(App.APP_VARIABLES, MODE_PRIVATE);
        prefs.edit().putBoolean(App.APP_FIRST_OPENING, true).apply();   // TODO Remove after testing

        if(prefs.getBoolean(App.APP_FIRST_OPENING, true))
        {
            startActivity(new Intent(this, WelcomeActivity.class));
            prefs.edit().putBoolean(App.APP_FIRST_OPENING, false).apply();
        }
        else
            startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
