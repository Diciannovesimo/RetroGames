package com.nullpointerexception.retrogames;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.Breakout.MainActivityBreakout;
import com.nullpointerexception.retrogames.Pacman.MainActivityPacman;
import com.nullpointerexception.retrogames.Pong.MainActivityPong;
import com.nullpointerexception.retrogames.SpaceInvaders.MainActivitySpaceInvaders;
import com.nullpointerexception.retrogames.Tetris.Tetris;

public class MainActivity extends AppCompatActivity {

    private Button btnTetris, btnPong, btnSpaceInvaders, btnPacman, btnBreakout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTetris = findViewById(R.id.button2);
        btnPong = findViewById(R.id.button3);
        btnSpaceInvaders = findViewById(R.id.button4);
        btnPacman = findViewById(R.id.button5);
        btnBreakout = findViewById(R.id.button6);


        btnTetris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Tetris.class);
                startActivity(intent);
            }
        });


        btnPong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivityPong.class);
                startActivity(intent);
            }
        });


        btnSpaceInvaders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivitySpaceInvaders.class);
                startActivity(intent);
            }
        });


        btnPacman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivityPacman.class);
                startActivity(intent);
            }
        });


        btnBreakout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivityBreakout.class);
                startActivity(intent);
            }
        });
    }
}
