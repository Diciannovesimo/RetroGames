package com.nullpointerexception.retrogames.Pong;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
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

    private static final String KEY_HUMAN_PLAYER_DATA = "humanPlayer";
    private static final String KEY_COMPUTER_PLAYER_DATA = "computerPlayer";
    private static final String KEY_BALL_DATA = "ball";
    private static final String KEY_GAME_STATE = "state";
    private static final String TAG = "PongThread";

    //Stati di gioco
    private static final int STATE_PAUSE = 0;
    static final int STATE_READY = 1;
    static final int STATE_RUNNING = 2;
    private static final int STATE_LOSE = 3;
    private static final int STATE_WIN = 4;
    private static final int LOSE_VALUE = 3;
    private static final int PHYS_BALL_SPEED = 15;
    private static final int PHYS_PADDLE_SPEED = 8;
    private static final int PHYS_FPS = 60;
    private static final double PHYS_MAX_BOUNCE_ANGLE = 2 * Math.PI / 12; //52 gradi in radianti
    private static final int PHYS_COLLISION_FRAMES = 5;

    private int highscore_point = 0;

    private final SurfaceHolder mSurfaceHolder; //simula la superficie dello schermo
    private final Handler mStatusHandler;   //invia il messaggio relativo allo status
    private final Handler mScoreHandler;    //invia il messaggio relativo allo score
    private final Context mContext;         //contesto
    private Bundle universal_map;           //l'ultima mappa caricata dal restore
    private boolean mRun;                   //indica se il gioco è in run
    private int mState;                     //stato del gioco


    private Player mHumanPlayer;    //giocatore controllato dall'utente
    private Player mComputerPlayer; //giocatore controllato dalla cpu
    private Ball mBall;             //palla

    private Paint mMedianLinePaint;     //stile linea di mediana
    private Paint mCanvasBoundsPaint;   //stile del perimetro del campo
    private int mCanvasHeight;  //larghezza del canvas
    private int mCanvasWidth;   //altezza del canvas

    private onEndGameListener onEndGameListener;
    private OnAddScoreListener onAddScoreListener;

    /**
     * usato per permettere al computer di "dimenticarsi" di muovere il canvas in modo tale da
     * avere la senSazione di star giocando contro un essere umano.
     */
    private Random mRandomGen;
    private float mComputerMoveProbability; //probabilità che il computer si muova

    /**
     * SoundPool costants
     */
    private SoundPool soundPool;
    private final int NUMBER_OF_SIMULTANEOUS_SOUNDS = 5;
    private final float LEFT_VOLUME_VALUE = 1.0f;
    private final float RIGHT_VOLUME_VALUE = 1.0f;
    private final int MUSIC_LOOP = 0;
    private final int SOUND_PLAY_PRIORITY = 1;
    private final float PLAY_RATE= 1.0f;
    private static int idSound;

    /**
     * Costrutture di PongThread
     * @param surfaceHolder interfaccia di una superficie
     * @param context contesto
     * @param statusHandler handler dello status
     * @param scoreHandler handler dello score
     */
    PongThread(final SurfaceHolder surfaceHolder, final Context context, final Handler statusHandler, final Handler scoreHandler) {
        mSurfaceHolder = surfaceHolder;
        mStatusHandler = statusHandler;
        mScoreHandler = scoreHandler;
        mContext = context;

        mRun = false;

        update_Highscore();

        int paddleHeight = 200;
        int paddleWidth = 35;
        int ballRadius = 32;


        //creazione del giocatore umano
        Paint humanPlayerPaint = new Paint();
        humanPlayerPaint.setAntiAlias(true);
        humanPlayerPaint.setColor(Color.BLUE);
        mHumanPlayer = new Player(paddleWidth, paddleHeight, humanPlayerPaint);

        //creazione del giocatore cpu
        Paint computerPlayerPaint = new Paint();
        computerPlayerPaint.setAntiAlias(true);
        computerPlayerPaint.setColor(Color.RED);
        mComputerPlayer = new Player(paddleWidth, paddleHeight, computerPlayerPaint);

        //creazione della palla
        Paint ballPaint = new Paint();
        ballPaint.setAntiAlias(true);
        ballPaint.setColor(Color.GREEN);
        mBall = new Ball(ballRadius, ballPaint);


        //creazione dello stile delle linea di mediana
        mMedianLinePaint = new Paint();
        mMedianLinePaint.setAntiAlias(true);
        mMedianLinePaint.setColor(Color.YELLOW);
        mMedianLinePaint.setAlpha(80);
        mMedianLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mMedianLinePaint.setStrokeWidth(2.0f);
        mMedianLinePaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));

        //creazione dello stile delle linee dei limiti del campo
        mCanvasBoundsPaint = new Paint();
        mCanvasBoundsPaint.setAntiAlias(true);
        mCanvasBoundsPaint.setColor(Color.YELLOW);
        mCanvasBoundsPaint.setStyle(Paint.Style.STROKE);
        mCanvasBoundsPaint.setStrokeWidth(1.0f);

        //impostati altezza e larghezza
        mCanvasHeight = 1;
        mCanvasWidth = 1;

        mRandomGen = new Random();
        mComputerMoveProbability = 0.6f;
    }

    /**
     * Loop del gioco
     */
    @Override
    public void run() {
        long mNextGameTick = SystemClock.uptimeMillis();    //prende il ms di quando viene avviato il sistema
        int skipTicks = 1000 / PHYS_FPS;    //17ms
        Canvas c;
        while (mRun) {
            c = null;

            try {
                c = mSurfaceHolder.lockCanvas(null);    //restituisce il canvas bloccato

                if (c != null)
                {
                    if (mState == STATE_RUNNING)
                        updatePhysics();    //aggiorna la fisica

                    if (mRun)
                        updateDisplay(c);   //aggiorna il display

                }
            } finally {
                if (c != null) {
                    mSurfaceHolder.unlockCanvasAndPost(c);  //disegna i pixel sullo schermo e perde il contenuto in mSurfaceHolder
                }
            }


            mNextGameTick += skipTicks;
            long sleepTime = mNextGameTick - SystemClock.uptimeMillis();    //prende il ms dopo e li confronta con i primi

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);    //addormenta il thread per far sinconizzare fli fps
                } catch (InterruptedException e) {
                    Log.e(TAG, "Interrupted", e);
                }
            }

        }
    }

    /**
     * Setta lo stato del thread nella view a seconda del parametro passato.
     * @param running
     */
    void setRunning(boolean running) {
        mRun = running;
    }

    /**
     * salva lo stato del gioco mettendo in una
     * mappa (map) le informazioni riguardanti la posizione
     * di ogni oggetto sul layout (palla,giocatore,cpu)
     * @param map
     */
    void saveState(Bundle map) {
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

    /**
     * ripristina lo stato del gioco mettendo in una
     * mappa (universal_map = map) le informazioni riguardanti la posizione
     * di ogni oggetto sul layout (palla,giocatore,cpu)
     * universal_map servirà al metodo SetupNewGame
     * @param map
     */
    void restoreState(Bundle map) {
        float[] humanPlayerData = map.getFloatArray(KEY_HUMAN_PLAYER_DATA);
        mHumanPlayer.score = (int) humanPlayerData[2];
        movePlayer(mHumanPlayer, humanPlayerData[0], humanPlayerData[1]);

        float[] computerPlayerData = map.getFloatArray(KEY_COMPUTER_PLAYER_DATA);
        mComputerPlayer.score = (int) computerPlayerData[2];
        movePlayer(mComputerPlayer, computerPlayerData[0], computerPlayerData[1]);
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
        initSound();
        mState = mode;
        Resources res = mContext.getResources();
        switch (mState) {
            case STATE_READY:
                setupNewRound();    //setta un nuovo round
                break;
            case STATE_RUNNING:
                hideStatusText();   //nasconde il testo
                break;
            case STATE_WIN:         //aggiunge un punto
                setStatusText(res.getString(R.string.mode_win));
                mHumanPlayer.score++;
                if(onAddScoreListener != null)
                    onAddScoreListener.onAddScore(mHumanPlayer.score, mComputerPlayer.score);
                setupNewRound();
                break;
            case STATE_LOSE:        //toglie un punto
                setStatusText(res.getString(R.string.mode_lose));
                mComputerPlayer.score++;
                if(onAddScoreListener != null)
                    onAddScoreListener.onAddScore(mHumanPlayer.score, mComputerPlayer.score);
                if (mComputerPlayer.score == LOSE_VALUE)
                {
                    playSound();
                    game_over(mComputerPlayer.score,mHumanPlayer.score);
                    startNewGame();

                }
                setupNewRound();
                break;
            case STATE_PAUSE:       //gioco in pausa
                setStatusText(res.getString(R.string.mode_pause));
                break;
        }
    }

    /**
     * aggiorna l'highscore
     */
    private void update_Highscore() {
        if (App.scoreboardDao.getScore(App.PONG) != highscore_point) {
            highscore_point = App.scoreboardDao.getScore(App.PONG);
        }
    }

    /**
     * metodo chiamato quando il gioco finisce,
     * calcola il punteggio, controlla se è stato fatto un nuovo highscore
     * e fa partire il dialog.
     * @param cpuScore score della cpu
     * @param humanScore    score dell'umano
     */
    void game_over(int cpuScore, int humanScore){

        int score_pong = humanScore - cpuScore;

        int exit_mode = 1;

        if (score_pong < 0)
        {
            score_pong = 0;
            exit_mode = 1;
        }

        if (highscore_point < score_pong)
        {
            highscore_point = score_pong;
            SaveScore pong = new SaveScore();
            pong.save(App.PONG, highscore_point,mContext);
            exit_mode = 2;
        }
        else if(score_pong != 0) {
            exit_mode = 3;
        }

        if(onEndGameListener != null)
            onEndGameListener.onEnd(score_pong, exit_mode);

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
        idSound = soundPool.load(mContext, R.raw.hit_pong, SOUND_PLAY_PRIORITY);
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
    *il metodo pause in pongthread viene chiamato
    *solo se si è effettivamente
    *in game ovvero la palla è in movimento
    *
     */
    void pause() {
        if (mState != STATE_PAUSE) {
            setState(STATE_PAUSE);
        }
    }

    /**
     * Starta una nuova partita
     */
    void startNewGame() {
        mHumanPlayer.score = 0;
        mComputerPlayer.score = 0;
        setupNewRound();
        setState(STATE_PAUSE);
    }

    /**
     * @return vero se lo stato del gioco è diverso da Running.
     */
    boolean isBetweenRounds() {
        return mState != STATE_RUNNING;
    }


    /**
     * sincronizza la posizione del giocatore sul layout
     * @param dy posizione sull'asse y del paddle
     */
    void moveHumanPaddle(float dy) {
        movePlayer(mHumanPlayer, mHumanPlayer.bounds.left, mHumanPlayer.bounds.top + dy);
    }

    /**
     * setta la superficie del layout
     * @param width larghezza
     * @param height altezza
     */
    void setSurfaceSize(int width, int height) {
        mCanvasWidth = width;
        mCanvasHeight = height;
        setupNewRound();
    }

    /**
     * aggiorna la posizione dei canvas, controlla le collisioni e le
     * condizioni di vittoria o sconfitta
     */
    private void updatePhysics() {

        //Permette di far vedere l'effetto luminoso per 5 frame
        if (mHumanPlayer.collision > 0) {
            mHumanPlayer.collision--;
        }
        if (mComputerPlayer.collision > 0) {
            mComputerPlayer.collision--;
        }


        if (collision(mHumanPlayer, mBall)) {
            playSound();
            handleCollision(mHumanPlayer, mBall);
            mHumanPlayer.collision = PHYS_COLLISION_FRAMES;
        } else if (collision(mComputerPlayer, mBall)) {
            playSound();
            handleCollision(mComputerPlayer, mBall);
            mComputerPlayer.collision = PHYS_COLLISION_FRAMES;
        } else if (ballCollidedWithTopOrBottomWall()) {
            playSound();
            mBall.dy = -mBall.dy;
        } else if (ballCollidedWithRightWall()) {   //l'utente ha segnato
            playSound();
            setState(STATE_WIN);
            return;
        } else if (ballCollidedWithLeftWall()) {    //l'utente ha subito un gol
            playSound();
            setState(STATE_LOSE);
            return;
        }



        if (mRandomGen.nextFloat() < mComputerMoveProbability) {    //Fa muove la cpu solo se il numnero generato è minore di 0.6
            doAI(); //Muove il paddle della cpu
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
        //disegna il campo nero
        canvas.drawColor(Color.BLACK);
        canvas.drawRect(5, 8, mCanvasWidth, mCanvasHeight - 5 , mCanvasBoundsPaint);

        //disegna la linea centrale trattegiata
        final int middle = mCanvasWidth / 2;
        canvas.drawLine(middle, 8, middle, mCanvasHeight - 5, mMedianLinePaint);

        setScoreText(mHumanPlayer.score + "    " + mComputerPlayer.score);

        //massaggi di handler per i punteggi
        handleHit(mHumanPlayer);
        handleHit(mComputerPlayer);

        //disegna i paddle dei due player
        canvas.drawRoundRect(mHumanPlayer.bounds, 5, 5, mHumanPlayer.paint);
        canvas.drawRoundRect(mComputerPlayer.bounds, 5, 5, mComputerPlayer.paint);

        //disegna la palla
        canvas.drawCircle(mBall.cx, mBall.cy, mBall.radius, mBall.paint);
    }

    /**
     * disegna un'ombra quando i paddle
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
            //ripristina i valori di default
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

            //rimette la palla e i giocatori nella posizione in cui si trovavano l'ultima volta
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
     * Invia il messaggio di status tramite handler
     * @param text testo di vittoria/sconfitta/pausa
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
     * Nasconde il messaggio di status tramite handler
     */
    private void hideStatusText() {
        Message msg = mStatusHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putInt("vis", View.INVISIBLE);
        msg.setData(b);
        mStatusHandler.sendMessage(msg);
    }

    /**
     * Invia il messaggio dello score tramite handler
     * @param text testo con i puntia
     */
    private void setScoreText(String text) {
        Message msg = mScoreHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("text", text);
        msg.setData(b);
        mScoreHandler.sendMessage(msg);

    }

    /**
     * Imposta la nuova posizione del giocatore
     * @param player giocatore
     * @param left posizione sull'asse x del paddle
     * @param top posizione sull'asse y del paddle
     */
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
     * Constrolla se ci sono collisioni tra il paddle e la palla
     * @param player paddle
     * @param ball palla
     * @return vero se c'è una collisione, altrimenti falso
     */
    private boolean collision(Player player, Ball ball) {
        return player.bounds.intersects(
                ball.cx - mBall.radius,
                ball.cy - mBall.radius,
                ball.cx + mBall.radius,
                ball.cy + mBall.radius);
    }

    /**
     *  Imposta la nuova direzione della palla dopo essere stata toccata dal giocatore
     */
    private void handleCollision(Player player, Ball ball) {
        float relativeIntersectY = player.bounds.top + player.paddleHeight / 2 - ball.cy;
        float normalizedRelativeIntersectY = relativeIntersectY / (player.paddleHeight / 2);
        double bounceAngle = normalizedRelativeIntersectY * PHYS_MAX_BOUNCE_ANGLE;  //angolo di rimbalzo

        ball.dx = (float) (-Math.signum(ball.dx) * PHYS_BALL_SPEED * Math.cos(bounceAngle));    //coseno per l'asse x
        ball.dy = (float) (PHYS_BALL_SPEED * -Math.sin(bounceAngle));   //seno per l'asse y

        if (player == mHumanPlayer) {
            mBall.cx = mHumanPlayer.bounds.right + mBall.radius;
        } else {
            mBall.cx = mComputerPlayer.bounds.left - mBall.radius;
        }
    }

    void setOnEndGameListener(onEndGameListener onEndGameListener){
        this.onEndGameListener = onEndGameListener;
    }

    void setOnAddScoreListener(OnAddScoreListener onAddScoreListener) {
        this.onAddScoreListener = onAddScoreListener;
    }

    /**
     * Listener per la fine del gioco
     */
    public interface onEndGameListener{
        void onEnd(int score, int exit_mode);
    }

    /**
     * Listener per un nuovo punteggio
     */
    public interface OnAddScoreListener {
        void onAddScore(int humanScore, int computerScore);
    }

}
