package com.nullpointerexception.retrogames.Pong;

import android.app.AlertDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.Components.SaveScore;
import com.nullpointerexception.retrogames.Hole.MainActivityHole;
import com.nullpointerexception.retrogames.R;

/**
 * Main activity of MainActivityPong game.
 */
public class MainActivityPong extends AppCompatActivity {

    private TextView mScore, mHighScore;
    private int highScore, score;
    private PongThread mGameThread;
    private Bundle save;
    private AlertDialog mDlgMsg = null;
    private Context context;

    private SoundPool soundPool;
    private final int NUMBER_OF_SIMULTANEOUS_SOUNDS = 5;
    private final float LEFT_VOLUME_VALUE = 1.0f;
    private final float RIGHT_VOLUME_VALUE = 1.0f;
    private final int MUSIC_LOOP = 0;
    private final int SOUND_PLAY_PRIORITY = 1;
    private final float PLAY_RATE= 1.0f;
    private int idSound;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pong_layout);

        //Prendo il topscore dal database locale
        if(App.scoreboardDao.getGame(App.PONG) != null) //Controllo se già esiste un topscore
            //Esiste già un topscore
            highScore = App.scoreboardDao.getScore(App.PONG); //Leggo il vecchio topscore
        else
            //Non esiste un topscore
            highScore = 0;





        context = this;

        initSound();

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


    }

    /**
     * Inizializza SoundPool in base alla versione di android
     */
    private void initSound() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool= new SoundPool.Builder()
                    .setMaxStreams(NUMBER_OF_SIMULTANEOUS_SOUNDS)
                    .build();
        } else
            soundPool= new SoundPool(NUMBER_OF_SIMULTANEOUS_SOUNDS, AudioManager.STREAM_MUSIC, 0);

        //inserisce i suoni
        idSound = soundPool.load(context, R.raw.gameover_pong, SOUND_PLAY_PRIORITY);
    }

    /**
     * Riproduce i suoni
     */
    public int playSound() {
        return soundPool.play(idSound,
                LEFT_VOLUME_VALUE,
                RIGHT_VOLUME_VALUE,
                SOUND_PLAY_PRIORITY,
                MUSIC_LOOP,
                PLAY_RATE);
    }

    /**
     * Disalloca l'audio
     */
    public final void cleanUpIfEnd() {
        soundPool.release();
        soundPool = null;
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
                Log.i("lol", String.valueOf(playSound()));

                if (exit_mode == 1)
                {
                    mDlgMsg = new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.gameOver))
                            .setMessage(context.getString(R.string.your_score_is) + ": " + score + "\n" + context.getString(R.string.no_point))
                            .setPositiveButton(context.getString(R.string.again), (dialog, which) -> {
                                mDlgMsg.dismiss();
                                resetScoreTv();
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
                                resetScoreTv();
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
                                resetScoreTv();
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

    private void resetScoreTv() {
        mHighScore.setText(getResources().getString(R.string.highscore) + highScore);
        mScore.setText(getResources().getString(R.string.score) + score);
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
        mPongView.setStatusView(findViewById(R.id.status));
        mPongView.setScoreView(findViewById(R.id.score));


        mGameThread = mPongView.getGameThread();

        mScore = findViewById(R.id.score_tv);
        mHighScore = findViewById(R.id.highscore_tv);

        mHighScore.setText(getResources().getString(R.string.high_score_) + highScore);
        mScore.setText(getResources().getString(R.string.score_0));

        if (savedInstanceState == null) {
            mGameThread.setState(PongThread.STATE_READY);
        } else {
            mGameThread.restoreState(savedInstanceState);
        }
        mGameThread.setOnEndGameListener((score, exit_mode) -> showDialog_GameOver(score,exit_mode));
        mGameThread.setOnAddScoreListener(new PongThread.OnAddScoreListener() {
            @Override
            public void onAddScore(int humanScore, int computerScore) {
                int score = humanScore - computerScore;
                if(score >= 0)
                    mScore.setText(getResources().getString(R.string.score) + score);

                if(score > highScore)
                    mHighScore.setText(getResources().getString(R.string.high_score) + score);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanUpIfEnd();
    }
}
