package com.nullpointerexception.retrogames.Welcome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nullpointerexception.retrogames.MainActivity;
import com.nullpointerexception.retrogames.R;

public class WelcomeActivity extends AppCompatActivity {

    private FragmentManager fm;
    private FragmentTransaction tx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        fm = getSupportFragmentManager();
        tx = fm.beginTransaction();

        // Primo fragment
        WelcomeFragment welcome = new WelcomeFragment();

        tx.add(R.id.fragment , welcome);
        tx.commit();

    }



}
