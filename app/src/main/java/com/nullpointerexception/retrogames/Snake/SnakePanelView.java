package com.nullpointerexception.retrogames.Snake;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.Components.SaveScore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SnakePanelView extends View {
    private final static String TAG = SnakePanelView.class.getSimpleName();

    //È una lista di GridSquare usata per creare la mappa
    private List<List<GridSquare>> mGridSquare = new ArrayList<>();
    //Lista contenente le posizioni del serpente
    private List<GridPosition> mSnakePositions = new ArrayList<>();

    SaveScore game;

    private OnEatListener onEatListener;
    private GridPosition mSnakeHeader;                         //posizione della testa del serpente
    private GridPosition mFoodPosition;                        //posizione del cibo
    private int mSnakeLength = 3;                              //lunghezza del serpente
    private int mDifficulty;
    private int mSpeed = 8;                                    //velocità del serpente
    private int mSnakeDirection = GameType.RIGHT;              //direzione iniziale serpente
    private boolean mIsEndGame = false;                        //Il gioco finisce
    private int mGridSize = 20;                                //Taglia della griglia
    private Paint mGridPaint = new Paint();                    //colore paint
    private Paint mStrokePaint = new Paint();                  //spessore paint
    private int mRectSize = dp2px(getContext(), 15);    //Dimensione del quadrato
    private int mStartX, mStartY;                              //cordinate posizione iniziale serpente
    private int mPoint;
    private int mHighScore;


    public SnakePanelView(Context context) {
        this(context, null);
    }

    public SnakePanelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        game= new SaveScore();
    }

    public SnakePanelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
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
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mStartX = w / 2 - mGridSize * mRectSize / 2;
        mStartY = dp2px(getContext(), 40);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = mStartY * 2 + mGridSize * mRectSize;
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        //Pennello griglia
        mGridPaint.reset();
        mGridPaint.setStyle(Paint.Style.FILL);
        mGridPaint.setAntiAlias(true);

        mStrokePaint.reset();
        mStrokePaint.setColor(Color.BLACK);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setAntiAlias(true);

        for (int i = 0; i < mGridSize; i++) {
            for (int j = 0; j < mGridSize; j++) {
                int left = mStartX + i * mRectSize;
                int top = mStartY + j * mRectSize;
                int right = left + mRectSize;
                int bottom = top + mRectSize;
                canvas.drawRect(left, top, right, bottom, mStrokePaint);
                mGridPaint.setColor(mGridSquare.get(i).get(j).getColor());
                canvas.drawRect(left, top, right, bottom, mGridPaint);
            }
        }
    }

    private void refreshFood(GridPosition foodPosition) {
        mGridSquare.get(foodPosition.getX()).get(foodPosition.getY()).setType(GameType.FOOD);
    }

    public void setSpeed(int speed) {
        mSpeed = speed;
    }

    public void setDifficulty(int difficulty) {
        mDifficulty = difficulty;
    }

    public void setGridSize(int gridSize) {
        mGridSize = gridSize;
    }

    public void setSnakeDirection(int snakeDirection) {
        if (mSnakeDirection == GameType.RIGHT && snakeDirection == GameType.LEFT) return;
        if (mSnakeDirection == GameType.LEFT && snakeDirection == GameType.RIGHT) return;
        if (mSnakeDirection == GameType.TOP && snakeDirection == GameType.BOTTOM) return;
        if (mSnakeDirection == GameType.BOTTOM && snakeDirection == GameType.TOP) return;
        mSnakeDirection = snakeDirection;
    }

    private class GameMainThread extends Thread {

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

        //Gestisce la velocità di aggiornamento
        private void handleSpeed() {
            try {
                sleep(1000 / mSpeed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //Rileva collisione
    private void checkCollision() {
        //Ottiene la posizione della testa
        GridPosition headerPosition = mSnakePositions.get(mSnakePositions.size() - 1);

        for (int i = 0; i < mSnakePositions.size() - 2; i++) {
            GridPosition position = mSnakePositions.get(i);
            if (headerPosition.getX() == position.getX() && headerPosition.getY() == position.getY()) {
                //Il serpente si è morso
                mIsEndGame = true;
                showMessageDialog();
                return;
            }
        }

        //Rileva collisioni cibo
        if (headerPosition.getX() == mFoodPosition.getX()
                && headerPosition.getY() == mFoodPosition.getY()) {   //Se la posizione della testa
            mSnakeLength++;                                           //è uguale a quella del cibo
            generateFood();
            addPoint();                                               //aumenta la lunghezza e
        }                                                             //genera il nuovo cibo
    }

    //Aggiungere lo score
    private void addPoint() {
        mPoint++;
        if(mPoint > mHighScore) {
            mHighScore = mPoint;
            game.save(App.SNAKE, mPoint, getContext());
        }
        if(onEatListener != null) {
            onEatListener.onEat(mPoint, mHighScore);
        }
    }

    private void showMessageDialog() {
        post(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(getContext()).setMessage("Game " + "Over!")
                        .setCancelable(false)
                        .setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                reStartGame(mSpeed);
                            }
                        })
                        .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }

    public void reStartGame(int difficult) {

        switch (difficult) {
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

        if(App.scoreboardDao.getGame(App.SNAKE) != null)
            mHighScore = App.scoreboardDao.getScore(App.SNAKE);
        else
            mHighScore = 0;

        if (!mIsEndGame) return;
        for (List<GridSquare> squares : mGridSquare) {
            for (GridSquare square : squares) {
                square.setType(GameType.GRID);
            }
        }
        if (mSnakeHeader != null) {
            mSnakeHeader.setX(10);
            mSnakeHeader.setY(10);
        } else {
            mSnakeHeader = new GridPosition(10, 10);    //The initial position of the snake
        }
        mSnakePositions.clear();
        mSnakePositions.add(new GridPosition(mSnakeHeader.getX(), mSnakeHeader.getY()));
        mSnakeLength    = 3;                          //Lunghezza serpente
        mSnakeDirection = GameType.RIGHT;

        if (mFoodPosition != null) {
            mFoodPosition.setX(0);
            mFoodPosition.setY(0);
        } else {
            mFoodPosition = new GridPosition(0, 0);
        }
        refreshFood(mFoodPosition);
        mIsEndGame = false;
        GameMainThread thread = new GameMainThread();
        thread.start();
    }

    //Genera il nuovo cibo
    private void generateFood() {
        Random random = new Random();
        int foodX = random.nextInt(mGridSize - 1);
        int foodY = random.nextInt(mGridSize - 1);
        for (int i = 0; i < mSnakePositions.size() - 1; i++) {
            //Se il cibo si genera sulla posizione del corpo, lo rigenera
            if (foodX == mSnakePositions.get(i).getX() && foodY == mSnakePositions.get(i).getY()) {

                foodX = random.nextInt(mGridSize - 1);
                foodY = random.nextInt(mGridSize - 1);
                //Resetta il contatore
                i = 0;
            }
        }
        mFoodPosition.setX(foodX);
        mFoodPosition.setY(foodY);
        refreshFood(mFoodPosition);
    }

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

    private void refreshGridSquare() {
        for (GridPosition position : mSnakePositions) {
            mGridSquare.get(position.getX()).get(position.getY()).setType(GameType.SNAKE);
        }
    }

    //Gestisce la coda del serpente
    private void handleSnakeTail() {
        int snakeLength = mSnakeLength;
        for (int i = mSnakePositions.size() - 1; i >= 0; i--) {
            if (snakeLength > 0) {
                snakeLength--;
            } else {
                GridPosition position = mSnakePositions.get(i);
                mGridSquare.get(position.getX()).get(position.getY()).setType(GameType.GRID);
            }
        }
        snakeLength = mSnakeLength;
        for (int i = mSnakePositions.size() - 1; i >= 0; i--) {
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

    public void setOnEatListener(OnEatListener onEatListener) {
        this.onEatListener = onEatListener;
    }

    public interface OnEatListener {
        void onEat(int point, int highScore);
    }
}
