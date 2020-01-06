package com.nullpointerexception.retrogames;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.Activities.LoginActivity;
import com.nullpointerexception.retrogames.Activities.WelcomeActivity;
import com.nullpointerexception.retrogames.Breakout.MainActivityBreakout;
import com.nullpointerexception.retrogames.Components.App;
import com.nullpointerexception.retrogames.Components.BackEndInterface;
import com.nullpointerexception.retrogames.Components.Scoreboard;
import com.nullpointerexception.retrogames.Hole.FullscreenActivity;
import com.nullpointerexception.retrogames.Pong.MainActivityPong;
import com.nullpointerexception.retrogames.Snake.MainActivitySnake;
import com.nullpointerexception.retrogames.Tetris.Tetris;

import java.util.List;

public class MainActivity extends AppCompatActivity
{

    private Button btnTetris, btnPong, btnSpaceInvaders, btnPacman, btnBreakout, btnLogin, btnWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialUI();

        provaDatabaseFirebase();

        provaDatabaseLocale();

        buttonListener();

    }

    private void initialUI() {
        btnTetris = findViewById(R.id.button2);
        btnPong = findViewById(R.id.button3);
        btnSpaceInvaders = findViewById(R.id.button4);
        btnPacman = findViewById(R.id.button5);
        btnBreakout = findViewById(R.id.button6);
        btnLogin = findViewById(R.id.registration_btn);
        btnWelcome = findViewById(R.id.button7);
    }

    private void buttonListener() {
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
                Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);
                startActivity(intent);
            }
        });

        btnPacman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivitySnake.class);
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

        btnWelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void provaDatabaseLocale() {
        //Inserimento Tetris
        Scoreboard tetris = new Scoreboard(App.TETRIS, 10);
        if(App.scoreboardDao.getGame(App.TETRIS) == null)
            App.scoreboardDao.insertAll(tetris);
        else
            App.scoreboardDao.update(tetris);
        //Toast.makeText(getApplicationContext(),App.scoreboardDao.getGame(App.TETRIS) + ": " + App.scoreboardDao.getScore(App.TETRIS), Toast.LENGTH_SHORT).show();


        //Inserimanto Pacman
        Scoreboard pacman = new Scoreboard(App.PACMAN,20);
        if(App.scoreboardDao.getGame(App.PACMAN) == null)
            App.scoreboardDao.insertAll(pacman);
        else
            App.scoreboardDao.update(pacman);
        //Toast.makeText(getApplicationContext(),App.scoreboardDao.getGame(App.PACMAN) + ": " + App.scoreboardDao.getScore(App.PACMAN), Toast.LENGTH_SHORT).show();


        //Inserimanto Pong
        Scoreboard pong = new Scoreboard(App.PONG,30);
        if(App.scoreboardDao.getGame(App.PONG) == null)
            App.scoreboardDao.insertAll(pong);
        else
            App.scoreboardDao.update(pong);
        //Toast.makeText(getApplicationContext(),App.scoreboardDao.getGame(App.PONG) + ": " + App.scoreboardDao.getScore(App.PONG), Toast.LENGTH_SHORT).show();

        //Inserimanto SpaceInvaders
        Scoreboard spaceInvaders = new Scoreboard(App.SPACEINVADERS,40);
        if(App.scoreboardDao.getGame(App.SPACEINVADERS) == null)
            App.scoreboardDao.insertAll(spaceInvaders);
        else
            App.scoreboardDao.update(spaceInvaders);
        //Toast.makeText(getApplicationContext(),App.scoreboardDao.getGame(App.SPACEINVADERS) + ": " + App.scoreboardDao.getScore(App.SPACEINVADERS), Toast.LENGTH_SHORT).show();


        //Inserimanto Breakout
        Scoreboard breakout = new Scoreboard(App.BREAKOUT,50);
        if(App.scoreboardDao.getGame(App.BREAKOUT) == null)
            App.scoreboardDao.insertAll(breakout);
        else
            App.scoreboardDao.update(breakout);
        //Toast.makeText(getApplicationContext(),App.scoreboardDao.getGame(App.BREAKOUT) + ": " + App.scoreboardDao.getScore(App.BREAKOUT), Toast.LENGTH_SHORT).show();


        //Leggo tutti i giochi inseriti nel database
        List<Scoreboard> scoreboards = App.scoreboardDao.getAll();
        for(int i=0; i<scoreboards.size(); i++)
            Toast.makeText(getApplicationContext(), scoreboards.get(i).getGame() + ": " + scoreboards.get(i).getScore(), Toast.LENGTH_SHORT).show();
    }

    private void provaDatabaseFirebase() {
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
               // Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
            }

        });

        //Scrittura sul database di un user
        BackEndInterface.get().writeUser("ilmatty98s@gmail.com","ilMatty98");
        BackEndInterface.get().writeUser("cicacica98@gmail.com","Cischi98");
        BackEndInterface.get().writeUser("slab98@gmail.com","sLab98");

        //Leggo il nickname di un determinato giocatore
        BackEndInterface.get().readUser("cicacica98@gmail.com", new BackEndInterface.OnDataReceivedListener() {
            @Override
            public void onDataReceived(boolean success, String value) {
               // Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
