package com.nullpointerexception.retrogames.Pacman;

import android.app.Activity;
import android.os.Bundle;

import com.nullpointerexception.retrogames.R;

public class HelpActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_layout_pacman);
        MainActivityPacman.getPlayer().start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivityPacman.getPlayer().pause();
    }

}
