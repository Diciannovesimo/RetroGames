package com.nullpointerexception.retrogames.Pong;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.R;

/**
 * Main activity of MainActivityPong game.
 */
public class MainActivityPong extends AppCompatActivity {

    /*
    private static final int MENU_NEW_GAME = 1;
    private static final int MENU_RESUME = 2;
    private static final int MENU_EXIT = 3;
    */

    private PongThread mGameThread;
    private Bundle save;
    private AlertDialog mDlgMsg = null;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pong_layout);

        context = this;


        //update_Highscore();
    }


    /**

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


    private void showDialog_GameOver(int score) {
        mDlgMsg = new AlertDialog.Builder(context)
                .setTitle(getResources().getString(R.string.gameOver))
                .setMessage(getResources().getString(R.string.your_score_is) + ": " + score)
                .setPositiveButton(getResources().getString(R.string.again), (dialog, which) -> {
                    mDlgMsg.dismiss();
                })
                .setNegativeButton(getResources().getString(R.string.exit), (dialog, which) -> {
                    mDlgMsg.dismiss();
                    if(context instanceof MainActivityPong)
                        ((MainActivityPong) context).finish();
                })
                .show();
    }

     **/
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

        mGameThread.setOnEndGameListener(new PongThread.onEndGameListener() {
            @Override
            public void onEnd(int score, int exit_mode) {

                showDialog_GameOver(score,exit_mode);

            }
        });
    }


    private void showDialog_GameOver(int score, int exit_mode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (exit_mode == 1)
                {
                    mDlgMsg = new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.gameOver))
                            .setMessage(context.getString(R.string.your_score_is) + ": " + score + "\n" + context.getString(R.string.no_point))


                            .setPositiveButton(context.getString(R.string.again), (dialog, which) -> {
                                mDlgMsg.dismiss();
                            })
                            .setNegativeButton(context.getString(R.string.exit), (dialog, which) -> {
                                mDlgMsg.dismiss();
                                finish();
                            })
                            .show();
                }
                if (exit_mode == 2)
                {
                    mDlgMsg = new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.gameOver))
                            .setMessage(context.getString(R.string.your_score_is) + ": " + score + "\n" + context.getString(R.string.good_job))


                            .setPositiveButton(context.getString(R.string.again), (dialog, which) -> {
                                mDlgMsg.dismiss();
                            })
                            .setNegativeButton(context.getString(R.string.exit), (dialog, which) -> {
                                mDlgMsg.dismiss();
                                finish();
                            })
                            .show();
                }
                if (exit_mode == 3)
                {
                    mDlgMsg = new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.gameOver))
                            .setMessage(context.getString(R.string.your_score_is) + ": " + score + "\n" + context.getString(R.string.you_try))


                            .setPositiveButton(context.getString(R.string.again), (dialog, which) -> {
                                mDlgMsg.dismiss();
                            })
                            .setNegativeButton(context.getString(R.string.exit), (dialog, which) -> {
                                mDlgMsg.dismiss();
                                finish();

                            })
                            .show();
                }

            }
        });

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
