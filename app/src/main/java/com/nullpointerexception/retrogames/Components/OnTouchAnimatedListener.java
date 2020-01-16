package com.nullpointerexception.retrogames.Components;

import android.view.MotionEvent;
import android.view.View;

public abstract class OnTouchAnimatedListener implements View.OnTouchListener
{
    private float deltaScale = 0.03f;
    private float deltaAlpha = 0.15f;

    /**
     * Costruttore vuoto necessario per mantenere le variabili invariate
     */
    public OnTouchAnimatedListener() {}


    @Override
    public final boolean onTouch(View view, MotionEvent motionEvent)
    {
        switch (motionEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                view.setScaleX(1 - deltaScale);
                view.setScaleY(1 - deltaScale);
                view.setAlpha(1 - deltaAlpha);
                break;

            case MotionEvent.ACTION_UP:
                view.setScaleX(1f);
                view.setScaleY(1f);
                view.setAlpha(1f);
                view.performClick();
                onClick(view);
                break;

            case MotionEvent.ACTION_CANCEL:
                view.setScaleX(1f);
                view.setScaleY(1f);
                view.setAlpha(1f);
                break;
        }

        return false;
    }

    public abstract void onClick(View view);
}
