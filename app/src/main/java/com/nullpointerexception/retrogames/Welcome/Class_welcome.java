package com.nullpointerexception.retrogames.Welcome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nullpointerexception.retrogames.R;

public class Class_welcome extends AppCompatActivity {

    Button next;
    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction tx = fm.beginTransaction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Primo fragment
        Fragment_welcome welcome = new Fragment_welcome();

        tx.add(R.id.fragment , welcome);
        tx.commit();

        next = findViewById(R.id.button_welcome);


        next.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Secondo fragment
                Fragment_first_access first_access = new Fragment_first_access();

                tx.add(R.id.fragment,first_access);
                tx.commit();
            }
        });
    }
}
