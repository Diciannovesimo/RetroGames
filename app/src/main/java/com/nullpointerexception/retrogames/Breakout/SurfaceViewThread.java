package com.nullpointerexception.retrogames.Breakout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class SurfaceViewThread extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder surfaceHolder = null;
    private Thread thread = null;
    private Paint paint = new Paint();
    private Canvas canvas = null;
    private Cercle cercle;
    private Paddle paddle;
    private Brique[] bricks = new Brique[200];
    private int nbBricks, brickWidth, brickHeight, screenWidth, screenHeight;
    private int CdistX, CdistY, nearestX, nearestY;
    private int fps = 1;
    private boolean threadRunning = false;
    boolean paused = true;
    int score = 0;
    int lives = 3;
    SharedPreferences pref;

    //|-----------------------|//
    // CONSTRUCTEUR DE LA VIEW //
    //|-----------------------|//

    public SurfaceViewThread(Context context) {
        super(context);

        // Get SurfaceHolder object.
        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);

        //Get width and height screen
        Display ecran = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        screenWidth= ecran.getWidth();
        screenHeight= ecran.getHeight();

        //Initialisation paddle, balle, briques
        paddle = new Paddle(screenWidth, screenHeight);
        cercle = new Cercle(screenWidth, screenHeight, 55, 15);
        BuildAWall();

        // Set the SurfaceView object at the top of View object.
        setZOrderOnTop(true);
    }




    //|-----------------------------------------|//
    // METHODE COLLISION ENTRE CERCLE ET BRIQUES //
    //|-----------------------------------------|//

    public boolean collisionBrique(Cercle c,Brique b){
        nearestX = (int)Math.max(b.getRect().left,Math.min(c.getX(),b.getRect().right));
        nearestY = (int)Math.max(b.getRect().top,Math.min(c.getY(),b.getRect().bottom));

        CdistX = c.getX() - nearestX;
        CdistY = c.getY() - nearestY;

        return(CdistX * CdistX + CdistY * CdistY) < (c.getRayon() * c.getRayon());
    }




    //|------------------------------------------|//
    // METHODE COLLISION ENTRE CERCLE ET RAQUETTE //
    //|------------------------------------------|//

    public boolean collisionPaddle(Cercle c,Paddle p){
        nearestX = (int)Math.max(p.getRect().left,Math.min(c.getX(),p.getRect().right));
        nearestY = (int)Math.max(p.getRect().top,Math.min(c.getY(),p.getRect().bottom));

        CdistX = c.getX() - nearestX;
        CdistY = c.getY() - nearestY;

        return(CdistX * CdistX + CdistY * CdistY) < (c.getRayon() * c.getRayon());
    }



    //|-----------------------------------------------------|//
    // METHODE COLLISION CHANGEMENT D'ANGLE GAUCHE ET DROITE //
    //|-----------------------------------------------------|//

    public void collisionGaucheDroite(Cercle c,Brique b){
        if(c.getX() + c.getXSpeed() < b.getRect().left || c.getX() + c.getXSpeed() > (b.getRect().right - c.getRayon())){
            c.reverseXVelocity();
        }else c.reverseYVelocity();
    }




    //|----------------------|//
    // METHODE DE MISE A JOUR //
    //|----------------------|//

    public void update() {
        //Mise à jour de la raquette et de la balle
        paddle.update(screenWidth);
        cercle.move(fps);

        // Check for ball colliding with a brick
        for (int i = 0; i < nbBricks; i++) {
            if (bricks[i].getVisibility() && collisionBrique(cercle,bricks[i])) {
                if(bricks[i].getRes() > 0) {
                    bricks[i].setRes();
                    collisionGaucheDroite(cercle,bricks[i]);
                }else{
                    bricks[i].setInvisible();
                    bricks[i].setRes();
                    collisionGaucheDroite(cercle,bricks[i]);
                    score+=1;
                    if (score==6*8) {
                        paused = true;
                        BuildAWall();
                    }
                }
            }
        }
        // Check for ball colliding with paddle
        if (collisionPaddle(cercle, paddle)) {
            cercle.setRandomXVelocity();
            cercle.reverseYVelocity();
            cercle.clearObstacleY((int)paddle.getRect().top - 2);
        }

        //Check for ball colliding with screen
        if (cercle.getX() + cercle.getXSpeed() < cercle.getRayon() ) {
            cercle.reverseXVelocity();
        }
        if (cercle.getY() + cercle.getYSpeed() < brickHeight*2) {
            cercle.reverseYVelocity();
        }
        if (cercle.getX() + cercle.getXSpeed() > (screenWidth - cercle.getRayon())) {
            cercle.reverseXVelocity();
        }
        if (cercle.getY() + cercle.getYSpeed() > (screenHeight- cercle.getRayon())) {
            cercle.reverseYVelocity();
            lives--;

            // Restart game
            if (lives == 0) {
                paused = true;
                BuildAWall();
            }
        }
    }




    //|---------------------------|//
    // METHODE CONSTRUCTION DU MUR //
    //|---------------------------|//

    public void BuildAWall() {
        // Largeur et longueur des briques
        brickWidth = screenWidth / 8;
        brickHeight = screenHeight / 15;

        // En cas de reset du jeu
        cercle.reset(screenWidth+230, screenHeight-cercle.getRayon());

        // Build a wall of bricks
        nbBricks = 0;
        for (int column = 0; column < 8; column++) {
            for (int row = 3; row < 9; row++) {
                bricks[nbBricks] = new Brique(column, row, brickWidth, brickHeight);
                nbBricks++;
            }
        }
        // Restart game
        if (lives == 0 || score==6*8) {
            Boolean Lose;
            if(score==6*8) Lose = false;
            else Lose = true;
            pref = getContext().getSharedPreferences("PlayerScore", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("LatestScore", score);
            editor.apply();
            score = 0;
            lives = 3;
            Intent ScoreActivity = new Intent(getContext(), Scoreboard.class);
            ScoreActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ScoreActivity.putExtra("Lose", Lose);
            getContext().startActivity(ScoreActivity);
        }
    }




    //|---------------------------|//
    // METHODE RUN POUR LE THREAD  //
    //|---------------------------|//

    @Override
    public void run() {
        //Initialisation des briques
        BuildAWall();

        while(threadRunning) {
            if (!paused) {
                update();
            }
            draw();
            try {
                Thread.sleep(fps);
            }catch (InterruptedException ex) {}
        }
    }




    //|----------------------------------|//
    // METHODE DESSINE SUR LA SURFACEVIEW //
    //|----------------------------------|//

    private void draw() {
        int left = 0;
        int top = 0;
        int right = screenWidth;
        int bottom = screenHeight;
        Rect fond = new Rect(left, top, right, bottom);
        Rect fond2 = new Rect(left, top, right, (int)(bottom/8));
        Rect fond3 = new Rect(left, bottom-(bottom/12), right, bottom);

        if(surfaceHolder.getSurface().isValid()) {
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
            if(score < 10){
                canvas.drawText(String.valueOf("0"+score), (int)(screenWidth / 2.4), screenHeight / 10, paint);
            }else canvas.drawText(String.valueOf(score), (int)(screenWidth / 2.4), screenHeight / 10, paint);

            // Draw life
            if(lives == 1){
                paint.setTextSize(90);
                paint.setColor(Color.RED);
                canvas.drawText(String.valueOf(lives), screenWidth - 150, screenHeight / 11, paint);
            }else{
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
            for (int i = 0; i < nbBricks; i++) {
                if (bricks[i].getVisibility()) {
                    bricks[i].draw(canvas);
                }
            }

            // Draw the ball
            cercle.draw(canvas);

            // Send message to main UI thread to update the drawing to the main view special area.
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }




    //|---------------------------------------|//
    // METHODE DETECTION DE TOUCHER DE L'ECRAN //
    //|---------------------------------------|//
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:
                paused = false;
                if (motionEvent.getX() > screenWidth / 2) {
                    paddle.setMovementState(paddle.RIGHT);
                }else{
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
    public void pause() {
        threadRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }




    //|-----------------------------------|//
    // METHODE START POUR LA GAME_ACTIVITY //
    //|-----------------------------------|//
    public void start() {
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
    public void surfaceCreated(SurfaceHolder holder) {
        // Start the game !
        start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Set threadrunning flag to false when Surface is destroyed.
        // Then the thread will jump out the while loop and complete.
        pause();
    }
}