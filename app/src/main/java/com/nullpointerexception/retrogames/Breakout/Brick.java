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
    public static final int GREEN = 0;
    public static final int BLUE = 1;
    public static final int RED = 2;

    private static Context context;

    private int x,y,width,height,resistance, oldResistance;
    private RectF rect;
    private boolean isVisible;
    private int padding = 5;
    private int r = (int) (Math.random() * 255);
    private int v = (int) (Math.random() * 255);
    private int b = (int) (Math.random() * 255);
    private Bitmap bitmap;
    private int res;
    private Paint p;
    private int color = GREEN;

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
        p = new Paint();
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

    public int getColor()
    {
        return color;
    }

    public void setColor(int color)
    {
        this.color = color;
    }

    public static void setContext(Context ctx) { context = ctx; }

    public static Context getContext() { return context; }

    public void draw(Canvas c)
    {
        if (this.resistance == 2)
        {
            if(res != getResourceForResistance(2))
            {
                res    = getResourceForResistance(2);
                bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), res), (int) rect.width(), (int) rect.height(), false);
                bitmap.setConfig(Bitmap.Config.ARGB_8888);
            }

            c.drawBitmap(bitmap, rect.left, rect.top, p);
        }
        else if (this.resistance == 1)
        {
            if(res != getResourceForResistance(1))
            {
                res    = getResourceForResistance(1);
                bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), res), (int) rect.width(), (int) rect.height(), false);
                bitmap.setConfig(Bitmap.Config.ARGB_8888);
            }

            c.drawBitmap(bitmap, rect.left, rect.top, p);
        }
        else if (this.resistance == 0)
        {
            if(res != getResourceForResistance(0))
            {
                res    = getResourceForResistance(0);
                bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), res), (int) rect.width(), (int) rect.height(), false);
                bitmap.setConfig(Bitmap.Config.ARGB_8888);
            }

            c.drawBitmap(bitmap, rect.left, rect.top, p);
        }
        else
            c.drawBitmap(bitmap, rect.left, rect.top, p);
    }

    private int getResourceForResistance(int resistance)
    {
        int res = 0;

        switch (color)
        {
            default:
            case GREEN:

                switch (resistance)
                {
                    case 2: res = R.drawable.brick_green; break;
                    case 1: res = R.drawable.brick_green2; break;
                    case 0: res = R.drawable.brick_green3; break;
                }

                break;

            case BLUE:

                switch (resistance)
                {
                    case 2: res = R.drawable.brick_blue; break;
                    case 1: res = R.drawable.brick_blue2; break;
                    case 0: res = R.drawable.brick_blue3; break;
                }

                break;

            case RED:

                switch (resistance)
                {
                    case 2: res = R.drawable.brick_red; break;
                    case 1: res = R.drawable.brick_red2; break;
                    case 0: res = R.drawable.brick_red3; break;
                }

                break;
        }

        return res;
    }

}

