package com.nullpointerexception.retrogames.Snake;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.Components.Blocker;
import com.nullpointerexception.retrogames.R;

public class MainActivitySnake extends AppCompatActivity implements View.OnClickListener,
        CustomSnakeDialog.CustomSnakeDialogListener {

    private SnakePanelView mSnakePanelView;
    private TextView mScore, mHighScore;
    private String mhighScoreLoaded;
    Blocker mBlocker = new Blocker();

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

        mSnakePanelView.setOnEatListener(new SnakePanelView.OnEatListener() {
            @Override
            public void onEat(int point, int highScore) {
                MainActivitySnake.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String score = MainActivitySnake.this.getResources().getString(R.string.score) + point;
                        String highscore_string = MainActivitySnake.this.getResources().getString(R.string.highscore) + highScore;
                        mScore.setText(score);
                        mHighScore.setText(highscore_string);
                    }
                });
            }
        });

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
        mScore.setText(getResources().getString(R.string.default_score_snake));
        findViewById(R.id.left_btn).setOnClickListener(this);
        findViewById(R.id.right_btn).setOnClickListener(this);
        findViewById(R.id.top_btn).setOnClickListener(this);
        findViewById(R.id.bottom_btn).setOnClickListener(this);
        findViewById(R.id.start_btn).setOnClickListener(this);
        findViewById(R.id.restart_btn).setOnClickListener(this);
    }

    /**
     * Quando un pulsante viene premuto, lo individua e cambia la direzione del serpente
     * @param v View di ogni bottone dell'interfaccia
     */
    @Override
    public void onClick(View v) {
        if (!mBlocker.block(55)) {
            switch (v.getId()) {
                case R.id.left_btn:
                    mSnakePanelView.setSnakeDirection(GameType.LEFT);
                    break;
                case R.id.right_btn:
                    mSnakePanelView.setSnakeDirection(GameType.RIGHT);
                    break;
                case R.id.top_btn:
                    mSnakePanelView.setSnakeDirection(GameType.TOP);
                    break;
                case R.id.bottom_btn:
                    mSnakePanelView.setSnakeDirection(GameType.BOTTOM);
                    break;
                case R.id.start_btn:
                    openDialog();
                    break;
                case R.id.restart_btn:
                    mSnakePanelView.startGame(GameType.GENERIC_DIFFICULTY, true);
                    break;
            }
        }
    }

    public void openDialog() {
        CustomSnakeDialog csd = new CustomSnakeDialog();
        csd.show(getSupportFragmentManager(), "snake dialog");
    }

    @Override
    public void applyDifficult(int difficulty) {
        mSnakePanelView.startGame(difficulty, false);
        if(mhighScoreLoaded != null && !mhighScoreLoaded.isEmpty())
            mHighScore.setText(getResources().getString(R.string.highscore) + mhighScoreLoaded);
    }
}
