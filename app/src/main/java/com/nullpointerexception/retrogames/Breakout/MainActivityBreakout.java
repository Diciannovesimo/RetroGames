package com.nullpointerexception.retrogames.Breakout;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.R;

public class MainActivityBreakout extends AppCompatActivity
{
    SurfaceViewThread surfaceViewThread;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_breakout);

        // Rende l'app a schermo intero per nascondere la barra di stato superiore
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Creare l'oggetto SurfaceViewThread
        surfaceViewThread = new SurfaceViewThread(this);
        LinearLayout drawCanvas = findViewById(R.id.drawCanvas);
        drawCanvas.addView(surfaceViewThread);

        SharedPreferences prefs = getSharedPreferences(App.APP_VARIABLES, MODE_PRIVATE);
        if(prefs.getBoolean(App.BREAKOUT_TUTORIAL, true))
        {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.breakout_welcome)
                    .setMessage(R.string.breakout_tutorial)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dialog_ok, (a, b) ->
                            prefs.edit().putBoolean(App.BREAKOUT_TUTORIAL, false).apply())
                    .show();
        }

        startMusic();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // Indicare al metodo di ripresa gameView da eseguire
        surfaceViewThread.start();

        if(player != null && ! player.isPlaying())
            startMusic();
    }

    // Questo metodo viene eseguito quando il giocatore esce dal gioco
    @Override
    protected void onPause()
    {
        super.onPause();
        surfaceViewThread.pause();

        if(player != null)
            player.stop();
    }

    /**
     *  Fa partire la classica musica di Breakout
     */
    private void startMusic()
    {
        player = MediaPlayer.create(this, R.raw.breakout_theme);
        player.setVolume(100, 100);
        player.setLooping(true);
        player.start();
    }

}