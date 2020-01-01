package com.nullpointerexception.retrogames.Breakout;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.R;

public class GameActivity extends AppCompatActivity {
    SurfaceViewThread surfaceViewThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_breakout);

        // Hide app title bar.
        getSupportActionBar().hide();

        // Make app full screen to hide top status bar.
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Create the SurfaceViewThread object.
        surfaceViewThread = new SurfaceViewThread(getApplicationContext());

        // Get text drawing LinearLayout canvas.
        LinearLayout drawCanvas = (LinearLayout)findViewById(R.id.drawCanvas);
        // Add surfaceview object to the LinearLayout object.
        drawCanvas.addView(surfaceViewThread);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tell the gameView resume method to execute
        surfaceViewThread.start();


    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();
        // Tell the gameView pause method to execute
        surfaceViewThread.pause();
    }
}