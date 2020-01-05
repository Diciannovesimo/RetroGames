package com.nullpointerexception.retrogames.Tetris;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import static android.content.Context.MODE_PRIVATE;

public class TetrisCtrl extends View {
    Context context;
    final int MatrixSizeH = 10; //Numero di celle sull'asse x
    final int MatrixSizeV = 18; //Numero di celle sull'asse Y

    //Costanti per determinare da che lato il blocco deve ruotare
    final int DirRotate = 0;
    final int DirLeft = 1;
    final int DirRight = 2;
    final int DirDown = 3;

    //Tempo in ms per far muovere un blcco da una cella all'altra
    final int TimerGapStart = 1000;
    int TimerGapNormal = TimerGapStart;
    int TimerGapFast = 50;
    int mTimerGap = TimerGapNormal;


    int[][] mArMatrix = new int[MatrixSizeV][MatrixSizeH]; //Matrice delle celle
    double mBlockSize = 0; //Dimensione del blocco
    Point mScreenSize = new Point(0, 0); //Dimensione dello schermo
    int mNewBlockArea = 5; //Area massima del blocco
    int[][] mArNewBlock = new int[mNewBlockArea][mNewBlockArea]; //Matrice contenente il blocco generato
    int[][] mArNextBlock = new int[mNewBlockArea][mNewBlockArea]; //TODO da capire
    Point mNewBlockPos = new Point(0, 0); //Posizione del blocco sullo schermo
    Bitmap[] mArBmpCell = new Bitmap[8]; //Immagini delle celle
    AlertDialog mDlgMsg = null;
    SharedPreferences mPref = null; //TODO da togliere
    int mScore = 0; //Punteggio corrente
    int mTopScore = 0; //Punteggio massimo

    /**
     * Restituisce un oggetto di tipo rect contenente le coordinate intere per un rettangolo
     * @param x contiene il numero di pixel per l'asse x del rettangolo
     * @param y contiene il numero di pixel per l'asse y del rettangolo
     * @return oggetto di tipo rect
     */
    Rect getBlockArea(int x, int y) {
        Rect rtBlock = new Rect();
        rtBlock.left = (int)(x * mBlockSize);
        rtBlock.right = (int)(rtBlock.left + mBlockSize);
        rtBlock.bottom = mScreenSize.y - (int)(y * mBlockSize);
        rtBlock.top = (int)(rtBlock.bottom - mBlockSize);
        return rtBlock;
    }

    /**
     * Restituisce un valore intero random compresto tra min e max
     * @param min valore intero minimo
     * @param max valore intero massimo
     * @return numero random di tipo intero
     */
    int random(int min, int max) {
        int rand = (int)(Math.random() * (max - min + 1)) + min;
        return rand;
    }

    //TODO da togliere, sono le sharedPreferences
    public TetrisCtrl(Context context) {
        super(context);
        this.context = context;
        mPref = context.getSharedPreferences("info",MODE_PRIVATE);
        mTopScore = mPref.getInt("TopScore", 0);
    }

    /**
     * Inizializzazione delle varibili
     * @param canvas oggetto di tipo Canvas
     */
    void initVariables(Canvas canvas) {
        mScreenSize.x = canvas.getWidth();  //imposta larghezza schermo
        mScreenSize.y = canvas.getHeight(); //imposta altezza schermo
        mBlockSize = mScreenSize.x / MatrixSizeH; //determina dimensione del blocco

        startGame();
    }

