package com.nullpointerexception.retrogames.Pong;

import android.graphics.Paint;
import android.graphics.RectF;

class Player {

    int paddleWidth;    //larghezza del paddle
    int paddleHeight;   //altezza del paddle
    int collision;      //numero delle collisioni
    int score;          //punteggio
    //stile dei rettangoli
    Paint paint;   //stile dei rettangoli
    RectF bounds;  //limiti


    /**
     * Costruttore del player
     * @param paddleWidth larghezza del paddle
     * @param paddleHeight  altezza del paddle
     * @param paint stile del rettangolo
     */
    Player(int paddleWidth, int paddleHeight, Paint paint) {
        this.paddleWidth = paddleWidth;
        this.paddleHeight = paddleHeight;
        this.paint = paint;
        this.score = 0;
        this.bounds = new RectF(0, 0, paddleWidth, paddleHeight);
        this.collision = 0;
    }

}
