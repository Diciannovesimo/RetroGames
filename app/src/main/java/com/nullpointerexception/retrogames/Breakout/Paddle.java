package com.nullpointerexception.retrogames.Breakout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.nullpointerexception.retrogames.R;

class Paddle
{
    private RectF rect; //rettangolo della barra
    private int x;      //posizione della barra sull'asse x
    private int length; //Lunghezza
    private int height; //Altezza
    private int paddleSpeed; //velocità

    // In che modo si può muovere
    final int STOPPED = 0;
    final int LEFT = 1;
    final int RIGHT = 2;

    private Bitmap bitmap;
    private int paddleMoving = STOPPED;

    /**
     * Costruttore del paddle
     * @param screenX  lunghezza del paddle
     * @param screenY larghezza del paddle
     * @param speed velocità del paddle
     */
    Paddle(int screenX, int screenY, int speed) {
        this.length = screenX / 4;
        this.height = 30;

        this.x = (screenX / 2) - (length / 2);
        int y = screenY - (screenY / 16);

        //Creazione del rettangolo del paddle
        this.rect = new RectF(this.x, y, this.x + this.length, y + this.height);

        this.paddleSpeed = speed;
    }

    /**
     * Imposta la direzione della barra, se sta andando a destra a sinistra o da nesusna parte
     * @param state stato della barra
     */
    void setMovementState(int state){
        this.paddleMoving = state;
    }

    /**
     * Aggiorna le coordinate durante il movimento
     * @param w posizione
     */
    void update(int w) {
        if(this.paddleMoving == this.LEFT && this.x > 0)
        {
            this.x -= this.paddleSpeed;
        }

        if(this.paddleMoving == this.RIGHT && this.x + this.length < w)
        {
            this.x += this.paddleSpeed;
        }

        this.rect.left = this.x;
        this.rect.right = this.x + this.length;
    }

    /**
     * Crea il bitmap del paddle
     * @param context contesto
     */
    void createPaddleDrawable(Context context) {
        bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.paddle), (int) rect.width(), (int) rect.height(), false);
        bitmap.setConfig(Bitmap.Config.ARGB_8888);
    }

    /**
     * Disegna il paddle
     * @param c canvas
     */
    void draw(Canvas c)
    {
        c.drawBitmap(bitmap, rect.left, rect.top, new Paint());
    }

    int getX() { return x; }

    void setX(int x) { this.x = x; }

    RectF getRect(){
        return this.rect;
    }

}