    /**
     * Genera un nuovo blocco
     * @param arBlock
     */
    void addNewBlock(int[][] arBlock) {
        //Imposta tutta la matrice a 0
        for(int i=0; i < mNewBlockArea; i++) {
            for(int j=0; j < mNewBlockArea; j++)
                arBlock[i][j] = 0;
        }

        mNewBlockPos.x = (MatrixSizeH - mNewBlockArea) / 2; //Posiziona il nuovo blocco a centro dell'asse x
        mNewBlockPos.y = MatrixSizeV - mNewBlockArea;   //Posiziona il nuovo blocco sopra lo schermo

        //Genera il blocco random
        int blockType = random(1, 7);

        switch(blockType) {
            case 1:
                // Block 1 : --
                arBlock[2][1] = 1;
                arBlock[2][2] = 1;
                arBlock[2][3] = 1;
                arBlock[2][4] = 1;
                break;
            case 2:
                // Block 2 : └-
                arBlock[3][1] = 2;
                arBlock[2][1] = 2;
                arBlock[2][2] = 2;
                arBlock[2][3] = 2;
                break;
            case 3:
                // Block 3 : -┘
                arBlock[2][1] = 3;
                arBlock[2][2] = 3;
                arBlock[2][3] = 3;
                arBlock[3][3] = 3;
                break;
            case 4:
                // Block 4 : ▣
                arBlock[2][2] = 4;
                arBlock[2][3] = 4;
                arBlock[3][2] = 4;
                arBlock[3][3] = 4;
                break;
            case 5:
                // Block 5 : ＿｜￣
                arBlock[3][3] = 5;
                arBlock[3][2] = 5;
                arBlock[2][2] = 5;
                arBlock[2][1] = 5;
                break;
            case 6:
                // Block 6 : ＿｜＿
                arBlock[2][1] = 6;
                arBlock[2][2] = 6;
                arBlock[2][3] = 6;
                arBlock[3][2] = 6;
                break;
            default:
                // Block 7 : ￣｜＿
                arBlock[2][3] = 7;
                arBlock[2][2] = 7;
                arBlock[3][2] = 7;
                arBlock[3][1] = 7;
                break;
        }
        redraw();
    }

    /**
     * Ridisegna la View
     */
    public void redraw() {
        this.invalidate();
    }

    /**
     * Controlla se il nuovo blocco può ruotare
     * @param arNewBlock matrice contenente il nuovo blocco
     * @param posBlock posizione del blocco
     * @return true se il blocco può ruotare, altrimenti false
     */
    boolean checkBlockSafe(int[][] arNewBlock, Point posBlock) {
        for(int i=0; i < mNewBlockArea ; i++) {
            for(int j=0; j < mNewBlockArea ; j++) {
                if( arNewBlock[i][j] == 0 )
                    continue;
                int x = posBlock.x + j;
                int y = posBlock.y + i;
                if( checkCellSafe(x, y) == false )
                    return false;
            }
        }
        return true;
    }

    /**
     * Controlla se la cella è disponibile
     * @param x posizione sull'asse x della cella
     * @param y posizione sull'asse y della cella
     * @return true se la cella è disponibile, altrimenti false
     */
    boolean checkCellSafe(int x, int y) {
        if( x < 0 )
            return false;
        if( x >= MatrixSizeH )
            return false;
        if( y < 0 )
            return false;
        if( y >= MatrixSizeV )
            return true;
        if( mArMatrix[y][x] > 0 )
            return false;
        return true;
    }

    /**
     * Muove 
     * @param dir
     * @param arNewBlock
     * @param posBlock
     */
    void moveNewBlock(int dir, int[][] arNewBlock, Point posBlock) {
        switch( dir ) {
            case DirRotate :
                int[][] arRotate = new int[mNewBlockArea ][mNewBlockArea ];
                for(int i=0; i < mNewBlockArea ; i++) {
                    for(int j=0; j < mNewBlockArea ; j++) {
                        arRotate[mNewBlockArea - j - 1][i] = arNewBlock[i][j];
                    }
                }
                for(int i=0; i < mNewBlockArea ; i++) {
                    for(int j=0; j < mNewBlockArea ; j++) {
                        arNewBlock[i][j] = arRotate[i][j];
                    }
                }
                break;
            case DirLeft :
                posBlock.x --;
                break;
            case DirRight :
                posBlock.x ++;
                break;
            case DirDown :
                posBlock.y --;
                break;
        }
    }


    int[][] duplicateBlockArray(int[][] arBlock) {
        int size1 = mNewBlockArea , size2 = mNewBlockArea ;
        int[][] arClone = new int[size1][size2];
        for(int i=0; i < size1; i++) {
            for(int j=0; j < size2; j++) {
                arClone[i][j] = arBlock[i][j];
            }
        }
        return arClone;
    }

