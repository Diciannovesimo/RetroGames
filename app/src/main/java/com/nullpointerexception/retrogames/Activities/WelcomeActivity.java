package com.nullpointerexception.retrogames.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.Fragments.WelcomeFragment;
import com.nullpointerexception.retrogames.R;

public class WelcomeActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, new WelcomeFragment())
                .commit();
    }

}
