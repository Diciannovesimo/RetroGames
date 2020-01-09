package com.nullpointerexception.retrogames.Pong;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.Components.SaveScore;
import com.nullpointerexception.retrogames.R;
import com.nullpointerexception.retrogames.Tetris.MainActivityTetris;

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
    private int highscore_point = 0;
    private AlertDialog mDlgMsg = null;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        update_Highscore();
    }

    /**
     * aggiorna l'highscore
     */
    private void update_Highscore()
    {
        if (App.scoreboardDao.getScore(App.PONG) != highscore_point) {
            highscore_point = App.scoreboardDao.getScore(App.PONG);
        }
    }

    public void game_over(int cpuScore, int humanScore){

        int score_pong = humanScore - cpuScore;

        if (score_pong < 0)
        {
            score_pong = 0;
            //fine 1
            showDialog_GameOver(score_pong);
        }

        if (highscore_point < score_pong)
        {
            highscore_point = score_pong;

            SaveScore pong = new SaveScore();
            pong.save(App.PONG, highscore_point,context);

            //fine 2
            showDialog_GameOver(score_pong);
        }
        else {
            //fine 3
            showDialog_GameOver(score_pong);
        }
    }

    /**
     * Mostra il dialog di GameOver
     */
    private void showDialog_GameOver(int score) {
        mDlgMsg = new AlertDialog.Builder(context)
                .setTitle(getResources().getString(R.string.gameOver))
                .setMessage(getResources().getString(R.string.your_score_is) + ": " + score)
                .setPositiveButton(getResources().getString(R.string.again), (dialog, which) -> {
                    mDlgMsg.dismiss();
                })
                .setNegativeButton(getResources().getString(R.string.exit), (dialog, which) -> {
                    mDlgMsg.dismiss();
                    if(context instanceof MainActivityTetris)
                        ((MainActivityTetris) context).finish();
                })
                .show();
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
