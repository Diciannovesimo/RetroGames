package com.nullpointerexception.retrogames.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.nullpointerexception.retrogames.Components.App;
import com.nullpointerexception.retrogames.Fragments.LoginFragment;
import com.nullpointerexception.retrogames.Fragments.WelcomeFragment;
import com.nullpointerexception.retrogames.R;

public class WelcomeActivity extends AppCompatActivity
{

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        SharedPreferences prefs = getSharedPreferences(App.APP_VARIABLES, MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean(App.APP_FIRST_OPENING, true);

        if(firstStart){
            // Primo fragment
            WelcomeFragment welcome = new WelcomeFragment();
            prefs.edit().putBoolean(App.APP_FIRST_OPENING, false).apply();
            fragmentTransaction.add(R.id.fragment , welcome);
        }else{
            // Secondo fragment
            LoginFragment loginFragment = new LoginFragment();
            fragmentTransaction.add(R.id.fragment , loginFragment);
        }

        fragmentTransaction.commit();
    }

}
