package com.nullpointerexception.retrogames.Breakout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.nullpointerexception.retrogames.R;

import java.util.Random;

public class Circle
{
    private int radius; //raggio della palla
    private int x; //posizione della palla sull'asse x
    private int y; //posizione della palla sull'asse y
    private int Xspeed; //velocità asse x
    private int Yspeed; //velocità asse y
    private Color color;

    private Bitmap ballBitmap;

    /**
     * Costruttore della palla
     * @param x posizione sull'asse x
     * @param y posizione sull'asse y
     * @param r raggio della palla
     * @param s velocità della palla
     */
    Circle(int x, int y, int r, int s) {
        super();
        this.x = x / 2;
        this.y = y - 200;
        this.radius = r / 2;
        this.Xspeed = s;
        this.Yspeed = s;
    }

    /**
     * Setta il bitmap della palla
     * @param context contesto
     */
    void setBall(Context context)
    {
        ballBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.breakout_ball), radius*2, radius*2, false);
        ballBitmap.setConfig(Bitmap.Config.ARGB_8888);
    }

    /**
     * Disegna la palla
     * @param c oggetto di tipo canvas
     */
    void draw(Canvas c)
    {
        if(ballBitmap != null)
            c.drawBitmap(ballBitmap, x, y, new Paint());
        else
        {   //Se il bitmap non esiste genera una palla rossa
            Paint p = new Paint();
            p.setColor(Color.RED);
            c.drawCircle(this.x, this.y, this.radius, p);
        }
    }

    /**
     * Setta in maniera random la velocità della palla
     */
    void setRandomXVelocity() {
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if(answer == 0){
            reverseXVelocity();
        }
    }

    /**
     * Elimina l'ostacolo per sorpassa la coordinata del blocco rotto
     * @param y posizione sull'asse y della palla
     */
    void clearObstacleY(int y){this.y = (y - getRadius()*2);}

    /**
     * Muove la palla
     * @param fps numero di fps
     */
    void move(long fps)
    {
        this.x += (Xspeed / fps);
        this.y += (Yspeed / fps);
    }

    /**
     * Resetta la posizione della palla
     * @param x coordinata asse x
     * @param y coordinata asse y
     */
    void reset(int x, int y){
        this.x = x / 2;
        this.y = y - 200;
    }

    void reverseYVelocity(){ this.Yspeed = -this.Yspeed;}

    void reverseXVelocity(){ this.Xspeed = -this.Xspeed;}

    int getXSpeed(){return this.Xspeed;}

    int getYSpeed(){return this.Yspeed;}

    int getX() {return this.x;}

    int getY() {return this.y;}

    void setX(int x) {this.x = x;}

    int getRadius() {return this.radius;}

    Color getColor() {return this.color;}

    void setColor(Color c) {this.color = c;}
}