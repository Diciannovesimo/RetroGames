package com.nullpointerexception.retrogames.Snake;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.Components.SaveScore;
import com.nullpointerexception.retrogames.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class SnakePanelView extends View {
    private final static String TAG = SnakePanelView.class.getSimpleName();

    //È una lista di GridSquare usata per creare la mappa
    private List<List<GridSquare>> mGridSquare = new ArrayList<>();
    //Lista contenente le posizioni del serpente
    private List<GridPosition> mSnakePositions = new ArrayList<>();
    //Lista creata per contenere le coordinate della mappa
    private Set<Point> mapCoordinates = new HashSet<>();

    //Istanza del del database locale
    SaveScore game;

    //Istanza del thread principale del gioco
    GameMainThread thread = new GameMainThread();

    //Listener per restituire score, highscore, e punteggio resettato
    private OnEatListener onEatListener;
    private OnResetListener onResetListener;

    private GridPosition mSnakeHeader;                         //posizione della testa del serpente
    private GridPosition mFoodPosition;                        //posizione del cibo
    private static final int GRID_SIZE = 225;                   //cardinalità di mapCoordinates
    private int mSnakeLength = 3;                              //lunghezza del serpente
    private int mDifficulty;                                   //Difficoltà del gioco
    private int mSpeed = 8;                                    //velocità del serpente
    private int mSnakeDirection = GameType.RIGHT;              //direzione iniziale serpente
    private boolean mIsEndGame = false;                        //Il gioco finisce
    private int mGridSize = 15;                                //Taglia della griglia
    private Paint mGridPaint = new Paint();                    //colore paint
    private Paint mStrokePaint = new Paint();                  //spessore paint
    private int mRectSize = dp2px(getContext(), 20);    //Dimensione del quadrato
    private int mStartX, mStartY;                              //cordinate posizione iniziale serpente
    private int mPoint;                                        //Segna il punteggio attuale
    private int mHighScore;                                    //Segna l'highscore attuale

    //SoundPool costanti
    private SoundPool soundPool;
    private static final int NUMBER_OF_SIMULTANEOUS_SOUNDS = 5;
    private static final float LEFT_VOLUME_VALUE = 1.0f;
    private static final float RIGHT_VOLUME_VALUE = 1.0f;
    private static final int MUSIC_LOOP = 0;
    private static final int SOUND_PLAY_PRIORITY = 1;
    private static final float PLAY_RATE= 1.0f;
    private static int[] sm;

    /**
     * COSTRUTTORI
     */
    public SnakePanelView(Context context) {
        this(context, null);
    }

    public SnakePanelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        game = new SaveScore();
    }

    public SnakePanelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initSound();
    }

    /**
     * Disegna la mappa per la prima volta
     */
    public void init() {
        List<GridSquare> squares;
        for (int i = 0; i < mGridSize; i++) {
            //Inserisce una lista di quadrati in ogni posizione di squares
            squares = new ArrayList<>();
            for (int j = 0; j < mGridSize; j++) {
                squares.add(new GridSquare(GameType.GRID));
            }
            mGridSquare.add(squares);
        }
        //Posiziona la testa
        mSnakeHeader = new GridPosition(10, 10);
        //Aggiunge nelle posizioni del serpente le coordinate della testa
        mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
        //Imposta coordinate cibo iniziali
        mFoodPosition = new GridPosition(0, 0);
        mIsEndGame    = true;
        generateMapCoordinates();
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

        sm = new int[3];

        //inserisce i suoni
        sm[0] = soundPool.load(getContext(), R.raw.snake_eat, SOUND_PLAY_PRIORITY);
        sm[1] = soundPool.load(getContext(), R.raw.snake_loose3, SOUND_PLAY_PRIORITY);
    }

    /**
     * Riproduce i suoni
     *
     * @param sound Riceve un intero in base al tipo di audio che si vuole riprodurre
     */
    private void playSound(int sound) {
        soundPool.play(sm[sound],
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
        sm = null;
        soundPool.release();
        soundPool = null;
    }

    /**
     * Gestisce la grandezza della griglia in pixel
     *
     * @param w Larghezza corrente della view
     * @param h Altezza corrente della view
     * @param oldw Larghezza precedente della view
     * @param oldh Altezza precedente della view
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mStartX = w / 2 - mGridSize * mRectSize / 2;
        mStartY = dp2px(getContext(), 1);
    }

    /**
     * Misura la view e il suo contenuto per determinare larghezza e altezza
     *
     * @param widthMeasureSpec requisiti di spazio orizzontale imposti dalla view
     * @param heightMeasureSpec requisiti di spazio verticale imposti dalla view
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Modifico l'altezza personalizzandola
        int height = mStartY * 2 + mGridSize * mRectSize;
        //Se il metodo onMeasure è chiamato, la chiamata a setMeasuredDimension è obbligatoria
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), height);
    }

    /**
     * Disegna la grilia della mappa
     *
     * @param canvas Riceve in input un canvas inizializzato
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Pennello griglia
        mGridPaint.reset();
        mGridPaint.setStyle(Paint.Style.FILL);
        mGridPaint.setAntiAlias(true);

        //Pennello per le righe
        mStrokePaint.reset();
        mStrokePaint.setColor(Color.BLACK);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setAntiAlias(true);

        for (int i = 0; i < mGridSize; i++) {
            for (int j = 0; j < mGridSize; j++) {
                int left = mStartX + i * mRectSize;
                int top = mStartY + j * mRectSize;
                int right = left + mRectSize;
                int bottom;
                if(j != (mGridSize-1))
                    bottom = top + mRectSize;
                else
                    bottom = top + mRectSize - 5;

                canvas.drawRect(left, top, right, bottom, mStrokePaint);
                mGridPaint.setColor(mGridSquare.get(i).get(j).getColor());
                canvas.drawRect(left, top, right, bottom, mGridPaint);
            }
        }
    }

    /**
     * Imposta la posizione del cibo
     *
     * @param foodPosition Riceve le coordinate del cibo
     */
    private void refreshFood(GridPosition foodPosition) {
        mGridSquare.get(foodPosition.getX()).get(foodPosition.getY()).setType(GameType.FOOD);
    }

    /**
     * Imposta la velocità del serpente
     *
     * @param speed Un intero che indica la velocità del serpente
     */
    public void setSpeed(int speed) {
        mSpeed = speed;
    }

    /**
     * Imposta la difficoltà
     *
     * @param difficulty Un intero che indica la difficoltà
     */
    public void setDifficulty(int difficulty) {
        mDifficulty = difficulty;
    }

    /**
     * Imposta la dimensione della mappa
     *
     * @param gridSize Un intero che indica la grandezza della mappa
     */
    public void setGridSize(int gridSize) {
        mGridSize = gridSize;
    }

    /**
     * Imposta se il gioco è finito o meno
     * @param mIsEndGame se true, il gioco è temrinato altrimenti no
     */
    public void setmIsEndGame(boolean mIsEndGame) {
        this.mIsEndGame = mIsEndGame;
    }

    /**
     * Fuznione che gestisce la direzione del serpente.
     * Se la posizione ricevuta è opposta a quella attuale allora non fa niente.
     *
     * @param snakeDirection Intero che indica la direzione del serpente
     */
    public void setSnakeDirection(int snakeDirection) {
        if (mSnakeDirection == GameType.RIGHT && snakeDirection == GameType.LEFT) return;
        if (mSnakeDirection == GameType.LEFT && snakeDirection == GameType.RIGHT) return;
        if (mSnakeDirection == GameType.TOP && snakeDirection == GameType.BOTTOM) return;
        if (mSnakeDirection == GameType.BOTTOM && snakeDirection == GameType.TOP) return;
        mSnakeDirection = snakeDirection;
    }

    /**
     * Thread principale del gioco
     */
    public class GameMainThread extends Thread {

        @Override
        public void run() {
            while (!mIsEndGame) {
                moveSnake(mSnakeDirection);
                checkCollision();
                refreshGridSquare();
                handleSnakeTail();
                postInvalidate();     //Ridisegna l'interfaccia
                handleSpeed();
            }
        }

        /**
         * Gestisce la velocità di aggiornamento
         */
        private void handleSpeed() {
            try {
                sleep(1000 / mSpeed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Quando la testa si incontra con un altro elemento del corpo
     * si ha una collisione e si perde la partita.
     *
     * Gestisce anche le collisioni con il cibo chiamando la funzione
     * addPoint() e generateFood()
     */
    private void checkCollision() {
        //Ottiene la posizione della testa
        GridPosition headerPosition = mSnakePositions.get(mSnakePositions.size() - 1);
        int sizeSnakePositions = mSnakePositions.size() - 2;

        for (int i = 0; i < sizeSnakePositions; i++) {
            GridPosition position = mSnakePositions.get(i);
            if (headerPosition.getX() == position.getX() && headerPosition.getY() == position.getY()) {
                //Il serpente si è morso
                playSound(GameType.SNAKE_LOOSE);
                mIsEndGame = true;
                if(mPoint > mHighScore) {
                    mHighScore = mPoint;
                    game.save(App.SNAKE, mPoint, getContext());
                }
                showMessageDialog();
                return;
            }
        }

        //Rileva collisioni cibo
        if (headerPosition.getX() == mFoodPosition.getX()
                && headerPosition.getY() == mFoodPosition.getY()) {   //Se la posizione della testa
            mSnakeLength++;                                           //è uguale a quella del cibo
            playSound(GameType.SNAKE_EAT);
            generateFood();
            addPoint();                                               //aumenta la lunghezza e
        }                                                             //genera il nuovo cibo
    }

    /**
     * Aggiunge un punto allo score e se si super l'Highscore
     * lo si carica sul DB
     */
    private void addPoint() {
        switch (mDifficulty) {
            case GameType.EASY:
                mPoint += 1;
                break;
            case GameType.MEDIUM:
                    mPoint += 2;
                    break;
            case GameType.HARD:
                mPoint += 3;
                break;
        }

        if(onEatListener != null)
            onEatListener.onEat(mPoint, mHighScore);
    }

    /**
     * Mostra il dialog quando si perde la partita per scegliere se ricominciarla
     * o terminarla
     */
    private void showMessageDialog() {
        post(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(getContext()).setTitle(getResources().getString(R.string.gameOver))
                        .setCancelable(false)
                        .setMessage(getResources().getString(R.string.your_score_is) + " " + mPoint)
                        .setPositiveButton(getResources().getString(R.string.again), (dialog, which) -> {
                            //Resetta i punti
                            dialog.dismiss();
                            mPoint = 0;
                            if(onResetListener != null)
                                onResetListener.onReset(mPoint);
                            startGame(GameType.GENERIC_DIFFICULTY, false);
                        })
                        .setNegativeButton(getResources().getString(R.string.exit), (dialog, which) -> {
                            dialog.dismiss();
                            cleanUpIfEnd();
                            if(getContext() instanceof MainActivitySnake)
                                ((MainActivitySnake) getContext()).finish();
                        })
                        .create()
                        .show();
            }
        });
    }

    /**
     * Funzione che gestisce l'avvio della partita
     *
     * @param difficulty Un intero passato per comunicare la difficoltà
     * @param isRestart Indica se la partita deve essere riavviata o è il primo avvio
     */
    public void startGame(int difficulty, boolean isRestart) {
        //In base alla difficoltà, cambia la velocità del serpente
        if(!isRestart) {
            switch (difficulty) {
                case GameType.EASY:
                    setDifficulty(GameType.EASY);
                    setSpeed(4);
                    break;
                case GameType.MEDIUM:
                    setDifficulty(GameType.MEDIUM);
                    setSpeed(8);
                    break;
                case GameType.HARD:
                    setDifficulty(GameType.HARD);
                    setSpeed(12);
                    break;
            }
        }

        //Legge dal database se esistono salvataggi del gioco
        if(App.scoreboardDao.getGame(App.SNAKE) != null)
            mHighScore = App.scoreboardDao.getScore(App.SNAKE);  //se si, l'highscore viene aggiornato
        else
            mHighScore = 0;

        //if (!mIsEndGame) return;
        for (List<GridSquare> squares : mGridSquare) {
            for (GridSquare square : squares)
                square.setType(GameType.GRID);
        }

        if (mSnakeHeader != null) {
            mSnakeHeader.setX(10);
            mSnakeHeader.setY(10);
        } else
            mSnakeHeader = new GridPosition(10, 10);    //The initial position of the snake

        //Svuote le posizioni del serpente
        mSnakePositions.clear();
        //Aggiunge la testa nelle posizioni del serpente
        mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
        mSnakeLength    = 3;                          //Lunghezza serpente
        mSnakeDirection = GameType.RIGHT;

        //Genera il cibo
        if (mFoodPosition != null)
            generateFood();

        mIsEndGame = false;
        if(!isRestart && difficulty == GameType.GENERIC_DIFFICULTY) {
            mPoint = 0;
            if(onResetListener != null)
                onResetListener.onReset(mPoint);
            thread = new GameMainThread();
            thread.start();
        } else if(!isRestart && thread.isAlive()) {
            mPoint = 0;
            if(onResetListener != null)
                onResetListener.onReset(mPoint);
        }else if(!isRestart){
            mPoint = 0;
            if(onResetListener != null)
                onResetListener.onReset(mPoint);
            thread.start();
        }else {
            mPoint = 0;
            if(onResetListener != null)
                onResetListener.onReset(mPoint);
        }
    }

    /**
     * Genera il cibo
     */
    private void generateFood() {

        //Una lista contenente le eventuali celle da eliminare da mapCoordinates
        List<Point> cellsToDelete = new ArrayList<>();


        for(GridPosition gridPosition : mSnakePositions) {
            for (Point point : mapCoordinates) {
                /*Se la coordinata di mapCoordinates coincide con la coordinata presente in mSnakePosition
                  questa viene aggiunta in cellsToDelete*/
                if (point.x == gridPosition.getX() && point.y == gridPosition.getY())
                    cellsToDelete.add(point);
            }
        }

        //Rimuove le celle trovate precedentemente da mapCoordinates
        for(Point cell : cellsToDelete)
            mapCoordinates.remove(cell);

        Random random = new Random();

        //Genera un numero casuale da 0 alla taglia di mapCoordinates
        int cellNumber = random.nextInt(mapCoordinates.size());
        int iterator = 0;
        Point randomCell = new Point(0, 0);

        for(Point point : mapCoordinates) {
            //Se l'iteratore ha raggiunto il numero generato precedentemente
            if(iterator == cellNumber) {
                //la randomCell si riempie con la posizione di mapCoordinates
                randomCell = point;
                break;
            }

            iterator++;
        }

        //Posizione del cibo sicura per il serpente
        int foodX = randomCell.x;
        int foodY = randomCell.y;

        mFoodPosition.setX(foodX);
        mFoodPosition.setY(foodY);
        refreshFood(mFoodPosition);
        generateMapCoordinates();
    }

    /**
     * Genera ogni volta che il cibo viene mangiato una nuova griglia con tutte le
     * coordinate
     */
    private void generateMapCoordinates() {
        int x = 0;
        int y = 0;

        for(int i = 0; i < GRID_SIZE; i++) {
            Point p = new Point(x, y);
            mapCoordinates.add(p);
            x++;
            if(x == 15) {
                y++;
                x = 0;
            }
        }
    }

    /**
     * Controlla la posizione della testa del serpente
     * Se la testa del serpente raggiunge il bordo della mappa allora dovrà spostarsi
     * dalla parte opposta della mappa.
     * inoltre memorizza la posizione della tasta nelle posizioni del corpo
     *
     * @param snakeDirection Un intero che indica la direzione del serpente
     */
    private void moveSnake(int snakeDirection) {
        switch (snakeDirection) {
            case GameType.LEFT:
                //se raggiunge l'estrema sinistra, attraversa lo schermo all'estrema destra
                if (mSnakeHeader.getX() - 1 < 0) {
                    mSnakeHeader.setX(mGridSize - 1);
                } else {
                    mSnakeHeader.setX(mSnakeHeader.getX() - 1);
                }
                mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
                break;
            case GameType.TOP:
                //se raggiunge l'estremo superiore, attraversa lo schermo dal basso
                if (mSnakeHeader.getY() - 1 < 0) {
                    mSnakeHeader.setY(mGridSize - 1);
                } else {
                    mSnakeHeader.setY(mSnakeHeader.getY() - 1);
                }
                mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
                break;
            //se raggiunge l'estrema destra, attraversa lo schermo all'estrema sinistra
            case GameType.RIGHT:
                if (mSnakeHeader.getX() + 1 >= mGridSize) {
                    mSnakeHeader.setX(0);
                } else {
                    mSnakeHeader.setX(mSnakeHeader.getX() + 1);
                }
                mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
                break;
            //se raggiunge l'estrema sinistra, attraversa lo schermo all'estrema destra
            case GameType.BOTTOM:
                if (mSnakeHeader.getY() + 1 >= mGridSize) {
                    mSnakeHeader.setY(0);
                } else {
                    mSnakeHeader.setY(mSnakeHeader.getY() + 1);
                }
                mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
                break;
        }
    }

    /**
     * controlla tutta la griglia della mappa e aggiorna le posizioni del serpente
     */
    private void refreshGridSquare() {
        for (GridPosition position : mSnakePositions)
            mGridSquare.get(position.getX()).get(position.getY()).setType(GameType.SNAKE);
    }

    /**
     * Gestisce la coda del serpente.
     *
     * Imposta ogni quadrato della mappa in tipo mappa se il serpente non è su di essi.
     */
    private void handleSnakeTail() {
        int snakeLength = mSnakeLength;
        int sizeSnakePositions = mSnakePositions.size() - 1;

        for (int i = sizeSnakePositions; i >= 0; i--) {
            if (snakeLength > 0) {
                snakeLength--;
            } else {
                GridPosition position = mSnakePositions.get(i);
                mGridSquare.get(position.getX()).get(position.getY()).setType(GameType.GRID);
            }
        }
        snakeLength = mSnakeLength;
        for (int i = sizeSnakePositions; i >= 0; i--) {
            if (snakeLength > 0) {
                snakeLength--;
            } else {
                mSnakePositions.remove(i);
            }
        }
    }

    /**
     * Converte la dimensione dei dp in pixels
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                context.getResources().getDisplayMetrics());
    }




    /**
     * Imposta il listener per quando il serpente mangia
     *
     * @param onEatListener
     */
    public void setOnEatListener(OnEatListener onEatListener) {
        this.onEatListener = onEatListener;
    }

    /**
     * Imposta il listener per quando il serpente mangia
     *
     * @param onResetListener
     */
    public void setOnResetListener(OnResetListener onResetListener) {
        this.onResetListener = onResetListener;
    }

    public interface OnEatListener {
        void onEat(int point, int highScore);
    }

    public interface OnResetListener {
        void onReset(int point);
    }
}
