package com.nullpointerexception.retrogames.Breakout;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

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

        // Make app full screen to hide top status bar.
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Create the SurfaceViewThread object.
        surfaceViewThread = new SurfaceViewThread(this);

        // Get text drawing LinearLayout canvas.
        LinearLayout drawCanvas = findViewById(R.id.drawCanvas);
        // Add surfaceView object to the LinearLayout object.
        drawCanvas.addView(surfaceViewThread);

        startMusic();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // Tell the gameView resume method to execute
        surfaceViewThread.start();

        if(player != null && ! player.isPlaying())
            startMusic();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause()
    {
        super.onPause();
        // Tell the gameView pause method to execute
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