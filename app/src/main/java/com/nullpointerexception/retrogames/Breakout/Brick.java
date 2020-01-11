package com.nullpointerexception.retrogames.Breakout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.nullpointerexception.retrogames.R;


public class Brick
{
    private static Context context;

    private int x,y,width,height,resistance, oldResistance;
    private RectF rect;
    private boolean isVisible;
    private int padding = 5;
    private int r = (int) (Math.random()*255);
    private int v = (int) (Math.random()*255);
    private int b = (int) (Math.random()*255);
    private boolean firstDraw = true;
    private Bitmap bitmap, decoded;
    private int res;

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
    public void setRes(){
        oldResistance = this.resistance;
        this.resistance -= 1;
    }
    public void setY(int y){this.y = y;}
    public void setWidth(int width){this.width = width;}
    public void setHeight(int height){this.height = height;}
    public void setPadding(int p){this.padding = p;}
    public void setVisibility(boolean v){this.isVisible = v;}
    public void setInvisible(){
        this.isVisible = false;
    }

    public static void setContext(Context ctx) { context = ctx; }

    public static Context getContext() { return context; }

    public void draw(Canvas c)
    {
        if ((oldResistance > resistance) || oldResistance == 0) {
            if (this.resistance == 2 && res != R.drawable.cell4) {
                res    = R.drawable.cell4;
                bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), res), (int) rect.width(), (int) rect.height(), false);
                bitmap.setConfig(Bitmap.Config.ARGB_8888);
                Paint pi = new Paint();
                c.drawBitmap(bitmap, rect.left, rect.top, pi);
            } else if(this.resistance == 2) {
                Paint pi = new Paint();
                c.drawBitmap(bitmap, rect.left, rect.top, pi);
            } else if (this.resistance == 1 && res != R.drawable.cell2) {
                res    = R.drawable.cell2;
                bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), res), (int) rect.width(), (int) rect.height(), false);
                Paint pi = new Paint();
                c.drawBitmap(bitmap, rect.left, rect.top, pi);
                bitmap.setConfig(Bitmap.Config.ARGB_8888);
            } else if (this.resistance == 1) {
                Paint pi = new Paint();
                c.drawBitmap(bitmap, rect.left, rect.top, pi);
            } else if (this.resistance == 0 && res != R.drawable.cell1) {
                res    = R.drawable.cell1;
                bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), res), (int) rect.width(), (int) rect.height(), false);
                bitmap.setConfig(Bitmap.Config.ARGB_8888);
                Paint pi = new Paint();
                c.drawBitmap(bitmap, rect.left, rect.top, pi);
            } else {
                Paint pi = new Paint();
                c.drawBitmap(bitmap, rect.left, rect.top, pi);
            }
        }
    }
        
}