    void copyBlock2Matrix(int[][] arBlock, Point posBlock) {
        for(int i=0; i < mNewBlockArea ; i++) {
            for(int j=0; j < mNewBlockArea ; j++) {
                if( arBlock[i][j] == 0 )
                    continue;
                mArMatrix[posBlock.y + i][posBlock.x + j] = arBlock[i][j];
                arBlock[i][j] = 0;
            }
        }
    }

    int checkLineFilled() {
        int filledCount = 0;
        boolean bFilled;

        for(int i=0; i < MatrixSizeV; i++) {
            bFilled = true;
            for(int j=0; j < MatrixSizeH; j++) {
                if( mArMatrix[i][j] == 0 ) {
                    bFilled = false;
                    break;
                }
            }
            if( bFilled == false )
                continue;

            filledCount ++;
            for(int k=i+1; k < MatrixSizeV; k++) {
                for (int j = 0; j < MatrixSizeH; j++) {
                    mArMatrix[k-1][j] = mArMatrix[k][j];
                }
            }
            for (int j = 0; j < MatrixSizeH; j++) {
                mArMatrix[MatrixSizeV - 1][j] = 0;
            }
            i--;
        }


        mScore += filledCount * filledCount;
        if( mTopScore < mScore ) {
            mTopScore = mScore;
            SharedPreferences.Editor edit = mPref.edit();
            edit.putInt("TopScore", mTopScore);
            edit.commit();
        }
        return filledCount;
    }

    boolean isGameOver() {
        boolean canMove = checkBlockSafe(mArNewBlock, mNewBlockPos);
        return !canMove;
    }

    boolean moveNewBlock(int dir) {
        int[][] arBackup = duplicateBlockArray( mArNewBlock );
        Point posBackup = new Point(mNewBlockPos);

        moveNewBlock(dir, mArNewBlock, mNewBlockPos);
        boolean canMove = checkBlockSafe(mArNewBlock, mNewBlockPos);
        if( canMove ) {
            redraw();
            return true;
        }

        for(int i=0; i < mNewBlockArea ; i++) {
            for(int j=0; j < mNewBlockArea ; j++) {
                mArNewBlock[i][j] = arBackup[i][j];
            }
        }

        mNewBlockPos.set(posBackup.x, posBackup.y);
        return false;
    }

    void showScore(Canvas canvas, int score) {
        int fontSize = mScreenSize.x / 20;
        Paint pnt = new Paint();
        pnt.setTextSize(fontSize);
        pnt.setARGB(128, 255, 255,255);
        int posX = (int)(fontSize * 0.5);
        int poxY = (int)(fontSize * 1.5);
        canvas.drawText("Score : " + mScore, posX, poxY, pnt);

        poxY += (int)(fontSize * 1.5);
        canvas.drawText("Top Score : " + mTopScore, posX, poxY, pnt);
    }

    void showMatrix(Canvas canvas, int[][] arMatrix, boolean drawEmpth) {
        for(int i=0; i < MatrixSizeV; i++) {
            for(int j=0; j < MatrixSizeH; j++) {
                if( arMatrix[i][j] == 0 && drawEmpth == false )
                    continue;
                showBlockImage(canvas, j, i, arMatrix[i][j]);
            }
        }
    }

    void showBlockImage(Canvas canvas, int blockX, int blockY, int blockType) {
        Rect rtBlock = getBlockArea(blockX, blockY);

        canvas.drawBitmap(mArBmpCell[blockType], null, rtBlock, null);
    }

    /*** Interface start ***/

    public void addCellImage(int index, Bitmap bmp) {
        mArBmpCell[index] = bmp;
    }

    public boolean block2Left() {
        return moveNewBlock(DirLeft);
    }

    public boolean block2Right() {
        return moveNewBlock(DirRight);
    }

    public boolean block2Rotate() {
        return moveNewBlock(DirRotate);
    }

    public boolean block2Bottom() {
        mTimerFrame.removeMessages(0);
        mTimerGap = TimerGapFast;
        mTimerFrame.sendEmptyMessageDelayed(0, 10);
        return true;
    }

