package com.nullpointerexception.retrogames.Pong;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.Components.SaveScore;
import com.nullpointerexception.retrogames.R;

import java.util.Random;

/**
 * Handle animation, game logic and user input.
 */
public class PongThread extends Thread {

    public Bundle universal_map;

    public static final int STATE_PAUSE   = 0;
    public static final int STATE_READY   = 1;
    public static final int STATE_RUNNING = 2;
    public static final int STATE_LOSE    = 3;
    public static final int STATE_WIN     = 4;

    private static final int    PHYS_BALL_SPEED       = 15;
    private static final int    PHYS_PADDLE_SPEED     = 8;
    private static final int    PHYS_FPS              = 60;
    //todo: Eventualmente modificare l'angolo del rimbalzo per una migliore giocabilità
    private static final double PHYS_MAX_BOUNCE_ANGLE = 5 * Math.PI / 12; //75 degrees in radians
    private static final int    PHYS_COLLISION_FRAMES = 5;

    private static final String KEY_HUMAN_PLAYER_DATA    = "humanPlayer";
    private static final String KEY_COMPUTER_PLAYER_DATA = "computerPlayer";
    private static final String KEY_BALL_DATA            = "ball";
    private static final String KEY_GAME_STATE           = "state";

    private static final String TAG = "PongThread";

    private final SurfaceHolder mSurfaceHolder;
    private AlertDialog mDlgMsg = null;
    private int highscore_point = 0;

    private final Handler mStatusHandler;

    private final Handler mScoreHandler;

    private final Context mContext;

    private boolean mRun;
    private final Object  mRunLock;

    private int mState;

    private Player mHumanPlayer;
    private Player mComputerPlayer;
    private Ball   mBall;

    private Paint mMedianLinePaint;

    private Paint mCanvasBoundsPaint;
    private int   mCanvasHeight;
    private int   mCanvasWidth;

    private onEndGameListener onEndGameListener;

    /**
     * usato per permettere al computer di "dimenticarsi" di muovere il canvas in modo tale da
     * avere la senzazione di star giocando contro un essere umano.
     */
    private Random mRandomGen;

    /**
     * la probabilità che il computer muovi il canvas
     */
    private float mComputerMoveProbability;


    PongThread(final SurfaceHolder surfaceHolder,
               final Context context,
               final Handler statusHandler,
               final Handler scoreHandler,
               final AttributeSet attributeSet) {
        mSurfaceHolder = surfaceHolder;
        mStatusHandler = statusHandler;
        mScoreHandler = scoreHandler;
        mContext = context;

        mRun = false;
        mRunLock = new Object();

        update_Highscore();

        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.PongView);

        int paddleHeight = a.getInt(R.styleable.PongView_paddleHeight, 200);
        int paddleWidth = a.getInt(R.styleable.PongView_paddleWidth, 35);
        int ballRadius = a.getInt(R.styleable.PongView_ballRadius, 32);

        a.recycle();

        Paint humanPlayerPaint = new Paint();
        humanPlayerPaint.setAntiAlias(true);
        humanPlayerPaint.setColor(Color.BLUE);

        mHumanPlayer = new Player(paddleWidth, paddleHeight, humanPlayerPaint);

        Paint computerPlayerPaint = new Paint();
        computerPlayerPaint.setAntiAlias(true);
        computerPlayerPaint.setColor(Color.RED);

        mComputerPlayer = new Player(paddleWidth, paddleHeight, computerPlayerPaint);

        Paint ballPaint = new Paint();
        ballPaint.setAntiAlias(true);
        ballPaint.setColor(Color.GREEN);

        mBall = new Ball(ballRadius, ballPaint);

