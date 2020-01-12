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

    private PongThread mGameThread;
    private Bundle save;
    private AlertDialog mDlgMsg = null;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pong_layout);

        context = this;

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

    /**
     * mostra il dialog quando la partita finisce
     * @param score
     * @param exit_mode
     */
    private void showDialog_GameOver(int score, int exit_mode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGameThread.playSound(1);
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
                                mGameThread.cleanUpIfEnd();
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
                                mGameThread.cleanUpIfEnd();
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
                                mGameThread.cleanUpIfEnd();
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
