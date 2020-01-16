package com.nullpointerexception.retrogames.Snake;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.Components.Blocker;
import com.nullpointerexception.retrogames.Components.SaveScore;
import com.nullpointerexception.retrogames.R;

public class MainActivitySnake extends AppCompatActivity implements View.OnClickListener,
        View.OnTouchListener, CustomSnakeDialog.CustomSnakeDialogListener {

    //Istanza della view personalizzata per snake
    private SnakePanelView mSnakePanelView;
    //Millisecondi e punteggio corrente
    private int ms, point;
    private TextView mScore, mHighScore;
    private ImageView mDpad;

    //Drawable per ogni direzione premuta
    private Drawable leftDrw, rightDrw, upDrw, downDrw, dpadDrw;
    //Stringa contenente l'highscore caricato dal database
    private String mhighScoreLoaded;
    Blocker mBlocker = new Blocker();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_snake);

        //Inizializza l'interfaccia grafica
        initUI();

        //Acquisisto lo score più alto dal database locale
        if(App.scoreboardDao.getGame(App.SNAKE) != null)
            mhighScoreLoaded = String.valueOf(App.scoreboardDao.getScore(App.SNAKE));
        else
            mhighScoreLoaded = String.valueOf(0);

       runOnUiThread(new Runnable() {
           @Override
           public void run() {
               mHighScore.setText(getResources().getString(R.string.highscore) + mhighScoreLoaded);
           }
       });

        mSnakePanelView.setOnEatListener((point, highScore) -> runOnUiThread(() -> {
            this.point = point;
            String score = MainActivitySnake.this.getResources().getString(R.string.score) + point;
            String highscore_string = MainActivitySnake.this.getResources().getString(R.string.highscore) + highScore;
            mScore.setText(score);
            mHighScore.setText(highscore_string);
        }));

        mSnakePanelView.setOnResetListener(new SnakePanelView.OnResetListener() {
            @Override
            public void onReset(int point) {
                MainActivitySnake.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String score = MainActivitySnake.this.getResources().getString(R.string.score) + point;
                        mScore.setText(score);
                    }
                });
            }
        });

    }
    /**
     * Inizializza l'interfaccia grafica impostando un listener al click su ongi pulsante
     */
    public void initUI() {
        mSnakePanelView = findViewById(R.id.snake_view);
        mScore = findViewById(R.id.score_tv);
        mHighScore = findViewById(R.id.highscore_tv);
        mDpad = findViewById(R.id.dpad_iv);
        mScore.setText(getResources().getString(R.string.default_score_snake));

        //Inizializza le risorse per evitare chiamate inutili
        Resources resources = getResources();
        leftDrw = resources.getDrawable(R.drawable.ouya_dpad_left);
        rightDrw = resources.getDrawable(R.drawable.ouya_dpad_right);
        upDrw = resources.getDrawable(R.drawable.ouya_dpad_up);
        downDrw = resources.getDrawable(R.drawable.ouya_dpad_down);
        dpadDrw = resources.getDrawable(R.drawable.ouya_dpad);
        mDpad.setImageDrawable(dpadDrw);

        findViewById(R.id.left_btn).setOnTouchListener(this);
        findViewById(R.id.right_btn).setOnTouchListener(this);
        findViewById(R.id.top_btn).setOnTouchListener(this);
        findViewById(R.id.bottom_btn).setOnTouchListener(this);
        findViewById(R.id.start_btn).setOnClickListener(this);
        findViewById(R.id.restart_btn).setOnClickListener(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //Non permette di premere di entrare nello switch prima di ms predefiniti
        if (!mBlocker.block(ms)) {
            switch (view.getId()) {
                case R.id.top_btn:
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mSnakePanelView.setSnakeDirection(GameType.TOP);
                            mDpad.setImageDrawable(upDrw);
                            break;
                        case MotionEvent.ACTION_UP:
                            mDpad.setImageDrawable(dpadDrw);
                            break;
                    }
                    break;
                case R.id.bottom_btn:
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mSnakePanelView.setSnakeDirection(GameType.BOTTOM);
                            mDpad.setImageDrawable(downDrw);
                            break;
                        case MotionEvent.ACTION_UP:
                            mDpad.setImageDrawable(dpadDrw);
                            break;
                    }
                    break;
                case R.id.left_btn:
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mSnakePanelView.setSnakeDirection(GameType.LEFT);
                            mDpad.setImageDrawable(leftDrw);
                            break;
                        case MotionEvent.ACTION_UP:
                            mDpad.setImageDrawable(dpadDrw);
                            break;
                    }
                    break;
                case R.id.right_btn:
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mSnakePanelView.setSnakeDirection(GameType.RIGHT);
                            mDpad.setImageDrawable(rightDrw);
                            break;
                        case MotionEvent.ACTION_UP:
                            mDpad.setImageDrawable(dpadDrw);
                            break;
                    }
                    break;
            }
        }
        return false;
    }

    /**
     * Quando un pulsante viene premuto, lo individua e cambia la direzione del serpente
     * @param v View di ogni bottone dell'interfaccia
     */
    @Override
    public void onClick(View v) {
        if (!mBlocker.block(ms)) {
            switch (v.getId()) {
                case R.id.start_btn:
                    openDialog();
                    break;
                case R.id.restart_btn:
                    mSnakePanelView.startGame(GameType.GENERIC_DIFFICULTY, true);
                    break;
            }
        }
    }

    /**
     * Mostra il dialog per la scelta della difficoltà
     */
    public void openDialog() {
        CustomSnakeDialog csd = new CustomSnakeDialog();
        csd.show(getSupportFragmentManager(), "snake dialog");
    }

    /**
     * Metodo usato per impostare la difficoltà
     *
     * @param difficulty un intero ricevuto dal dialog quando si seleziona la difficoltà
     */
    @Override
    public void applyDifficult(int difficulty) {
        switch (difficulty) {
            case GameType.EASY:
                ms = 1000 / 4; //divide i1 secondo per un intero prestabilito per stabilire gli ms minimi per i quali il giocatore
                break;         //non può premere i pulsanti
            case GameType.MEDIUM:
                ms = 1000 / 8;
                break;
            case GameType.HARD:
                ms = 1000 / 12;
                break;
        }

        mSnakePanelView.startGame(difficulty, false);
        if(mhighScoreLoaded != null && !mhighScoreLoaded.isEmpty())
            mHighScore.setText(getResources().getString(R.string.highscore) + mhighScoreLoaded);
    }

    /**
     * Quando l'activity va in pausa, il punteggio viene salvato nel database
     */
    @Override
    protected void onPause() {
        super.onPause();
        SaveScore game = new SaveScore();
        game.save(App.SNAKE, point, this);
    }

}
