package com.nullpointerexception.retrogames.Pong;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.nullpointerexception.retrogames.R;

/**
 * Main activity of MainActivityPong game.
 */
public class MainActivityPong extends Activity {

    /*
    private static final int MENU_NEW_GAME = 1;
    private static final int MENU_RESUME = 2;
    private static final int MENU_EXIT = 3;
    */

    private PongThread mGameThread;
    private Bundle save;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * mette in pausa il gioco
     */
    @Override
    protected void onPause() {
        super.onPause();
        mGameThread.pause();
    }

    /**
     * salva lo stato del gioco nella variabile
     * save
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mGameThread.saveState(outState);
        save = outState;
    }

    /*
    Questi metodi non vengono mai
    chiamati quindi li ho commentati
     */

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_NEW_GAME, 0, R.string.menu_new_game);
        menu.add(0, MENU_RESUME, 0, R.string.menu_resume);
        menu.add(0, MENU_EXIT, 0, R.string.menu_exit);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_NEW_GAME:
                mGameThread.startNewGame();
                return true;
            case MENU_EXIT:
                finish();
                return true;
            case MENU_RESUME:
                mGameThread.unPause();
                return true;
        }
        return false;
    }
    */

    /**
     * quando il gioco viene ripreso
     * parte il metodo game con il
     * salvataggio(save) di
     * onSaveInstanceState
     */
    @Override
    protected void onResume() {
        super.onResume();
        game(save);
    }

    /**
     * setta i layout di pong per poi
     * creare una nuova partita o riprende
     * quella avviata in precedenza se esiste
     * @param savedInstanceState
     */
    private void game(Bundle savedInstanceState){
        setContentView(R.layout.pong_layout);

        final PongView mPongView = findViewById(R.id.main);
        mPongView.setStatusView((TextView) findViewById(R.id.status));
        mPongView.setScoreView((TextView) findViewById(R.id.score));

        mGameThread = mPongView.getGameThread();

        if (savedInstanceState == null) {
            mGameThread.setState(PongThread.STATE_READY);
        } else {
            mGameThread.restoreState(savedInstanceState);
        }
    }
}