        mMedianLinePaint = new Paint();
        mMedianLinePaint.setAntiAlias(true);
        mMedianLinePaint.setColor(Color.YELLOW);
        mMedianLinePaint.setAlpha(80);
        mMedianLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mMedianLinePaint.setStrokeWidth(2.0f);
        mMedianLinePaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));

        mCanvasBoundsPaint = new Paint();
        mCanvasBoundsPaint.setAntiAlias(true);
        mCanvasBoundsPaint.setColor(Color.YELLOW);
        mCanvasBoundsPaint.setStyle(Paint.Style.STROKE);
        mCanvasBoundsPaint.setStrokeWidth(1.0f);

        mCanvasHeight = 1;
        mCanvasWidth = 1;

        mRandomGen = new Random();
        mComputerMoveProbability = 0.6f;
    }

    /**
     * The game loop.
     */
    @Override
    public void run() {
        long mNextGameTick = SystemClock.uptimeMillis();
        int skipTicks = 1000 / PHYS_FPS;
        while (mRun) {
            Canvas c = null;
            try {
                c = mSurfaceHolder.lockCanvas(null);
                if (c != null) {
                    synchronized (mSurfaceHolder) {
                        if (mState == STATE_RUNNING) {
                            updatePhysics();
                        }
                        synchronized (mRunLock) {
                            if (mRun) {
                                updateDisplay(c);
                            }
                        }
                    }
                }
            } finally {
                if (c != null) {
                    mSurfaceHolder.unlockCanvasAndPost(c);
                }
            }
            mNextGameTick += skipTicks;
            long sleepTime = mNextGameTick - SystemClock.uptimeMillis();
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Interrupted", e);
                }
            }
        }
    }

    void setRunning(boolean running) {
        synchronized (mRunLock) {
            mRun = running;
        }
    }

    /**
     * salva lo stato del gioco mettendo in una
     * mappa (map) le informazioni riguardanti la posizione
     * di ogni oggetto sul layout (palla,giocatore,cpu)
     * @param map
     */
    void saveState(Bundle map) {
        synchronized (mSurfaceHolder) {
            map.putFloatArray(KEY_HUMAN_PLAYER_DATA,
                              new float[]{mHumanPlayer.bounds.left,
                                          mHumanPlayer.bounds.top,
                                          mHumanPlayer.score});

            map.putFloatArray(KEY_COMPUTER_PLAYER_DATA,
                              new float[]{mComputerPlayer.bounds.left,
                                          mComputerPlayer.bounds.top,
                                          mComputerPlayer.score});

            map.putFloatArray(KEY_BALL_DATA,
                              new float[]{mBall.cx, mBall.cy, mBall.dx, mBall.dy});


            map.putInt(KEY_GAME_STATE, mState);
        }
    }

    /**
     * ripristina lo stato del gioco mettendo in una
     * mappa (universal_map = map) le informazioni riguardanti la posizione
     * di ogni oggetto sul layout (palla,giocatore,cpu)
     * universal_map servirà al metodo SetupNewGame
     * @param map
     */
    void restoreState(Bundle map) {
        synchronized (mSurfaceHolder) {
            float[] humanPlayerData = map.getFloatArray(KEY_HUMAN_PLAYER_DATA);
            mHumanPlayer.score = (int) humanPlayerData[2];
            movePlayer(mHumanPlayer, humanPlayerData[0], humanPlayerData[1]);

            float[] computerPlayerData = map.getFloatArray(KEY_COMPUTER_PLAYER_DATA);
            mComputerPlayer.score = (int) computerPlayerData[2];
            movePlayer(mComputerPlayer, computerPlayerData[0], computerPlayerData[1]);

            float[] ballData = map.getFloatArray(KEY_BALL_DATA);
            mBall.cx = ballData[0];
            mBall.cy = ballData[1];
            mBall.dx = ballData[2];
            mBall.dy = ballData[3];


            int state = map.getInt(KEY_GAME_STATE);
            universal_map = map;
            setState(state);
        }
    }

    /**
     *setta lo stato del gioco
     * Ready: fa partire setupNewRound
     * Running: fa partire il gioco e nasconde il testo al centro
     * Win: assegna un punto al giocatore e stampa a video la scritta "good work"
     * Lose: assegna un punto alla cpu e stampa a video la scritta "sorry"
     *       se lo score della cpu è uguale a 5 la partita finisce
     * Pause: salva tutto lo stato della partita e la mette in pausa stampando la scritta "paused"
     * @param mode
     */
    void setState(int mode) {
        synchronized (mSurfaceHolder) {
            mState = mode;
            Resources res = mContext.getResources();
            switch (mState) {
                case STATE_READY:
                    setupNewRound();
                    break;
                case STATE_RUNNING:
                    hideStatusText();
                    break;
                case STATE_WIN:
                    setStatusText(res.getString(R.string.mode_win));
                    mHumanPlayer.score++;
                    setupNewRound();
                    break;
                case STATE_LOSE:
                    setStatusText(res.getString(R.string.mode_lose));
                    mComputerPlayer.score++;
                    if (mComputerPlayer.score == 1)
                    {
                        game_over(mComputerPlayer.score,mHumanPlayer.score);
                        startNewGame();

                    }
                    setupNewRound();
                    break;
                case STATE_PAUSE:
                    setStatusText(res.getString(R.string.mode_pause));
                    break;
            }
        }
    }

    /**
     * aggiorna l'highscore
     */
    private void update_Highscore()
    {
        if (App.scoreboardDao.getScore(App.PONG) != highscore_point) {
            highscore_point = App.scoreboardDao.getScore(App.PONG);
        }
    }

    public void game_over(int cpuScore, int humanScore){

        int score_pong = humanScore - cpuScore;

        if (score_pong < 0)
        {
            score_pong = 0;
            //fine 1
            //showDialog_GameOver(score_pong);
        }

        if (highscore_point < score_pong)
        {
            highscore_point = score_pong;

            SaveScore pong = new SaveScore();
            pong.save(App.PONG, highscore_point,mContext);

            //fine 2
            //showDialog_GameOver(score_pong);
        }
        else {
            //fine 3
            //showDialog_GameOver(score_pong);
        }



        if(onEndGameListener != null)
            onEndGameListener.onEnd(score_pong);


    }

    /**
     * Mostra il dialog di GameOver
     */
    private void showDialog_GameOver(int score) {
        mDlgMsg = new AlertDialog.Builder(mContext)
                .setTitle(mContext.getResources().getString(R.string.gameOver))
                .setMessage(mContext.getResources().getString(R.string.your_score_is) + ": " + score)
                .setPositiveButton(mContext.getResources().getString(R.string.again), (dialog, which) -> {
                    mDlgMsg.dismiss();
                })
                .setNegativeButton(mContext.getResources().getString(R.string.exit), (dialog, which) -> {
                    mDlgMsg.dismiss();
                    if(mContext instanceof MainActivityPong)
                        ((MainActivityPong) mContext).finish();
                })
                .show();
    }

    /**
    *il metodo pause in pongthread viene chiamato
    *solo se si è effettivamente
    *in game ovvero la palla è in movimento
    *
    *bug (risolto): quando si vince o si perde
    *e si va in pausa il punteggio dell'utlimo giocatore
    *che ha fatto un punto viene incrementato di uno
    *
     */
    void pause() {
        synchronized (mSurfaceHolder) {
            if (mState != STATE_PAUSE) {
                setState(STATE_PAUSE);
            }
        }
    }


    void unPause() {
        synchronized (mSurfaceHolder) {
            setState(STATE_RUNNING);
        }
    }

    void startNewGame() {
        synchronized (mSurfaceHolder) {
            mHumanPlayer.score = 0;
            mComputerPlayer.score = 0;
            setupNewRound();
            setState(STATE_PAUSE);
        }
    }


    /**
     * @return vero se lo stato del gioco è diverso da Running.
     */
    boolean isBetweenRounds() {
        return mState != STATE_RUNNING;
    }

    /**
     * @param event percepisce l'evento di movimento
     * @return la nuova posizione del player
     */
    boolean isTouchOnHumanPaddle(MotionEvent event) {
        return mHumanPlayer.bounds.contains(event.getX(), event.getY());
    }

    /**
     * sincronizza la posizione del giocatore
     * sul layout
     * @param dy
     */
    void moveHumanPaddle(float dy) {
        synchronized (mSurfaceHolder) {
            movePlayer(mHumanPlayer,
                       mHumanPlayer.bounds.left,
                       mHumanPlayer.bounds.top + dy);
        }
    }

    /**
     * setta la superficie del layout
     * @param width
     * @param height
     */
    void setSurfaceSize(int width, int height) {
        synchronized (mSurfaceHolder) {
            mCanvasWidth = width;
            mCanvasHeight = height;
            setupNewRound();
        }
    }

    /**
     * aggiorna la posizione dei canvas, controlla le collisioni e le
     * condizioni di vittoria o sconfitta
     */
    private void updatePhysics() {

        if (mHumanPlayer.collision > 0) {
            mHumanPlayer.collision--;
        }
        if (mComputerPlayer.collision > 0) {
            mComputerPlayer.collision--;
        }

        if (collision(mHumanPlayer, mBall)) {
            handleCollision(mHumanPlayer, mBall);
            mHumanPlayer.collision = PHYS_COLLISION_FRAMES;
        } else if (collision(mComputerPlayer, mBall)) {
            handleCollision(mComputerPlayer, mBall);
            mComputerPlayer.collision = PHYS_COLLISION_FRAMES;
        } else if (ballCollidedWithTopOrBottomWall()) {
            mBall.dy = -mBall.dy;
        } else if (ballCollidedWithRightWall()) {
            setState(STATE_WIN);    // human plays on left
            return;
        } else if (ballCollidedWithLeftWall()) {
            setState(STATE_LOSE);
            return;
        }

        if (mRandomGen.nextFloat() < mComputerMoveProbability) {
            doAI();
        }

        moveBall();
    }

    /**
     * muove la palla
     */
    private void moveBall() {
        mBall.cx += mBall.dx;
        mBall.cy += mBall.dy;

        if (mBall.cy < mBall.radius) {
            mBall.cy = mBall.radius;
        } else if (mBall.cy + mBall.radius >= mCanvasHeight) {
            mBall.cy = mCanvasHeight - mBall.radius - 1;
        }
    }

    /**
     * muove la cpu per colpire la palla
     */
    private void doAI() {
        if (mComputerPlayer.bounds.top > mBall.cy) {
            // move up
            movePlayer(mComputerPlayer,
                       mComputerPlayer.bounds.left,
                       mComputerPlayer.bounds.top - PHYS_PADDLE_SPEED);
        } else if (mComputerPlayer.bounds.top + mComputerPlayer.paddleHeight < mBall.cy) {
            // move down
            movePlayer(mComputerPlayer,
                       mComputerPlayer.bounds.left,
                       mComputerPlayer.bounds.top + PHYS_PADDLE_SPEED);
        }
    }

    /**
     *
     * @return vero se la palla collide con il muro di sinistra
     */
    private boolean ballCollidedWithLeftWall() {
        return mBall.cx <= mBall.radius;
    }

    /**
     *
     * @return vero se la palla collide con il muro di destra
     */
    private boolean ballCollidedWithRightWall() {
        return mBall.cx + mBall.radius >= mCanvasWidth - 1;
    }

    /**
     *
     * @return vero se la palla collide con il muro di sopra o sotto
     */
    private boolean ballCollidedWithTopOrBottomWall() {
        return mBall.cy <= mBall.radius
               || mBall.cy + mBall.radius >= mCanvasHeight - 1;
    }

    /**
     * disegna i canvas dello score, della palla, del giocatore e della cpu
     */
    private void updateDisplay(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        canvas.drawRect(5, 5, mCanvasWidth, mCanvasHeight - 5 , mCanvasBoundsPaint);

        final int middle = mCanvasWidth / 2;
        canvas.drawLine(middle, 5, middle, mCanvasHeight - 5, mMedianLinePaint);

        setScoreText(mHumanPlayer.score + "    " + mComputerPlayer.score);

        handleHit(mHumanPlayer);
        handleHit(mComputerPlayer);

        canvas.drawRoundRect(mHumanPlayer.bounds, 5, 5, mHumanPlayer.paint);
        canvas.drawRoundRect(mComputerPlayer.bounds, 5, 5, mComputerPlayer.paint);
        canvas.drawCircle(mBall.cx, mBall.cy, mBall.radius, mBall.paint);
    }

    /**
     * disegna un'ombra quando i canvas
     * del giocatore o della cpu colpiscono
     * la palla
     * @param player
     */
    private void handleHit(Player player) {
        if (player.collision > 0) {
            player.paint.setShadowLayer(player.paddleWidth / 2, 0, 0, player.paint.getColor());
        } else {
            player.paint.setShadowLayer(0, 0, 0, 0);
        }
    }

    /**
     * setta una nuova partita o ripristina l'ultima messa in pausa
     */
    private void setupNewRound() {

        if(universal_map == null) {
            mBall.cx = mCanvasWidth / 2;
            mBall.cy = mCanvasHeight / 2;
            mBall.dx = -PHYS_BALL_SPEED;
            mBall.dy = 0;

            movePlayer(mHumanPlayer,
                    2,
                    (mCanvasHeight - mHumanPlayer.paddleHeight) / 2);

            movePlayer(mComputerPlayer,
                    mCanvasWidth - mComputerPlayer.paddleWidth - 2,
                    (mCanvasHeight - mComputerPlayer.paddleHeight) / 2);

        }
        else {
            float[] humanPlayerData = universal_map.getFloatArray(KEY_HUMAN_PLAYER_DATA);
            mHumanPlayer.score = (int) humanPlayerData[2];
            movePlayer(mHumanPlayer, humanPlayerData[0], humanPlayerData[1]);

            float[] computerPlayerData = universal_map.getFloatArray(KEY_COMPUTER_PLAYER_DATA);
            mComputerPlayer.score = (int) computerPlayerData[2];
            movePlayer(mComputerPlayer, computerPlayerData[0], computerPlayerData[1]);

            float[] ballData = universal_map.getFloatArray(KEY_BALL_DATA);
            mBall.cx = ballData[0];
            mBall.cy = ballData[1];
            mBall.dx = ballData[2];
            mBall.dy = ballData[3];
        }

        universal_map = null;
    }

    /**
     * a seconda del parametro passato
     * setta il testo
     * @param text
     */
    private void setStatusText(String text) {
        Message msg = mStatusHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("text", text);
        b.putInt("vis", View.VISIBLE);
        msg.setData(b);
        mStatusHandler.sendMessage(msg);
    }

    /**
     * nasconde il testo
     */
    private void hideStatusText() {
        Message msg = mStatusHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putInt("vis", View.INVISIBLE);
        msg.setData(b);
        mStatusHandler.sendMessage(msg);
    }

    /**
     * scrive lo score
     * @param text
     */
    private void setScoreText(String text) {
        Message msg = mScoreHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("text", text);
        msg.setData(b);
        mScoreHandler.sendMessage(msg);

    }

    private void movePlayer(Player player, float left, float top) {
        if (left < 2) {
            left = 2;
        } else if (left + player.paddleWidth >= mCanvasWidth - 2) {
            left = mCanvasWidth - player.paddleWidth - 2;
        }
        if (top < 0) {
            top = 0;
        } else if (top + player.paddleHeight >= mCanvasHeight) {
            top = mCanvasHeight - player.paddleHeight - 1;
        }
        player.bounds.offsetTo(left, top);
    }

    /**
     * controlla le collisioni
     * @param player
     * @param ball
     * @return vero se c'è una collisione
     */
    private boolean collision(Player player, Ball ball) {
        return player.bounds.intersects(
                ball.cx - mBall.radius,
                ball.cy - mBall.radius,
                ball.cx + mBall.radius,
                ball.cy + mBall.radius);
    }

    /**
     * computa la direzione della palla dopo una collisione con il giocatore
     */
    private void handleCollision(Player player, Ball ball) {
        float relativeIntersectY = player.bounds.top + player.paddleHeight / 2 - ball.cy;
        float normalizedRelativeIntersectY = relativeIntersectY / (player.paddleHeight / 2);
        double bounceAngle = normalizedRelativeIntersectY * PHYS_MAX_BOUNCE_ANGLE;

        ball.dx = (float) (-Math.signum(ball.dx) * PHYS_BALL_SPEED * Math.cos(bounceAngle));
        ball.dy = (float) (PHYS_BALL_SPEED * -Math.sin(bounceAngle));

        if (player == mHumanPlayer) {
            mBall.cx = mHumanPlayer.bounds.right + mBall.radius;
        } else {
            mBall.cx = mComputerPlayer.bounds.left - mBall.radius;
        }
    }

    public void setOnEndGameListener(onEndGameListener onEndGameListener){
        this.onEndGameListener = onEndGameListener;
    }

    public interface onEndGameListener{
        void onEnd(int score);
    }


}
