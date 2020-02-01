package com.nullpointerexception.retrogames.Hole;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.Components.SaveScore;
import com.nullpointerexception.retrogames.R;

import java.util.Random;

public class CanvasView extends View implements View.OnTouchListener {

    private double y;   //Coordinata dell'asse y della palla
    private double x;   //Coordinata dell'asse x della palla
    private double vx;  //nuova coordinata asse x della palla
    private double vy;  //nuova coordinata asse y della palla
    private Bitmap ball2;   //Immagine della palla
    private Bitmap background; //Immagine dello sfondo
    private Bitmap hole;    //Immagine della buca
    private int ballRadius; //Raggio della palla
    private int holeX = 300;   //Posizione della buca asse x
    private int holeY = 300;    //Posizione della buca asse y
    private Random random = new Random();
    private int score;  //punteggio
    private boolean warningVisible; //serve per rendere immuni
    private Paint brush = new Paint();  //stile dello sfondo dei messaggi di warning e del punteggio
    private Paint paint = new Paint(); //stile del messaggio di start
    private long lastInvalidate; //contiene il tempo in ms dell'ultima volta che è stato refreshato lo schermo
    private int life; //vita
    private final Rect textBounds = new Rect(); //don't new this up in a draw method
    private boolean inPause = false;  //indica se il gioco è in pausa o meno
    private boolean gameStarted; //indica se il game è iniziato
    private boolean gameOverDisplayed; //indica se il game over è mostrato
    private float fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()); //dimensione del testo
    private AlertDialog mDlgMsg = null;
    private OnChangeScoreListener onChangeScoreListener;

    //SoundPool costants
    private SoundPool soundPool;
    private final int NUMBER_OF_SIMULTANEOUS_SOUNDS = 5;
    private final float LEFT_VOLUME_VALUE = 1.0f;
    private final float RIGHT_VOLUME_VALUE = 1.0f;
    private final int MUSIC_LOOP = 0;
    private final int SOUND_PLAY_PRIORITY = 1;
    private final float PLAY_RATE= 1.0f;
    static int[] sm;

    /**
     * Cra il CanvasView
     * @param context contesto
     */
    public CanvasView(Context context) {
        super(context);
        init();
        initSound();
    }

    /**
     * Cra il CanvasView
     * @param context contesto
     * @param attrs attributi del layout xml
     */
    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initSound();
    }

    /**
     * Inizilizza le bitmap e determina il raggio della palla
     */
    private void init() {
        //ball2 = BitmapFactory.decodeResource(getResources(), R.mipmap.ball2);   //Inizializza l'immagine della palla
        ball2 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getContext().getResources(), R.mipmap.ball2),  125,  125, false);
        ballRadius = ball2.getWidth() / 2;  //Determino il raggio della palla
        background = BitmapFactory.decodeResource(getResources(), R.mipmap.background); //Inizializzo l'immagine dello sfondo
        hole = BitmapFactory.decodeResource(getResources(), R.mipmap.hole); //Inizializzo l'immagine della buca
        setOnTouchListener(this);
    }

    /**
     * Reimposta tutti i valori di default per reiniziare una nuova partita
     */
    private void restartGame() {
        //Setta le coordinate della palline al centro dello schermo
        y = getHeight() / 2;
        x = getWidth() / 2;
        //Reimposta lo score e le vite
        score = 0;
        life = 3;
        if(onChangeScoreListener != null)
            onChangeScoreListener.onChangeScore(score, life);
        gameStarted = true;
        gameOverDisplayed = false;
    }

    /**
     * Imposta la fine del gioco
     */
    private void gameOver() {
        SaveScore saveScore = new SaveScore();
        saveScore.save(App.HOLE, score, getContext());
        mDlgMsg = new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.gameOver))
                .setMessage(getResources().getString(R.string.your_score_is) + ": " + score)
                .setPositiveButton(getResources().getString(R.string.again), (dialog, which) -> {
                    mDlgMsg.dismiss();
                    restartGame();
                })
                .setNegativeButton(getResources().getString(R.string.exit), (dialog, which) -> {
                    mDlgMsg.dismiss();
                    if(getContext() instanceof MainActivityHole)
                    {
                        cleanUpIfEnd();
                        ((MainActivityHole) getContext()).finish();
                    }

                })
                .show();

        gameStarted = false;
        gameOverDisplayed = true;

    }

    /**
     * Gestisce il tocco della palla con il muro
     */
    private void wallCrash() {
        if (warningVisible) {
            return;
        }

        playSound(1);
        life = life - 1;  //Tolgo una vita
        if(onChangeScoreListener != null)
            onChangeScoreListener.onChangeScore(score, life);

        if (life == 0) {
            gameOver();
            return;
        }

        //Rende immune per 3 secondi
        warningVisible = true;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                warningVisible = false;
            }
        }, 3000);


        //Mostra il messaggio quando si perde una vita
        TextView tv = new TextView(getContext());
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(20);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setText(getResources().getString(R.string.you_hit_the_wall));

        //Genera un layout per mostrare la textView
        LinearLayout layout = new LinearLayout(getContext());
        layout.setBackgroundResource(R.color.black_overlay);
        layout.addView(tv);

        //Inserisce nel toast nel layout e lo mostra
        Toast toast = new Toast(getContext());
        toast.setView(layout);
        toast.setGravity(Gravity.BOTTOM, 100, 300);
        toast.show();
    }

    /**
     * Gestisce il movimento della palla
     */
    private void move() {
        //Procede solo se il gioco non è in pausa
        if(!inPause) {
            //determina la nuova posizione della pallina
            y = y + vy;
            x = x + vx;

            //determina un range per il buco della pallina
            double distance = Math.sqrt(Math.pow(x - holeX, 2) + Math.pow(y - holeY, 2));

            if (distance < 50) {
                //genera una nunova posizione per il buco
                holeX = random.nextInt(getWidth() - ballRadius * 2) + ballRadius;
                holeY = random.nextInt((getHeight() - 20) - ballRadius * 2) + (ballRadius - 20);
                score++;
                playSound(0);
                if (onChangeScoreListener != null)
                    onChangeScoreListener.onChangeScore(score, life);
            }

            //la palla prende la posizione della buca
            if (x < ballRadius)
                x = ballRadius;

            if (y < ballRadius)
                y = ballRadius;

            if (x >= getWidth() - ballRadius)
                x = getWidth() - ballRadius;

            if (y >= getHeight() - ballRadius)
                y = getHeight() - ballRadius;

            //determino se la palla ha toccato il muro
            if (x >= getWidth() - ballRadius) {
                wallCrash();
            } else if (y >= getHeight() - ballRadius) {
                wallCrash();
            } else if (x <= ballRadius) {
                wallCrash();
            } else if (y <= ballRadius) {
                wallCrash();
            }

            //Refresh dello schermo solo dopo 5ms,
            if (System.currentTimeMillis() - lastInvalidate > 5) {
                invalidate(); //Refresh della schermata
                lastInvalidate = System.currentTimeMillis();
            }
        }
    }

    /**
     * Imposta le nuove coordinate alla pallina aumentandone la velocità
     * @param dx coordinata dell'asse x
     * @param dy coordinata dell'asse y
     */
    public void changeVelocity(float dx, float dy) {
        if (gameStarted ) {
            vx = dx * 10;
            vy = dy * 10;
            move();
        }
    }

    /**
     * Disegna tutti i canvas
     * @param canvas canvas
     */
    protected void onDraw(Canvas canvas) {

        //determina lo stile dello start game
        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(true);
        paint.setTextSize(fontSize);

        //determina lo stile dello sfondo dei messaggi
        brush.setStrokeWidth(10);
        brush.setColor(Color.WHITE);

        canvas.drawBitmap(background, 0, 0, null);  //disegna lo sfondo
        canvas.drawBitmap(hole, holeX - ballRadius, holeY - ballRadius, null);  //disegna la buca
        canvas.drawBitmap(ball2, (int) (x - ballRadius), (int) (y - ballRadius), null);  //disegna la palla

        float width = 49 * fontSize / 8;

        if (!gameStarted && !gameOverDisplayed)
        {
            canvas.drawRect(getWidth() / 2 - 290, getHeight() / 2 - 70, getWidth() / 2 + 290, getHeight() / 2 + 70, brush); //disegna rettangolo per lo start game
            drawTextCentred(canvas, paint, getContext().getResources().getString(R.string.start_game), getWidth() / 2 - width / 2, getHeight() / 2); //scrive nel rettangolo di start game
        }

    }

    /**
     * Disegna il testo centrato
     * @param canvas canvas
     * @param paint stile
     * @param text testo
     * @param cx asse x
     * @param cy asse y
     */
    public void drawTextCentred(Canvas canvas, Paint paint, String text, float cx, float cy){
        paint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, cx, cy - textBounds.exactCenterY(), paint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            //determino pressapoco la dimensione del tasto start game
            if ((x >= getWidth() / 2 - 290 && x <= getWidth() / 2 + 290) && (y >= getHeight() / 2 - 70 && y <= getHeight() / 2 + 70)) {
                if (gameStarted == false) {
                    restartGame();
                }
            }
        }
        return true;
    }

    public interface OnChangeScoreListener{
        void onChangeScore(long score, int life);
    }

    public void setOnChangeScoreListener(OnChangeScoreListener onChangeScoreListener){
        this.onChangeScoreListener = onChangeScoreListener;
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
        sm[0] = soundPool.load(getContext(), R.raw.glug, SOUND_PLAY_PRIORITY);
        sm[1] = soundPool.load(getContext(), R.raw.hit, SOUND_PLAY_PRIORITY);
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

    //GETTER AND SETTER
    public boolean isInPause() {
        return inPause;
    }

    public void setInPause(boolean inPause) {
        this.inPause = inPause;
    }
}
