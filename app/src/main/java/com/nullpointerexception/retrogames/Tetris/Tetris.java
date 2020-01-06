package com.nullpointerexception.retrogames.Tetris;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.R;

public class Tetris extends AppCompatActivity {


    private static MediaPlayer player; //Gestione della riproduzione audio
    private Point mScreenSize = new Point(0, 0); //dimensione dello schermo
    private Point mMousePos = new Point(-1, -1); //posizione del tocco
    private int mCellSize = 0; //dimensione di una singola cella
    private TetrisCtrl mTetrisCtrl;
    private boolean mIsTouchMove = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tetris);

        //Gestione musica
        startMusic();

        //Gestione risoluzione dello schermo
        DisplayMetrics dm = this.getApplicationContext().getResources().getDisplayMetrics();
        mScreenSize.x = dm.widthPixels; //imposta larghezza schermo
        mScreenSize.y = dm.heightPixels; //imposta altezza schermo
        mCellSize = (mScreenSize.x / 8); //imposta dimensione della cella

        initTetrisCtrl();
    }


    /**
     * Crea le immagini delle celle e inizializza il layoutCanvas
     */
    void initTetrisCtrl() {
        mTetrisCtrl = new TetrisCtrl(this);
        //Crea le bitmap delle 8 immagini relative agli 8 tipi di cell.png
        for(int i=0; i <= 7; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cell0 + i);
            mTetrisCtrl.addCellImage(i, bitmap);
        }
        RelativeLayout layoutCanvas = findViewById(R.id.layoutCanvas);
        layoutCanvas.addView(mTetrisCtrl);
    }


    /**
     * Gestisce gli eventi causati da un tocco sullo schermo
     * @param event gestisce i tocchi sullo schermo
     * @return
     */
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        //Gestisco il tocco sullo schermo
        switch( event.getAction() ) {

            //Quando tocco lo schermo parte ACTION_DOWN
            case MotionEvent.ACTION_DOWN :
                mIsTouchMove = false;
                if( event.getY() < (int)(mScreenSize.y * 0.75)) { //Il tocco è vero solo quando si preme sulla parte superiore dello schermo (il 75%)
                    //Prendo la posizione del tocco sullo schermo
                    mMousePos.x = (int) event.getX();
                    mMousePos.y = (int) event.getY();
                }
                break;

            //Quando cambia la parte di schermo premuta parte ACTION_MOVE
            case MotionEvent.ACTION_MOVE :
                if( mMousePos.x < 0 )
                    break;

                if( (event.getX() - mMousePos.x) > mCellSize ) {
                    mTetrisCtrl.block2Right(); //Ruoto il blocco a destra
                    //Prendo la posizione del tocco sullo schermo
                    mMousePos.x = (int) event.getX();
                    mMousePos.y = (int) event.getY();
                    mIsTouchMove = true;
                } else if( (mMousePos.x - event.getX()) > mCellSize ) {
                    mTetrisCtrl.block2Left(); //Ruoto il blocco a sinistra
                    //Prendo la posizione del tocco sullo schermo
                    mMousePos.x = (int) event.getX();
                    mMousePos.y = (int) event.getY();
                    mIsTouchMove = true;
                }
                break;

            //Quando tolgo il dito parte ACTION_UP
            case MotionEvent.ACTION_UP :
                if( mIsTouchMove == false && mMousePos.x > 0 )
                    mTetrisCtrl.block2Rotate(); //Ruoto il blocco
                mMousePos.set(-1, -1);  //Risetto la posizione a -1,-1 del puntatore
                break;
        }
        return true;
    }


    /**
     * Gestisce i bottoni per spostare i blocchi
     * @param view
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
            /*case R.id.btnRotate :     //TODO vedere qui per implementare il bottone per far ruotare i blocchi
                mTetrisCtrl.block2Rotate();
                break;*/
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
        if(!player.isPlaying())
            startMusic();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTetrisCtrl.pauseGame();
        player.stop();
    }

    /**
     *  Fa partire la classica musica di Tetris
     */
    void startMusic() {
        player = MediaPlayer.create(this, R.raw.tetris_song);
        player.setVolume(100, 100);
        player.setLooping(true);
        player.start();
    }

}
