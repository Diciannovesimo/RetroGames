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

    private int x,y,width,height,resistance;
    private RectF rect;
    private boolean isVisible;
    private int padding = 5;
    private int r = (int) (Math.random()*255);
    private int v = (int) (Math.random()*255);
    private int b = (int) (Math.random()*255);

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

    public static void setContext(Context ctx) { context = ctx; }

    public static Context getContext() { return context; }

    public void draw(Canvas c)
    {
        /*
        Paint p = new Paint();
        //int rgb = argb(255,r,v,b);

        if(this.resistance == 2)
            p.setColor(Color.GREEN);
        else if(this.resistance == 1)
            p.setColor(Color.YELLOW);
        else
            p.setColor(Color.RED);
        c.drawRect(rect,p);*/

        int res;

        if(this.resistance == 2)
            res = R.drawable.cell4;
        else if(this.resistance == 1)
            res = R.drawable.cell2;
        else
            res = R.drawable.cell1;

        Bitmap bitmap = Bitmap.createScaledBitmap( BitmapFactory.decodeResource(
                context.getResources(), res), (int) rect.width(), (int) rect.height(), false);

        Paint pi = new Paint();
        c.drawBitmap(bitmap, rect.left, rect.top, pi);
    }
}

