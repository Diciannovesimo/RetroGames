package com.nullpointerexception.retrogames.Breakout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

public class Cercle{
    int rayon,x,y;
    int speed = 0;
    int Xspeed = 0;
    int Yspeed = 0;
    private Color couleur;

    public Cercle(int x, int y, int r, int s) {
        super();
        this.x = x/2;
        this.y = y-200;
        this.rayon = r/2;
        this.Xspeed = s;
        this.Yspeed = s;
    }
    public int getSpeed(){return this.speed;}
    public int getXSpeed(){return this.Xspeed;}
    public int getYSpeed(){return this.Yspeed;}
    public int getX() {return this.x;}
    public int getY() {return this.y;}

    public void setSpeed(int s){this.speed +=s;}
    public void setXSpeed(int s){this.Xspeed +=s;}
    public void setYSpeed(int s){this.Yspeed +=s;}
    public void setX(int x) {this.x = x;}
    public void setY(int y) {this.y = y;}

    public int getRayon() {return this.rayon;}
    public Color getColor() {return this.couleur;}

    public void setRayon(int r) {this.rayon = r;}
    public void setColor(Color c) {this.couleur = c;}

    public void draw(Canvas c) {
        Paint p = new Paint();
        p.setColor(Color.RED);
        c.drawCircle(this.x, this.y, this.rayon, p);
    }
    public void reverseYVelocity(){ this.Yspeed = -this.Yspeed;}
    public void reverseXVelocity(){
        this.Xspeed = -this.Xspeed;
    }

    public void setRandomXVelocity(){
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if(answer == 0){
            reverseXVelocity();
        }
    }

    public void clearObstacleY(int y){this.y = (y - getRayon()*2);}
    public void clearObstacleX(int x){this.x = (x + getRayon()*2);}

    public void move(long fps){
        this.x += (Xspeed / fps);
        this.y += (Yspeed / fps);
    }

    public void reset(int x, int y){
        this.x = x / 2;
        this.y = y - 200;
    }
}