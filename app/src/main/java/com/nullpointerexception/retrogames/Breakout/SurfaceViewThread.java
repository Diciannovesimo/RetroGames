package com.nullpointerexception.retrogames.Breakout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.nullpointerexception.retrogames.R;

public class SurfaceViewThread extends SurfaceView implements SurfaceHolder.Callback, Runnable
{
    private SurfaceHolder surfaceHolder = null;
    private Thread thread = null;
    private Paint paint = new Paint();
    private Canvas canvas = null;
    private Circle circle;
    private Paddle paddle;
    private Brick[] bricks = new Brick[200];
    private int nbBricks, brickWidth, brickHeight, screenWidth, screenHeight;
    private int CdistX, CdistY, nearestX, nearestY;
    private int fps = 1;
    private boolean threadRunning = false;
    boolean paused = true;
    int score = 0;
    int lives = 3;
    SharedPreferences pref;

    //  Variabili mie
    int totalScore = 48;
    private SoundPool soundPool;
    private static final int NUMBER_OF_SIMULTANEOUS_SOUNDS = 5;
    private final float LEFT_VOLUME_VALUE = 1.0f;
    private final float RIGHT_VOLUME_VALUE = 1.0f;
    private final int MUSIC_LOOP = 0;
    private final int SOUND_PLAY_PRIORITY = 1;
    private final float PLAY_RATE= 1.0f;
    private int[] sounds;
    private static final int HIT_SOUND = 0;
    private static final int HURT_SOUND = 1;
    private static final int LOOSE_SOUND = 2;
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

        //  Create soundPool
        initSoundpool();

        //Get width and height screen
        Display ecran = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        screenWidth= ecran.getWidth();
        screenHeight= ecran.getHeight();

        //Initialisation paddle, balle, briques
        paddle = new Paddle(screenWidth, screenHeight);
        circle = new Circle(screenWidth, screenHeight, 55, 15);
        BuildWall();

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
        sounds[HURT_SOUND] = soundPool.load(getContext(), R.raw.breakout_hurt, 1);
        sounds[LOOSE_SOUND] = soundPool.load(getContext(), R.raw.breakout_loose, 2);
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

    //|-----------------------------------------|//
    // METHODE COLLISION ENTRE CERCLE ET BRIQUES //
    //|-----------------------------------------|//

    public boolean collisionBrick(Circle c, Brick b)
    {
        nearestX = (int) Math.max(b.getRect().left,Math.min(c.getX(),b.getRect().right));
        nearestY = (int) Math.max(b.getRect().top,Math.min(c.getY(),b.getRect().bottom));

        CdistX = c.getX() - nearestX;
        CdistY = c.getY() - nearestY;

        return(CdistX * CdistX + CdistY * CdistY) < (c.getRadius() * c.getRadius());
    }

    //|------------------------------------------|//
    // METHODE COLLISION ENTRE CERCLE ET RAQUETTE //
    //|------------------------------------------|//

    public boolean collisionPaddle(Circle c, Paddle p)
    {
        nearestX = (int) Math.max(p.getRect().left,Math.min(c.getX(),p.getRect().right));
        nearestY = (int) Math.max(p.getRect().top,Math.min(c.getY(),p.getRect().bottom));

        CdistX = c.getX() - nearestX;
        CdistY = c.getY() - nearestY;

        return (CdistX * CdistX + CdistY * CdistY) < (c.getRadius() * c.getRadius());
    }



    //|-----------------------------------------------------|//
    // METHODE COLLISION CHANGEMENT D'ANGLE GAUCHE ET DROITE //
    //|-----------------------------------------------------|//

    public void collisionLeftRight(Circle c, Brick b)
    {
        if(c.getX() + c.getXSpeed() < b.getRect().left ||
                c.getX() + c.getXSpeed() > (b.getRect().right - c.getRadius()))
            c.reverseXVelocity();
        else
            c.reverseYVelocity();
    }


    //|----------------------|//
    // METHODE DE MISE A JOUR //
    //|----------------------|//

