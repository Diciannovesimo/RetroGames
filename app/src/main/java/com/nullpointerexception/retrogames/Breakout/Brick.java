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
    //Colori del brick
    static final int GREEN = 0;
    static final int BLUE = 1;
    static final int RED = 2;

    static Context context;

    private int x,y,width,height,resistance, oldResistance;
    private RectF rect;
    private boolean isVisible;
    private int padding = 5;
    private Bitmap bitmap;
    private int res;
    private Paint p;
    private int color = GREEN;

    /**
     * Costruttore del blocco
     * @param x colonna
     * @param y riga
     * @param width larghezza
     * @param height peso
     */
    Brick(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.resistance = 2;

        //Creazione del rettangolo
        this.rect = new RectF(this.x * this.width + padding,
                this.y * this.height + padding,
                this.x * this.width + this.width - padding,
                this.y * this.height + this.height - padding);

        this.isVisible = true;
        p = new Paint();
    }

    int getX(){return this.x;}
    int getRes(){return this.resistance;}
    int getY(){return this.y;}
    int getWidth(){return this.width;}
    int getHeight(){return this.height;}
    int getPadding(){return this.padding;}
    RectF getRect(){
        return this.rect;
    }
    boolean getVisibility(){return this.isVisible;}

    void setX(int x){this.x = x;}
    void setRes(){
        oldResistance = this.resistance;
        this.resistance -= 1;
    }
    void setY(int y){this.y = y;}
    void setWidth(int width){this.width = width;}
    void setHeight(int height){this.height = height;}
    void setPadding(int p){this.padding = p;}
    void setVisibility(boolean v){this.isVisible = v;}
    void setInvisible(){
        this.isVisible = false;
    }

    int getColor()
    {
        return color;
    }

    void setColor(int color)
    {
        this.color = color;
    }

    static void setContext(Context ctx) { context = ctx; }

    static Context getContext() { return context; }

    /**
     *  Disegna il blocco in base alla resistenza
     * @param c
     */
    void draw(Canvas c)
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

    /**
     * Restituisce il tipo di blocco da mostrare
     * @param resistance indice di resistenza del blocco
     * @return intero contenente la risorsa dell'immagine del blocco
     */
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

