package com.nullpointerexception.retrogames.Components;
import android.os.Handler;

    /**
     * Permette di controllare se il bottone è stato premuto in un tempo specifico
     */
    public class Blocker {
        private static final int DEFAULT_BLOCK_TIME = 1000;
        private boolean mIsBlockClick;

        /**
         * Blocca ogni evento accaduto in x millisecondi per prevenire azioni di spam

         * @param blockInMillis: tempo di controllo
         * @return false se non in stato bloccato, altrimenti restituisce true.
         */
        public boolean block(int blockInMillis) {
            if (!mIsBlockClick) {
                mIsBlockClick = true;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsBlockClick = false;
                    }
                }, blockInMillis);
                return false;
            }
            return true;
        }

        public boolean block() {
            return block(DEFAULT_BLOCK_TIME);
        }
    }
