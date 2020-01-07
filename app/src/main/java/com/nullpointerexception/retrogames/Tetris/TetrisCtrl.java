package com.nullpointerexception.retrogames.Tetris;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
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

import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.Components.SaveScore;
import com.nullpointerexception.retrogames.R;

public class TetrisCtrl extends View {

    private Context context;
    private final int MatrixSizeH = 10; //Numero di celle sull'asse x
    private final int MatrixSizeV = 18; //Numero di celle sull'asse Y

    //Costanti per determinare l'azione che il blocco deve eseguire
    private final int DirRotate = 0;    //rotazione
    private final int DirLeft = 1;      //sposta a sinistra
    private final int DirRight = 2;     //sposta a destra
    private final int DirDown = 3;      //sposta in basso

    //Tempo in ms per far muovere un blcco da una cella all'altra
    private final int TimerGapStart = 1000;
    private int TimerGapNormal = TimerGapStart;
    private int TimerGapFast = 100;
    private int mTimerGap = TimerGapNormal;


    private double mBlockSize = 0; //Dimensione del blocco
    private int mNewBlockArea = 5; //Area massima del blocco
    private int[][] mArMatrix = new int[MatrixSizeV][MatrixSizeH]; //Matrice del gioco
    private int[][] mArNewBlock = new int[mNewBlockArea][mNewBlockArea]; //Matrice contenente il blocco generato
    private int[][] mArNextBlock = new int[mNewBlockArea][mNewBlockArea]; //Matrice contenente il prossimo blocco generato
    private Point mScreenSize = new Point(0, 0); //Dimensione dello schermo
    private Point mNewBlockPos = new Point(0, 0); //Posizione del blocco sullo schermo
    private Bitmap[] mArBmpCell = new Bitmap[8]; //Immagini delle celle
    private AlertDialog mDlgMsg = null;
    private int mScore = 0; //Punteggio corrente
    private SharedPreferences totalscoreShared;
    private long mTopScore;


