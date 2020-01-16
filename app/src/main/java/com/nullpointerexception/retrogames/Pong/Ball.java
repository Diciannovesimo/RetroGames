package com.nullpointerexception.retrogames.Pong;

import android.graphics.Paint;

class Ball {

    Paint paint;    //stile della palla
    int radius;     //angolo della palla
    //posizione della palla
    float cx;
    float cy;
    float dx;
    float dy;

    /**
     *  Costruttore della palla
     * @param radius raggio della palla
     * @param paint stile della palla
     */
    Ball(int radius, Paint paint) {
        this.radius = radius;
        this.paint = paint;
    }

}
