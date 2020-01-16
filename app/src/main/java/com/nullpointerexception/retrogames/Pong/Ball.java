package com.nullpointerexception.retrogames.Pong;

import android.graphics.Paint;

class Ball {

    /*
        posizione palla
    */
    float cx;
    float cy;
    float dx;
    float dy;
    /*
        angolo
    */
    int radius;
    /*
        stile della palla
    */
    Paint paint;

    /**
     *
     * @param radius
     * @param paint
     */
    Ball(int radius, Paint paint) {
        this.radius = radius;
        this.paint = paint;
    }

}
