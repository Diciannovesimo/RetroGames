package com.nullpointerexception.retrogames.Hole;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nullpointerexception.retrogames.R;

import java.util.Random;

public class CanvasView extends View implements View.OnTouchListener {
    private static final int DOWN = 1;
    private static final int RIGHT = 1;

    private double y;   //Coordinata dell'asse y della palla
    private double x;   //Coordinata dell'asse x della palla
    private double directionY = DOWN;
    private double directionX = RIGHT;
    private Bitmap ball2;   //Immagine della palla
    private Bitmap background; //Immagine dello sfondo
    private Bitmap hole;    //Immagine della buca
    private double vx;
    private double vy;
    private int ballRadius; //Raggio della palla
    private int holeX = 700;
    private int holeY = 600;
    private Random random = new Random();
    private int score;
    private boolean warningVisible;
    private boolean wallCrash;
    private Paint brush = new Paint();
    private Paint scorePaint = new Paint();
    private Paint warningPaint = new Paint();
    private Paint startGamePaint = new Paint();
    private long lastInvalidate;
    private int lives;
    private boolean gameStarted;
    private boolean gameOverDisplayed;


    /**
     * Crea il CanvasView e setta il contesto
     * @param context contesto
     */

    public CanvasView(Context context) {
        super(context);
        init();
    }


    Resources r = getResources();
    float fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());

    /**
     * Cra il CanvasView
     * @param context contesto
     * @param attrs attributi del layout xml
     */
    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    /**
     * Inizilizza le bitmap e determina il raggio della palla
     */
    private void init() {
        ball2 = BitmapFactory.decodeResource(getResources(), R.mipmap.ball2);   //Inizializza l'immagine della palla
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
        lives = 3;
        gameStarted = true;
        gameOverDisplayed = false;
    }

    /**
     * Imposta la fine del gioco
     */
    private void gameOver() {
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

        lives = lives - 1;  //Tolgo una vita
        if (lives == 0) {
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

        //TODO: Vedere qui per il messaggio di quando si perde una vita

        //Mostra il messaggio quando si eprde una vita
        TextView tv = new TextView(getContext());
        tv.setTextColor(Color.RED);
        tv.setTextSize(20);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setText("BOOM!   -1");

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
        //y = y + vy * directionY;
        //x = x + vx * directionX;
        //determina la nuova posizione della pallina
        y = y + vy;
        x = x + vx;

        //determina un range per il buco della pallina
        double distance = Math.sqrt(Math.pow(x - holeX, 2) + Math.pow(y - holeY, 2));

        if (distance < 50) {
            //genera una nunova posizione per il buco
            holeX = random.nextInt(getWidth() - ballRadius * 2) + ballRadius;
            holeY = random.nextInt(getHeight() - ballRadius * 2) + ballRadius;
            score ++;
        }


        if (x < ballRadius) {
            x = ballRadius;
        }
        if (y < ballRadius) {
            y = ballRadius;
        }
        if (x >= getWidth() - ballRadius) {
            x = getWidth() - ballRadius;
        }
        if (y >= getHeight() - ballRadius) {
            y = getHeight() - ballRadius;
        }
        if (x >= getWidth() - ballRadius) {
            wallCrash();} else
        if (y >= getHeight() - ballRadius) {
            wallCrash();} else
        if (x <= ballRadius) {
            wallCrash();} else
        if (y <= ballRadius) {
            wallCrash();
        }
        if (System.currentTimeMillis() - lastInvalidate > 5) {
            invalidate();
            lastInvalidate = System.currentTimeMillis();
        }
    }

    /**
     * Imposta le nuove coordinate alla pallina aumentandone la velocità
     * @param dx
     * @param dy
     */
    public void changeVelocity(float dx, float dy) {
        if (gameStarted == false) {

        } else {
            vx = dx * 10;
            vy = dy * 10;
            move();
        }
    }

    protected void onDraw(Canvas canvas) {
        scorePaint.setColor(Color.BLACK);
        scorePaint.setFakeBoldText(true);
        scorePaint.setTextSize(fontSize);
        warningPaint.setColor(Color.RED);
        warningPaint.setTextSize(120);
        warningPaint.setTextAlign(Paint.Align.CENTER);
//        startGamePaint.setColor(Color.RED);
//        startGamePaint.setTextSize(fontSize);
//        startGamePaint.setFakeBoldText(true);
        brush.setStrokeWidth(10);
        brush.setColor(Color.WHITE);
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawBitmap(hole, holeX - ballRadius, holeY - ballRadius, null);
        canvas.drawBitmap(ball2, (int) (x - ballRadius), (int) (y - ballRadius), null);
        float width = 49 * fontSize / 8;
        float height = 14 * fontSize / 8;
        canvas.drawRect(30, 30, 30 + width, 30 + height, brush);
        canvas.drawRect(getWidth() - width - 30, 30, getWidth() - 30, height + 30, brush);
        if (gameStarted == false) {
        canvas.drawRect(getWidth() / 2 - 290, getHeight() / 2 - 70, getWidth() / 2 + 290, getHeight() / 2 + 70, brush);
        }
        drawTextCentred(canvas, scorePaint, "Score: " + score, 70, 30 + height / 2);
        drawTextCentred(canvas, scorePaint, "Lives: " + lives, getWidth() - width + 30, 30 + height / 2);
        if (gameStarted == false) {
            drawTextCentred(canvas, scorePaint, "START GAME", getWidth() / 2 - width / 2, getHeight() / 2);
        }
        if (gameOverDisplayed == true) {
            drawTextCentred(canvas, warningPaint, "GAME OVER", getWidth() / 2, getHeight() / 4);
        }
    }

    private final Rect textBounds = new Rect(); //don't new this up in a draw method

    public void drawTextCentred(Canvas canvas, Paint paint, String text, float cx, float cy){
        paint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, cx, cy - textBounds.exactCenterY(), paint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            if ((x >= getWidth() / 2 - 290 && x <= getWidth() / 2 + 290) && (y >= getHeight() / 2 - 70 && y <= getHeight() / 2 + 70)) {
                if (gameStarted == false) {
                    restartGame();
                }
            }
        }
        return true;
    }
}
