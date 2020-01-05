package com.nullpointerexception.retrogames.Breakout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.R;

public class MainActivityBreakout extends AppCompatActivity {

    Button btPlay, btScore;
    LinearLayout background;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_breakout);

        // Hide app title bar.
        //getSupportActionBar().hide();

        // Make app full screen to hide top status bar.
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        title = (TextView) findViewById(R.id.activity_main_title);
        background =  findViewById(R.id.activity_main);
        btPlay =  findViewById(R.id.activity_main_bt_play);
        btScore =  findViewById(R.id.activity_main_bt_score);

        // Couleurs
        background.setBackgroundColor(Color.BLACK);
        title.setTextColor(Color.WHITE);
        btPlay.setBackgroundColor(Color.WHITE);
        btScore.setBackgroundColor(Color.WHITE);

        // Action sur les boutons
        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GameActivity = new Intent(MainActivityBreakout.this, GameActivity.class);
                startActivity(GameActivity);
            }
        });
        btScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean show;
                show = false;
                Intent ScoreActivity = new Intent(MainActivityBreakout.this, Scoreboard.class);
                ScoreActivity.putExtra("ShowButton", show);
                startActivity(ScoreActivity);

            }
        });

    }
}
