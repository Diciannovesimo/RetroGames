package com.nullpointerexception.retrogames.Pong;

import android.graphics.Paint;
import android.graphics.RectF;

class Player {

    /*
    grandezza dei rettangoli
     */
    int paddleWidth;
    int paddleHeight;
    /*
    stile dei rettangoli
     */
    Paint paint;
    int score;
    RectF bounds;
    int collision;

    /**
     *
     * @param paddleWidth
     * @param paddleHeight
     * @param paint
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