    public void pauseGame() {
        if( mDlgMsg != null )
            return;

        mTimerFrame.removeMessages(0);
    }

    public void restartGame() {
        if( mDlgMsg != null )
            return;

        mTimerFrame.sendEmptyMessageDelayed(0, 1000);
    }

    public void startGame() {
        mScore = 0;

        for(int i=0; i < MatrixSizeV; i++) {
            for(int j=0; j < MatrixSizeH; j++) {
                mArMatrix[i][j] = 0;
            }
        }

        addNewBlock(mArNewBlock);
        addNewBlock(mArNextBlock);
        TimerGapNormal = TimerGapStart;
        mTimerFrame.sendEmptyMessageDelayed(0, 10);
    }

    /*** Interface end ***/

    void showDialog_GameOver() {
        mDlgMsg = new AlertDialog.Builder(context)
                .setTitle("Notice")
                .setMessage("Game over! Your score is " + mScore)
                .setPositiveButton("Again",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mDlgMsg = null;
                                startGame();
                            }
                        })
                .show();
    }

    public void onDraw(Canvas canvas) {
        if( mBlockSize < 1 )
            initVariables(canvas);
        canvas.drawColor(Color.DKGRAY);

        showMatrix(canvas, mArMatrix, true);
        showNewBlock(canvas);
        showScore(canvas, mScore);
        showNextBlock(canvas, mArNextBlock);
    }

    void showNewBlock(Canvas canvas) {
        for(int i=0; i < mNewBlockArea ; i++) {
            for(int j=0; j < mNewBlockArea ; j++) {
                if( mArNewBlock[i][j] == 0 )
                    continue;
                showBlockImage(canvas, mNewBlockPos.x + j, mNewBlockPos.y + i, mArNewBlock[i][j]);
            }
        }
    }

    Handler mTimerFrame = new Handler() {
        public void handleMessage(Message msg) {
            boolean canMove = moveNewBlock(DirDown);
            if( !canMove ) {
                copyBlock2Matrix(mArNewBlock, mNewBlockPos);
                checkLineFilled();
                copyBlockArray(mArNextBlock, mArNewBlock);
                addNewBlock(mArNextBlock);
                TimerGapNormal -= 2;
                mTimerGap = TimerGapNormal;
                if( isGameOver() ) {
                    showDialog_GameOver();
                    return;
                }
            }

            this.sendEmptyMessageDelayed(0, mTimerGap);
        }
    };

    void copyBlockArray(int[][] arFrom, int[][] arTo) {
        for(int i=0; i < mNewBlockArea; i++) {
            for(int j=0; j < mNewBlockArea; j++) {
                arTo[i][j] = arFrom[i][j];
            }
        }
    }

    void showNextBlock(Canvas canvas, int[][] arBlock) {
        for(int i=0; i < mNewBlockArea; i++) {
            for(int j=0; j < mNewBlockArea; j++) {
                int blockX = j;
                int blockY = mNewBlockArea - i;
                showBlockColor(canvas, blockX, blockY, arBlock[i][j]);
            }
        }
    }

    void showBlockColor(Canvas canvas, int blockX, int blockY, int blockType) {
        int[] arColor = {Color.argb(32,255,255,255),
                Color.argb(128,255,0,0),
                Color.argb(128,255,255,0),
                Color.argb(128,255,160,160),
                Color.argb(128,100,255,100),
                Color.argb(128,255,128,100),
                Color.argb(128,0,0,255),
                Color.argb(128,100,100,255)};
        int previewBlockSize = mScreenSize.x / 20;

        Rect rtBlock = new Rect();
        rtBlock.top = (blockY - 1) * previewBlockSize;
        rtBlock.bottom = rtBlock.top + previewBlockSize;
        rtBlock.left = mScreenSize.x - previewBlockSize * (mNewBlockArea - blockX);
        rtBlock.right = rtBlock.left + previewBlockSize;
        int crBlock = arColor[ blockType ];

        Paint pnt = new Paint();
        pnt.setStyle(Paint.Style.FILL);
        pnt.setColor(crBlock);
        canvas.drawRect(rtBlock, pnt);
    }

}
