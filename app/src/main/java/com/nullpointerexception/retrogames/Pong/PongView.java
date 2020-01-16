package com.nullpointerexception.retrogames.Pong;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

/**
 * A simple MainActivityPong game.
 */
public class PongView extends SurfaceView implements SurfaceHolder.Callback {

    private PongThread mGameThread;
    private TextView mStatusView;
    private TextView mScoreView;
    private boolean moving;
    private float mLastTouchY;

    /**
     * Crea la view di pong
     * @param context contesto
     * @param attributeSet
     */
    public PongView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        //Riceve il messaggio dall'handler per la statusView
        mGameThread = new PongThread(holder, context,
                new Handler() {
                    @Override
                    public void handleMessage(Message m) {
                        mStatusView.setVisibility(m.getData().getInt("vis"));
                        mStatusView.setText(m.getData().getString("text"));
                    }
                },
                new Handler() {
                    @Override
                    public void handleMessage(Message m) {
                        mScoreView.setText(m.getData().getString("text"));
                    }
                }
        );

        setFocusable(true);
    }

    /**
     * setta lo stato della partita
     * (win,lose,pause)
     * @param textView textview
     */
    public void setStatusView(TextView textView) {
        mStatusView = textView;
    }

    /**
     * setta lo score
     * @param textView textview
     */
    public void setScoreView(TextView textView) {
        mScoreView = textView;
    }

    public PongThread getGameThread() {
        return mGameThread;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) {
            mGameThread.pause();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mGameThread.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mGameThread.setRunning(true);
        mGameThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        mGameThread.setRunning(false);
        while (retry) {
            try {
                mGameThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // don't care
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mGameThread.isBetweenRounds()) {
                        // resume game
                        mGameThread.setState(PongThread.STATE_RUNNING);
                    } else {
                        // if (mGameThread.isTouchOnHumanPaddle(event)) {
                        moving = true;
                        mLastTouchY = event.getY();
                        //  }
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (moving) {
                        float y1 = event.getY();
                        float dy = y1 - mLastTouchY;
                        mLastTouchY = y1;
                        mGameThread.moveHumanPaddle(dy);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                        moving = false;
                        break;

            }
        return true;
    }



}
