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

    private double y;
    private double x;
    private double directionY = DOWN;
    private double directionX = RIGHT;
    private Bitmap ball2;
    private Bitmap background;
    private Bitmap hole;
    private double vx;
    private double vy;
    private int ballRadius;
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


    public CanvasView(Context context) {
        super(context);
        init();
    }

    Resources r = getResources();
    float fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        ball2 = BitmapFactory.decodeResource(getResources(), R.mipmap.ball2);
        ballRadius = ball2.getWidth() / 2;
        background = BitmapFactory.decodeResource(getResources(), R.mipmap.background);
        hole = BitmapFactory.decodeResource(getResources(), R.mipmap.hole);
        setOnTouchListener(this);
    }

    private void restartGame() {
        y = getHeight() / 2;
        x = getWidth() / 2;
        score = 0;
        lives = 3;
        gameStarted = true;
        gameOverDisplayed = false;
    }

    private void gameOver() {
        gameStarted = false;
        gameOverDisplayed = true;

    }

    private void wallCrash() {
        if (warningVisible) {
            return;
        }

        lives = lives - 1;
        if (lives == 0) {
            gameOver();
            return;
        }
        warningVisible = true;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                warningVisible = false;
            }
        }, 3000);

        TextView tv = new TextView(getContext());
        tv.setTextColor(Color.RED);
        tv.setTextSize(20);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setText("BOOM!   -1");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setBackgroundResource(R.color.black_overlay);
        layout.addView(tv);

        Toast toast = new Toast(getContext());
        toast.setView(layout);
        toast.setGravity(Gravity.BOTTOM, 100, 300);
        toast.show();
    }

    private void move() {
        y = y + vy * directionY;
        x = x + vx * directionX;

        double distance = Math.sqrt(Math.pow(x - holeX, 2) + Math.pow(y - holeY, 2));

        if (distance < 50) {
            holeX = random.nextInt(getWidth() - ballRadius * 2) + ballRadius;
            holeY = random.nextInt(getHeight() - ballRadius * 2) + ballRadius;
            score = score + 1;
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

    public void changeVelocity(float dx, float dy) {
        if (gameStarted == false) {

        } else {
            vx = dx;
            vy = dy;
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
