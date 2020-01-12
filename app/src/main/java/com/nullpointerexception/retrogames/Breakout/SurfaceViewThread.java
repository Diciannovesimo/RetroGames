package com.nullpointerexception.retrogames.Breakout;

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
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.Components.SaveScore;
import com.nullpointerexception.retrogames.R;

import java.util.Random;

public class SurfaceViewThread extends SurfaceView implements SurfaceHolder.Callback, Runnable
{
    private SurfaceHolder surfaceHolder = null;
    private Thread thread = null;
    private Paint paint = new Paint();
    private Canvas canvas = null;
    private Circle ball;
    private Paddle paddle;
    private Brick[] bricks = new Brick[200];
    private int nbBricks, brickWidth, brickHeight, screenWidth, screenHeight;
    private int CdistX, CdistY, nearestX, nearestY;
    private int fps = 1;
    private boolean threadRunning = false;
    boolean paused = true;
    int score = 0;
    int lives = 3;


    long calculatedFps = 0;
    private static final int TARGET_FPS = 60;
    float fpsDelay = 1f;
    int totalScore = 48;
    private Bitmap backgroundBitmap;
    private Bitmap lifeBitmap;
    private SoundPool soundPool;
    private static final int NUMBER_OF_SIMULTANEOUS_SOUNDS = 3;
    private final float LEFT_VOLUME_VALUE = 1.0f;
    private final float RIGHT_VOLUME_VALUE = 1.0f;
    private final int MUSIC_LOOP = 0;
    private final int SOUND_PLAY_PRIORITY = 1;
    private final float PLAY_RATE= 1.0f;
    private int[] sounds;
    private static final int HIT_SOUND = 0;
    private static final int NEW_WALL_SOUND = 1;
    private static final int HURT_SOUND = 2;
    private static final int BREAK_SOUND = 3;

    //|-----------------------|//
    // CONSTRUCTEUR DE LA VIEW //
    //|-----------------------|//

    public SurfaceViewThread(Context context)
    {
        super(context);

        // Get SurfaceHolder object.
        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);

        //  Setta il context alla classe brick, usata per accedere alle risorse grafiche
        Brick.setContext(context);

        //  Create soundPool
        initSoundpool();

        // Get width and height screen
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        screenWidth= display.getWidth();
        screenHeight= display.getHeight();

        backgroundBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.breakout_background), screenWidth, screenHeight, false);
        backgroundBitmap.setConfig(Bitmap.Config.ARGB_8888);

        lifeBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.breakout_life), 64, 64, false);
        lifeBitmap.setConfig(Bitmap.Config.ARGB_8888);

        // Inizializzazione paddle, ball, bricks
        paddle = new Paddle(screenWidth, screenHeight, 25);
        //paddle = new Paddle(screenWidth, screenHeight, 9);
        paddle.createPaddleDrawable(context);
        ball = new Circle(screenWidth, screenHeight, 55, 15);
        //ball = new Circle(screenWidth, screenHeight, 55, 6);
        ball.setBall(context);
        buildWall(true);

        // Set the SurfaceView object at the top of View object.
        setZOrderOnTop(true);
    }

    /**
     *      Inizializza la soundPool, usata per riprodurre effetti sonori
     */
    private void initSoundpool()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            soundPool= new SoundPool.Builder()
                    .setMaxStreams(NUMBER_OF_SIMULTANEOUS_SOUNDS)
                    .build();
        }
        else
            // Deprecated way of creating a SoundPool before Android API 21.
            soundPool= new SoundPool(NUMBER_OF_SIMULTANEOUS_SOUNDS, AudioManager.STREAM_MUSIC, 0);

        sounds = new int[4];

        //inserisce i suoni
        sounds[HIT_SOUND] = soundPool.load(getContext(), R.raw.breakout_hit, 1);
        sounds[NEW_WALL_SOUND] = soundPool.load(getContext(), R.raw.breakout_hurt, 1);
        sounds[HURT_SOUND] = soundPool.load(getContext(), R.raw.breakout_loose, 2);
        sounds[BREAK_SOUND] = soundPool.load(getContext(), R.raw.breakout_break, 0);
    }

    /**
     *      Riproduce un effetto sonoro
     *      @param sound intero identificativo del suono da riprodurre
     */
    private void playSound(int sound)
    {
        soundPool.play( sounds[sound],
                LEFT_VOLUME_VALUE,
                RIGHT_VOLUME_VALUE,
                SOUND_PLAY_PRIORITY,
                MUSIC_LOOP,
                PLAY_RATE);
    }

    /**
     *  Controlla eventuali collisioni tra la palla e i mattoni.
     *
     *  @param ball  Palla
     *  @param brick Mattoni
     *  @return  Se c'è stata una collisione
     */
    public boolean collisionBrick(Circle ball, Brick brick)
    {
        nearestX = (int) Math.max(brick.getRect().left,Math.min(ball.getX(),brick.getRect().right));
        nearestY = (int) Math.max(brick.getRect().top,Math.min(ball.getY(),brick.getRect().bottom));

        CdistX = ball.getX() - nearestX;
        CdistY = ball.getY() - nearestY;

        return(CdistX * CdistX + CdistY * CdistY) < (ball.getRadius() * ball.getRadius());
    }

    /**
     *      Controlla se c'è stata una collisione tra il giocatore (paddle) e la palla.
     *
     *      @param ball     Palla
     *      @param paddle   Giocatore
     *       @return        Se c'è stata una collisione.
     */
    public boolean collisionPaddle(Circle ball, Paddle paddle)
    {
        nearestX = (int) Math.max(paddle.getRect().left,Math.min(ball.getX(),paddle.getRect().right));
        nearestY = (int) Math.max(paddle.getRect().top,Math.min(ball.getY(),paddle.getRect().bottom));

        CdistX = ball.getX() - nearestX;
        CdistY = ball.getY() - nearestY;

        return (CdistX * CdistX + CdistY * CdistY) < (ball.getRadius() * ball.getRadius());
    }

    /**
            Controlla la collisione con i mattoni da destra/sinistra.
     */
    public void collisionLeftRight(Circle palla, Brick brick)
    {
        if(palla.getX() + palla.getXSpeed() < brick.getRect().left ||
                palla.getX() + palla.getXSpeed() > (brick.getRect().right - palla.getRadius()))
            palla.reverseXVelocity();
        else
            palla.reverseYVelocity();
    }


    /**
     *      Metodo principale, di aggiornamento dello stato del gioco.
     */
    public void update()
    {
        // Aggiorna la posizione della palla
        paddle.update(screenWidth);
        ball.move(fps);
        /*
        paddle.update(screenWidth, fpsDelay);
        ball.move(fps, fpsDelay);*/

        // Controlla le collisioni
        for (int i = 0; i < nbBricks; i++)
        {
            if (bricks[i].getVisibility() && collisionBrick(ball,bricks[i]))
            {
                //      Controlla la resistenza del mattone
                if(bricks[i].getRes() > 0)
                {
                    //  Danneggiamento del mattone

                    playSound(HIT_SOUND);

                    bricks[i].setRes();
                    collisionLeftRight(ball, bricks[i]);
                }
                else
                {
                    //  Distruzione del mattone

                    playSound(BREAK_SOUND);

                    bricks[i].setInvisible();
                    bricks[i].setRes();
                    collisionLeftRight(ball, bricks[i]);
                    score += 1;
                    if (score == totalScore)
                        buildWall(false);
                }
            }
        }
        // Controlla la collisione della palla con il giocatore (paddle)
        if (collisionPaddle(ball, paddle))
        {
            ball.setRandomXVelocity();
            ball.reverseYVelocity();
            ball.clearObstacleY((int) paddle.getRect().top - 2);
        }

        /*
                Controlla le collisioni della palla con lo schermo
         */
        if (ball.getX() + ball.getXSpeed() < ball.getRadius() )
        {
            ball.reverseXVelocity();
        }
        if (ball.getY() + ball.getYSpeed() < (screenHeight / 8))
        {
            ball.reverseYVelocity();
        }
        if (ball.getX() + ball.getXSpeed() > (screenWidth - ball.getRadius()))
        {
            ball.reverseXVelocity();
        }
        if (ball.getY() + ball.getYSpeed() > (screenHeight- ball.getRadius()))
        {
            ball.reverseYVelocity();
            lives--;
            playSound(HURT_SOUND);

            // Restart game
            if (lives == 0)
            {
                paddle.setX((int) ((screenWidth / 2) - (paddle.getRect().width()/2)));
                draw();
                int score = this.score;
                buildWall(true);

                ((AppCompatActivity) getContext()).runOnUiThread(() ->
                        new AlertDialog.Builder(getContext())
                                .setTitle(getResources().getString(R.string.gameOver))
                                .setMessage(getResources().getString(R.string.your_score_is)
                                        + ": " + score)
                                .setPositiveButton(getResources().getString(R.string.again),
                                        (dialog, which) -> dialog.dismiss())
                                .setNegativeButton(getResources().getString(R.string.exit), (dialog, which) ->
                                {
                                    dialog.dismiss();
                                    ((AppCompatActivity) getContext()).finish();
                                })
                                .show());
            }
        }
    }

    /**
     *      Genera il muro di blocchi da distruggere
     */
    public void buildWall(boolean paused)
    {
        // Dimensione dell'area del muro
        brickWidth = screenWidth / 8;
        brickHeight = screenHeight / 30;

        if(paused)
        {
            // Reset posizione palla
            ball.reset(screenWidth - 230, (int) paddle.getRect().top - ball.getRadius());
            this.paused = true;
        }
        else
            playSound(NEW_WALL_SOUND);

        // Costruisce il muro di blocchi
        nbBricks = 0;
        Random random = new Random();
        for (int column = 0; column < 8; column++)
        {
            for (int row = 6; row < 18; row++)
            {
                //  Costruisce il blocco
                bricks[nbBricks] = new Brick(column, row, brickWidth, brickHeight);

                //  Genera il colore
                switch (random.nextInt(3))
                {
                    default:
                    case 0:
                        bricks[nbBricks].setColor(Brick.GREEN);
                        break;
                    case 1:
                        bricks[nbBricks].setColor(Brick.BLUE);
                        break;
                    case 2:
                        bricks[nbBricks].setColor(Brick.RED);
                        break;
                }

                nbBricks++; //  Incrementa il numero di blocchi
            }
        }

        totalScore = nbBricks;

        // Restart game
        if (lives == 0)
        {
            Boolean lose = score != totalScore;

            SaveScore pong = new SaveScore();
            pong.save(App.BREAKOUT, score, getContext());

            score = 0;
            lives = 3;
        }
    }

    @Override
    public void run()
    {
        buildWall(true);

        while(threadRunning)
        {
            long startms = System.currentTimeMillis();

            if ( ! paused)
                update();

            draw();

            long frameTime = System.currentTimeMillis() - startms;
            if(frameTime != 0)
                calculatedFps = 1000 / frameTime;

            if(calculatedFps != TARGET_FPS)
                fpsDelay = (TARGET_FPS / (float) calculatedFps);
        }
    }


    //|----------------------------------|//
    // METHODE DESSINE SUR LA SURFACEVIEW //
    //|----------------------------------|//

    private void draw()
    {
        long startms = System.currentTimeMillis();

        int left = 0;
        int top = 0;
        int right = screenWidth;
        int bottom = screenHeight;

        Rect topRect = new Rect(left, top, right, (bottom/8));
        Rect screenRect = new Rect(0, topRect.bottom, screenWidth, screenHeight);

        if(surfaceHolder.getSurface().isValid())
        {
            canvas = surfaceHolder.lockCanvas();

            //  Disegna il background
            canvas.drawBitmap(backgroundBitmap, screenRect.left, screenRect.top, new Paint());

            Log.i("Performance", "[background - draw()]: " + (System.currentTimeMillis() - startms) + "ms.");

            // Disegna la parte superiore dove vengono mostrati i punteggi
            paint.setColor(Color.argb(240, 20, 20, 20));
            canvas.drawRect(topRect, paint);

            // Modifica l'oggetto paint
            paint.setColor(Color.argb(255, 255, 255, 255));
            paint.setTextSize(90);

            //  Disegna la linea
            canvas.drawLine(topRect.left, topRect.bottom, topRect.right, topRect.bottom, paint);

            int linesY = screenHeight / 11;

            /* // Scrive gli fps
            canvas.drawText("" + calculatedFps, 50, linesY, paint);
            canvas.drawText("" //getResources().getString(R.string.score)
                    + score, (int) (screenWidth / 2.4), linesY, paint);*/

            // Aggiorna lo score
            canvas.drawText(getResources().getString(R.string.score)
                    + " " + score, 50, linesY, paint);

            // Aggiorna le vite
            canvas.drawBitmap(lifeBitmap, (screenWidth - 158) - lifeBitmap.getWidth(),
                    linesY - lifeBitmap.getHeight(), paint);
            canvas.drawText(String.valueOf(lives), screenWidth - 150, linesY, paint);

            // Draw the paddle
            paddle.draw(canvas);

            long startms2 = System.currentTimeMillis();
            // Draw the bricks
            for (int i = 0; i < nbBricks; i++)
                if (bricks[i].getVisibility())
                    bricks[i].draw(canvas);
            Log.i("Performance", "[ bricks draw()]: " + (System.currentTimeMillis() - startms2) + "ms.");

            // Draw the ball
            ball.draw(canvas);

            // Send message to main UI thread to update the drawing to the main view special area.
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

        Log.i("Performance", "draw(): " + (System.currentTimeMillis() - startms) + "ms.\n");
    }

    //|---------------------------------------|//
    // METHODE DETECTION DE TOUCHER DE L'ECRAN //
    //|---------------------------------------|//
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK)
        {
            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:
                paused = false;
                if (motionEvent.getX() > screenWidth / 2)
                {
                    paddle.setMovementState(paddle.RIGHT);
                }
                else
                {
                    paddle.setMovementState(paddle.LEFT);
                }
                break;
            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:
                paddle.setMovementState(paddle.STOPPED);
                break;
        }
        return true;
    }


    //|----------------------------------|//
    // METHODE PAUSE POUR LA GAME_ACTIVITY //
    //|----------------------------------|//
    public void pause()
    {
        threadRunning = false;
        try
        {
            thread.join();
        }
        catch (InterruptedException e)
        {
            Log.e("Error:", "joining thread");
        }
    }


    //|-----------------------------------|//
    // METHODE START POUR LA GAME_ACTIVITY //
    //|-----------------------------------|//
    public void start()
    {
        // Create the child thread when SurfaceView is created.
        thread = new Thread(this);

        // Start to run the child thread.
        thread.start();

        // Set thread running flag to true.
        threadRunning = true;

        screenHeight = getHeight();
        screenWidth = getWidth();
    }


    //|---------------------|//
    // OVERRIDE SURFACE_VIEW //
    //|---------------------|//
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        // Start the game !
        start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // Set threadrunning flag to false when Surface is destroyed.
        // Then the thread will jump out the while loop and complete.
        pause();
    }
}