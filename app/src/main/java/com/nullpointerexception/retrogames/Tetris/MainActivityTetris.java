package com.nullpointerexception.retrogames.Tetris;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.R;

public class MainActivityTetris extends AppCompatActivity {


    private static MediaPlayer player; //Gestione della riproduzione audio
    private Point mScreenSize = new Point(0, 0); //dimensione dello schermo
    private int mCellSize = 0; //dimensione di una singola cella
    private TetrisCtrl mTetrisCtrl;
    private TextView textViewScore, textViewTotalscore, mPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tetris);

        textViewScore = findViewById(R.id.textViewScore);
        textViewTotalscore = findViewById(R.id.textViewTotalscore);
        mPause = findViewById(R.id.pause_tv);

        //Gestione risoluzione dello schermo
        DisplayMetrics dm = this.getApplicationContext().getResources().getDisplayMetrics();
        mScreenSize.x = dm.widthPixels; //imposta larghezza schermo
        mScreenSize.y = dm.heightPixels; //imposta altezza schermo
        mCellSize = (mScreenSize.x / 8); //imposta dimensione della cella

        SharedPreferences prefs = getSharedPreferences(App.APP_VARIABLES, MODE_PRIVATE);
        if(prefs.getBoolean(App.TETRIS_TUTORIAL, true))
        {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.tetris_welcome)
                    .setMessage(R.string.tetris_tutorial)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface a, int b) {
                            prefs.edit().putBoolean(App.TETRIS_TUTORIAL, false).apply();
                            //Gestione musica
                            startMusic();
                            initTetrisCtrl();
                        }
                    })
                    .show();
        }else {
            startMusic();
            initTetrisCtrl();
        }

    }

    /**
     * Crea le immagini delle celle e inizializza il layoutCanvas
     */
    void initTetrisCtrl() {
        mTetrisCtrl = new TetrisCtrl(this, textViewScore, textViewTotalscore, mPause);
        //Crea le bitmap delle 8 immagini relative agli 8 tipi di cell.png
        Bitmap[] bitmaps = new Bitmap[8];
        bitmaps[0] = BitmapFactory.decodeResource(getResources(), R.drawable.cell0);
        bitmaps[1] = BitmapFactory.decodeResource(getResources(), R.drawable.cell1);
        bitmaps[2] = BitmapFactory.decodeResource(getResources(), R.drawable.cell2);
        bitmaps[3] = BitmapFactory.decodeResource(getResources(), R.drawable.cell3);
        bitmaps[4] = BitmapFactory.decodeResource(getResources(), R.drawable.cell4);
        bitmaps[5] = BitmapFactory.decodeResource(getResources(), R.drawable.cell5);
        bitmaps[6] = BitmapFactory.decodeResource(getResources(), R.drawable.cell6);
        bitmaps[7] = BitmapFactory.decodeResource(getResources(), R.drawable.cell7);


        for(int i=0; i <= 7; i++)
            mTetrisCtrl.addCellImage(i, bitmaps[i]);

        RelativeLayout layoutCanvas = findViewById(R.id.layoutCanvas);
        layoutCanvas.addView(mTetrisCtrl);
    }


    /**
     * Gestisce i bottoni per spostare i blocchi
     * @param view view
     */
    public void buttonDirection(View view) {
        switch( view.getId() ) {
            case R.id.btnLeft :
                mTetrisCtrl.block2Left();
                break;
            case R.id.btnRight :
                mTetrisCtrl.block2Right();
                break;
            case R.id.btnBottom :
                mTetrisCtrl.block2Bottom();
                break;
            case R.id.btnRotate :
                mTetrisCtrl.block2Rotate();
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mTetrisCtrl.restartGame();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mTetrisCtrl != null) {
            if (mTetrisCtrl.isInPause())
                mPause.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mTetrisCtrl.isInPause()) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mPause.setVisibility(View.GONE);
                mTetrisCtrl.setInPause(false);
                startMusic();
            }
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTetrisCtrl.pauseGame();
        player.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTetrisCtrl.setInPause(true);
        if(player.isPlaying())
            player.pause();
    }

    /**
     *  Fa partire la classica musica di MainActivityTetris
     */
    private void startMusic() {
        player = MediaPlayer.create(this, R.raw.tetris_song);
        player.setVolume(100, 100);
        player.setLooping(true);
        player.start();
    }

}
