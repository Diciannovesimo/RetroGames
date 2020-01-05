package com.nullpointerexception.retrogames.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

        // Primo fragment
        WelcomeFragment welcome = new WelcomeFragment();

        fragmentTransaction.add(R.id.fragment , welcome);
        fragmentTransaction.commit();
    }

}