    /**
     * Restituisce un oggetto di tipo rect contenente le coordinate intere per una figura
     * @param x contiene il numero di pixel per l'asse x della figura
     * @param y contiene il numero di pixel per l'asse y della figura
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

    /**
     * Viene istanziato il Tetris Controller e recupera il topscore dal database locale
     * @param context contesto
     */
    public TetrisCtrl(Context context) {
        super(context);
        this.context = context;

        /**
        //Prende il totalscore dalle sharedPreferences
        totalscoreShared = context.getSharedPreferences(App.TOTALSCORE, Context.MODE_PRIVATE);
        mTopScore = totalscoreShared.getLong(App.TOTALSCORE, 0);  //Leggo il vecchio totalscore
         **/

        //Prendo il topscore dal database locale
        if(App.scoreboardDao.getGame(App.TETRIS) != null) //Controllo se già esiste un topscore
            //Esiste già un topscore
            mTopScore = App.scoreboardDao.getScore(App.TETRIS); //Leggo il vecchio topscore
        else
            //Non esiste un topscore
            mTopScore = 0;




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
     * Muove il nuovo blocco che sta cadendo dall'alto
     * @param dir intero che indica da che lato il blocco deve ruotare
     * @param arNewBlock matrice contenente il blocco generato
     * @param posBlock indica la posizione del blocco sullo schermo
     */
    void moveNewBlock(int dir, int[][] arNewBlock, Point posBlock) {
        switch( dir ) {
            case DirRotate : //ruota il blocco

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
            case DirLeft : //sposta il blocco a sinistra
                posBlock.x --;
                break;
            case DirRight : //sposta il blocco a destra
                posBlock.x ++;
                break;
            case DirDown : //sposta il blocco verso il basso
                posBlock.y --;
                break;
        }
    }

    /**
     * Duplica una matrice di interi
     * @param arBlock matrice di interi da duplicare
     * @return matrice di interi duplicata
     */
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

    /**
     * Copia il blocco in una seconda matrice
     * @param arBlock  matrice di interi contenente il blocco
     * @param posBlock posizione del blocco
     */
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

    /**
     * Controlla se le righe sono piene, se lo sono cancella la riga
     * e determina il nuovo score e l'eventuale topscore
     * @return numero intero di righe rimosse
     */
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

            SaveScore tetris = new SaveScore();
            tetris.save(App.TETRIS, mScore,getContext());

        }
        return filledCount;
    }

    /**
     * Controlla se la partita è terminata
     * @return true se la partita è terminata, altrimenti false
     */
    boolean isGameOver() {
        boolean canMove = checkBlockSafe(mArNewBlock, mNewBlockPos);
        return !canMove;
    }

    /**
     * Muove il nuovo blocco che sta cadendo dall'alto
     * @param dir intero che indica l'azione che il nuovo blocco deve eseguire
     * @return ritorna true se il blocco si è mosso altrimenti ritorna false
     */
    boolean moveNewBlock(int dir) {
        //Backup della matrice e della posizione
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

    /**
     * Mostra lo score in real-time sullo schermo
     * @param canvas foglio da disegno
     */
    void showScore(Canvas canvas) {
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

    /**
     * Mostra la matrice di tutto il gioco sullo schermo
     * @param canvas foglio da disegno
     * @param arMatrix matrice di gioco
     */
    void showMatrix(Canvas canvas, int[][] arMatrix) {
        for(int i=0; i < MatrixSizeV; i++) {
            for(int j=0; j < MatrixSizeH; j++)
                showBlockImage(canvas, j, i, arMatrix[i][j]);   //Stampa la matrice

        }
    }

    /**
     * Mostra il blocco che cade
     * @param canvas foglio di calcolo
     * @param blockX indica la posizione sull'asse x del blocco
     * @param blockY indica la posizione sull'asse y del blocco
     * @param blockType indica il tipo di blocco che sta cadendo
     */
    void showBlockImage(Canvas canvas, int blockX, int blockY, int blockType) {
        Rect rtBlock = getBlockArea(blockX, blockY); //crea un oggetto di tipo rect contenente le coordinate intere per una figura
        canvas.drawBitmap(mArBmpCell[blockType], null, rtBlock, null);
    }

    /**
     * Aggiunge l'immagine della cella nell'array di Bipmap
     * @param index intero contenente la posizione in cui si vuole memorizzare l'immagine
     * @param bmp immagine della cella
     */
    public void addCellImage(int index, Bitmap bmp) {
        mArBmpCell[index] = bmp;
    }

    /**
     * Sposta il blocco a sinistra
     * @return true se il blocco si è mosso, altrimenti false
     */
    public boolean block2Left() {
        return moveNewBlock(DirLeft);
    }

    /**
     * Sposta il blocco a destra
     * @return true se il blocco si è mosso, altrimenti false
     */
    public boolean block2Right() {
        return moveNewBlock(DirRight);
    }

    /**
     * Ruota il blocco
     * @return true se il blocco si ha ruotato, altrimenti false
     */
    public boolean block2Rotate() {
        return moveNewBlock(DirRotate);
    }

    /**
     * Spinge il blocco verso il basso
     * @return true se il blocco si è mosso
     */
    public boolean block2Bottom() {
        //Viene diminuito il tempo per far cadere il blocco
        mTimerFrame.removeMessages(0);
        mTimerGap = TimerGapFast;
        mTimerFrame.sendEmptyMessageDelayed(0, 10);
        return true;
    }

    /**
     * Permette di far andare il gioco in pausa
     */
    public void pauseGame() {
        if( mDlgMsg != null )
            return;

        mTimerFrame.removeMessages(0);
    }

    /**
     * Permette di far riprendere il gioco
     */
    public void restartGame() {
        if( mDlgMsg != null )
            return;

        mTimerFrame.sendEmptyMessageDelayed(0, 1000);
    }

    /**
     * Permette di far iniziare il gioco
     */
    public void startGame() {
        mScore = 0;

        //La matrice di gioco viene inizializzata con tutti 0
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

    //TODO vedere qui per eventualmente rimuovere il dialog e per cambiare le stringhe
    /**
     * Mostra il dialogn di GameOver
     */
    void showDialog_GameOver() {
        mDlgMsg = new AlertDialog.Builder(context)
                .setTitle(getResources().getString(R.string.gameOver))
                .setMessage(getResources().getString(R.string.your_score_is) + ": " + mScore)
                .setPositiveButton(getResources().getString(R.string.again), (dialog, which) -> {
                    mDlgMsg.dismiss();
                    startGame();
                })
                .setNegativeButton(getResources().getString(R.string.exit), (dialog, which) -> {
                    mDlgMsg.dismiss();
                    if(context instanceof Tetris)
                        ((Tetris) context).finish();
                })
                .show();
    }

    /**
     * Mostra sullo schermo tutti gli elementi necessari per poter interagire con il gioco
     * @param canvas foglio di disegno
     */
    public void onDraw(Canvas canvas) {
        if( mBlockSize < 1 )
            initVariables(canvas);
        canvas.drawColor(Color.DKGRAY);

        showMatrix(canvas, mArMatrix);
        showNewBlock(canvas);
        showScore(canvas);
        showNextBlock(canvas, mArNextBlock);
    }

    /**
     * Mostra il nuovo blocco generato
     * @param canvas foglio di disegno
     */
    void showNewBlock(Canvas canvas) {
        for(int i=0; i < mNewBlockArea ; i++) {
            for(int j=0; j < mNewBlockArea ; j++) {
                if( mArNewBlock[i][j] == 0 )
                    continue;
                showBlockImage(canvas, mNewBlockPos.x + j, mNewBlockPos.y + i, mArNewBlock[i][j]);
            }
        }
    }

    /**
     * Usato per diminuire il tempo di caduta del blocco
     */
    @SuppressLint("HandlerLeak")
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

    /**
     * Copia un array di interi in un secondo array di interi
     * @param arFrom array di interi da copiare
     * @param arTo array di interi copiato
     */
    void copyBlockArray(int[][] arFrom, int[][] arTo) {
        for(int i=0; i < mNewBlockArea; i++) {
            for(int j=0; j < mNewBlockArea; j++) {
                arTo[i][j] = arFrom[i][j];
            }
        }
    }

    /**
     * Mostra il prossimo blocco
     * @param canvas foglio di disegno
     * @param arBlock matrice di interi del prossimo blocco
     */
    void showNextBlock(Canvas canvas, int[][] arBlock) {
        for(int i=0; i < mNewBlockArea; i++) {
            for(int j=0; j < mNewBlockArea; j++) {
                int blockX = j;
                int blockY = mNewBlockArea - i;
                showBlockColor(canvas, blockX, blockY, arBlock[i][j]);
            }
        }
    }

    /**
     * Usato per mostrare il prossimo blocco
     * @param canvas foglio di disegno
     * @param blockX blocco sull'asse x
     * @param blockY blocco sull'asse y
     * @param blockType tipo di blocco
     */
    void showBlockColor(Canvas canvas, int blockX, int blockY, int blockType) {
        //vettore contenente tutti i tipi di blocchi generabili
        int[] arColor = {Color.argb(32,255,255,255),  //Bianco
                Color.argb(128,147,4,0),    //Rosso
                Color.argb(128,172,163,0),  //Giallo
                Color.argb(128,146,1,80),//Rosa
                Color.argb(128,0,139,32),//Verde
                Color.argb(128,163,92,0),//Rosa scuro
                Color.argb(128,19,35,229),    //Blu
                Color.argb(128,0,97,153)};//Grigio
        int previewBlockSize = mScreenSize.x / 20;

        //Creazione di una figura tramite un oggetto di tipo Rect
        Rect rtBlock = new Rect();
        rtBlock.top = (blockY - 1) * previewBlockSize;
        rtBlock.bottom = rtBlock.top + previewBlockSize;
        rtBlock.left = mScreenSize.x - previewBlockSize * (mNewBlockArea - blockX);
        rtBlock.right = rtBlock.left + previewBlockSize;
        int crBlock = arColor[ blockType ];

        //Creo un oggetto di tipo paint contenente stile e colore
        Paint pnt = new Paint();
        pnt.setStyle(Paint.Style.FILL);
        pnt.setColor(crBlock);
        canvas.drawRect(rtBlock, pnt);  //Disegno la figura associandoli un paint
    }

}