    public void update()
    {
        //Mise à jour de la raquette et de la balle
        paddle.update(screenWidth);
        circle.move(fps);

        // Check for ball colliding with a brick
        for (int i = 0; i < nbBricks; i++)
        {
            if (bricks[i].getVisibility() && collisionBrick(circle,bricks[i]))
            {
                if(bricks[i].getRes() > 0)
                {
                    playSound(HIT_SOUND);

                    bricks[i].setRes();
                    collisionLeftRight(circle,bricks[i]);
                }
                else
                {
                    playSound(BREAK_SOUND);

                    bricks[i].setInvisible();
                    bricks[i].setRes();
                    collisionLeftRight(circle,bricks[i]);
                    score += 1;
                    if (score == totalScore)
                    {
                        paused = true;
                        BuildWall();
                    }
                }
            }
        }
        // Check for ball colliding with paddle
        if (collisionPaddle(circle, paddle))
        {
            circle.setRandomXVelocity();
            circle.reverseYVelocity();
            circle.clearObstacleY((int) paddle.getRect().top - 2);
        }

        //Check for ball colliding with screen
        if (circle.getX() + circle.getXSpeed() < circle.getRadius() )
        {
            circle.reverseXVelocity();
        }
        if (circle.getY() + circle.getYSpeed() < brickHeight*2)
        {
            circle.reverseYVelocity();
        }
        if (circle.getX() + circle.getXSpeed() > (screenWidth - circle.getRadius()))
        {
            circle.reverseXVelocity();
        }
        if (circle.getY() + circle.getYSpeed() > (screenHeight- circle.getRadius()))
        {
            circle.reverseYVelocity();
            lives--;

            // Restart game
            if (lives == 0)
            {
                playSound(LOOSE_SOUND);

                paused = true;
                BuildWall();
            }
            else
                playSound(HURT_SOUND);
        }
    }




    //|---------------------------|//
    // METHODE CONSTRUCTION DU MUR //
    //|---------------------------|//

    public void BuildWall()
    {
        // Largeur et longueur des briques
        brickWidth = screenWidth / 8;
        brickHeight = screenHeight / 15;

        // En cas de reset du jeu
        circle.reset(screenWidth + 230, screenHeight- circle.getRadius());

        // Build a wall of bricks
        nbBricks = 0;
        for (int column = 0; column < 8; column++)
        {
            for (int row = 3; row < 9; row++)
            {
                bricks[nbBricks] = new Brick(column, row, brickWidth, brickHeight);
                nbBricks++;
            }
        }

        // Restart game
        if (lives == 0 || score == totalScore)
        {
            Boolean lose = score != totalScore;

            pref = getContext().getSharedPreferences("PlayerScore", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("LatestScore", score);
            editor.apply();
            score = 0;
            lives = 3;
            Intent ScoreActivity = new Intent(getContext(), Scoreboard.class);
            ScoreActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ScoreActivity.putExtra("Lose", lose);
            getContext().startActivity(ScoreActivity);
        }
    }




    //|---------------------------|//
    // METHODE RUN POUR LE THREAD  //
    //|---------------------------|//

    @Override
    public void run()
    {
        //Initialisation des briques
        BuildWall();

        while(threadRunning)
        {
            if (!paused)
                update();

            draw();
            try { Thread.sleep(fps); } catch (InterruptedException ex) {}
        }
    }




    //|----------------------------------|//
    // METHODE DESSINE SUR LA SURFACEVIEW //
    //|----------------------------------|//

    private void draw()
    {
        int left = 0;
        int top = 0;
        int right = screenWidth;
        int bottom = screenHeight;
        Rect fond = new Rect(left, top, right, bottom);
        Rect fond2 = new Rect(left, top, right, (int)(bottom/8));
        Rect fond3 = new Rect(left, bottom-(bottom/12), right, bottom);

        if(surfaceHolder.getSurface().isValid())
        {
            canvas = surfaceHolder.lockCanvas();

            // Draw the specify canvas background color.
            Paint background = new Paint();
            background.setColor(Color.argb(255, 60, 60, 60));
            canvas.drawRect(fond, background);

            // Background top of the screen
            paint.setColor(Color.argb(240, 20, 20, 20));
            canvas.drawRect(fond2, paint);

            // Choose the brush color for drawing
            paint.setColor(Color.argb(255, 255, 255, 255));
            paint.setTextSize(160);

            // Draw the score
            if(score < 10)
                canvas.drawText(String.valueOf("0"+score), (int) (screenWidth / 2.4), screenHeight / 10, paint);
            else
                canvas.drawText(String.valueOf(score), (int) (screenWidth / 2.4), screenHeight / 10, paint);

            // Draw life
            if(lives == 1)
            {
                paint.setTextSize(90);
                paint.setColor(Color.RED);
                canvas.drawText(String.valueOf(lives), screenWidth - 150, screenHeight / 11, paint);
            }
            else
            {
                paint.setTextSize(90);
                canvas.drawText(String.valueOf(lives), screenWidth - 150, screenHeight / 11, paint);
            }

            // Background bottom of the screen
            paint.setColor(Color.argb(240, 20, 20, 20));
            canvas.drawRect(fond3, paint);

            // Draw the paddle
            paint.setColor(Color.WHITE);
            canvas.drawRect(paddle.getRect(), paint);

            // Draw the bricks
            for (int i = 0; i < nbBricks; i++)
            {
                if (bricks[i].getVisibility())
                {
                    bricks[i].draw(canvas);
                }
            }

            // Draw the ball
            circle.draw(canvas);

            // Send message to main UI thread to update the drawing to the main view special area.
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
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