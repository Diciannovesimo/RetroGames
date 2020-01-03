package com.nullpointerexception.retrogames;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.Activities.LoginActivity;
import com.nullpointerexception.retrogames.Breakout.MainActivityBreakout;
import com.nullpointerexception.retrogames.Pacman.MainActivityPacman;
import com.nullpointerexception.retrogames.Pong.MainActivityPong;
import com.nullpointerexception.retrogames.SpaceInvaders.MainActivitySpaceInvaders;
import com.nullpointerexception.retrogames.Tetris.Tetris;

public class MainActivity extends AppCompatActivity {

    private Button btnTetris, btnPong, btnSpaceInvaders, btnPacman, btnBreakout, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTetris = findViewById(R.id.button2);
        btnPong = findViewById(R.id.button3);
        btnSpaceInvaders = findViewById(R.id.button4);
        btnPacman = findViewById(R.id.button5);
        btnBreakout = findViewById(R.id.button6);
        btnLogin = findViewById(R.id.login_btn);

        provaDatabase();

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

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }


    private void provaDatabase() {


        //Scrittura sul database dello score
        BackEndInterface.get().writeScoreFirebase(App.PACMAN,"ilmatty98", 10, 50);
        BackEndInterface.get().writeScoreFirebase(App.PACMAN,"diciannovesimo", 20, 70);
        BackEndInterface.get().writeScoreFirebase(App.PACMAN,"sgrulu", 30, 50);
        BackEndInterface.get().writeScoreFirebase(App.PACMAN,"cioscos", 40, 50);

        BackEndInterface.get().writeScoreFirebase(App.TETRIS,"ilmatty98", 50, 50);
        BackEndInterface.get().writeScoreFirebase(App.TETRIS,"diciannovesimo", 60, 50);
        BackEndInterface.get().writeScoreFirebase(App.TETRIS,"sgrulu", 70, 50);
        BackEndInterface.get().writeScoreFirebase(App.TETRIS,"cioscos", 80, 50);

        BackEndInterface.get().writeScoreFirebase(App.PONG,"ilmatty98", 90, 50);
        BackEndInterface.get().writeScoreFirebase(App.PONG,"diciannovesimo", 100, 50);
        BackEndInterface.get().writeScoreFirebase(App.PONG,"sgrulu", 110, 50);
        BackEndInterface.get().writeScoreFirebase(App.PONG,"cioscos", 120, 50);

        BackEndInterface.get().writeScoreFirebase(App.SPACEINVADERS,"ilmatty98", 130, 50);
        BackEndInterface.get().writeScoreFirebase(App.SPACEINVADERS,"diciannovesimo", 140, 50);
        BackEndInterface.get().writeScoreFirebase(App.SPACEINVADERS,"sgrulu", 150, 50);
        BackEndInterface.get().writeScoreFirebase(App.SPACEINVADERS,"cioscos", 160, 50);

        BackEndInterface.get().writeScoreFirebase(App.BREAKOUT,"ilmatty98", 170, 500);
        BackEndInterface.get().writeScoreFirebase(App.BREAKOUT,"diciannovesimo", 180, 506);
        BackEndInterface.get().writeScoreFirebase(App.BREAKOUT,"sgrulu", 190, 508);
        BackEndInterface.get().writeScoreFirebase(App.BREAKOUT,"cioscos", 200, 510);

        //Leggo lo score di un singolo giocatore in un determinato gioco o il suo totalscore
        BackEndInterface.get().readScoreFirebase(App.TOTALSCORE, "ilmatty98", new BackEndInterface.OnDataReceivedListener() {
            @Override
            public void onDataReceived(boolean success, String value) {
                Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
            }

        });





        //Scrittura sul database di un user
     //   BackEndInterface.get().writeUser("ilmatty98s@gmail.com","ilMatty98");
     //   BackEndInterface.get().writeUser("cicacica98@gmail.com","Cischi98");
        BackEndInterface.get().writeUser("slab98@gmail.com","sLab98");

        //Leggo il nickname di un determinato giocatore
        BackEndInterface.get().readUser("cicacica98@gmail.com", new BackEndInterface.OnDataReceivedListener() {
            @Override
            public void onDataReceived(boolean success, String value) {
                Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
