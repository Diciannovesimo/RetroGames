package com.nullpointerexception.retrogames.Breakout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;


public class Brick
{
    private int x,y,width,height,resistance;
    private RectF rect;
    private boolean isVisible;
    private int padding = 5;
    private int r = (int)(Math.random()*255);
    private int v = (int)(Math.random()*255);
    private int b = (int)(Math.random()*255);

    public Brick(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.resistance = 2;

        this.rect = new RectF(this.x * this.width + padding,
                this.y * this.height + padding,
                this.x * this.width + this.width - padding,
                this.y * this.height + this.height - padding);

        this.isVisible = true;
    }

    public int getX(){return this.x;}
    public int getRes(){return this.resistance;}
    public int getY(){return this.y;}
    public int getWidth(){return this.width;}
    public int getHeight(){return this.height;}
    public int getPadding(){return this.padding;}
    public RectF getRect(){
        return this.rect;
    }
    public boolean getVisibility(){return this.isVisible;}

    public void setX(int x){this.x = x;}
    public void setRes(){this.resistance -= 1;}
    public void setY(int y){this.y = y;}
    public void setWidth(int width){this.width = width;}
    public void setHeight(int height){this.height = height;}
    public void setPadding(int p){this.padding = p;}
    public void setVisibility(boolean v){this.isVisible = v;}
    public void setInvisible(){
        this.isVisible = false;
    }

    public void draw(Canvas c){
        Paint p = new Paint();
        //int rgb = argb(255,r,v,b);
        if(this.resistance == 2) p.setColor(Color.GREEN);
        else if(this.resistance == 1) p.setColor(Color.YELLOW);
        else p.setColor(Color.RED);
        c.drawRect(rect,p);

        /*
        Bitmap bitmap = Bitmap.createScaledBitmap( BitmapFactory.decodeResource( getResources(), R.drawable.cell0), (int)rect.width(), (int)rect.height(), false);

        Paint pi = new Paint();
        c.drawBitmap(bitmap,rect.width(), rect.height(),pi);
         */

    }
}

