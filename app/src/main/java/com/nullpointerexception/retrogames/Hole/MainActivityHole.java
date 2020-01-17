package com.nullpointerexception.retrogames.Hole;


import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.R;


public class MainActivityHole extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private CanvasView contentView;
    private TextView textViewTopScore, textViewScore, textViewLife;
    private long topScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_hole);

        textViewTopScore = findViewById(R.id.textViewTopScore);
        textViewScore = findViewById(R.id.textViewScore);
        textViewLife = findViewById(R.id.textViewLife);

        contentView = findViewById(R.id.fullscreen_content);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        SharedPreferences prefs = getSharedPreferences(App.APP_VARIABLES, MODE_PRIVATE);
        if(prefs.getBoolean(App.HOLE_TUTORIAL, true))
        {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.hole_welcome)
                    .setMessage(R.string.hole_tutorial)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dialog_ok, (a, b) ->
                            prefs.edit().putBoolean(App.HOLE_TUTORIAL, false).apply())
                    .show();
        }



        //Prendo il topscore dal database locale
        if(App.scoreboardDao.getGame(App.HOLE) != null) //Controllo se già esiste un topscore
            //Esiste già un topscore
            topScore = App.scoreboardDao.getScore(App.HOLE); //Leggo il vecchio topscore
        else
            //Non esiste un topscore
            topScore = 0;

        textViewTopScore.setText(getResources().getString(R.string.high_score_) + topScore);
        textViewScore.setText(getResources().getString(R.string.score_0));
        textViewLife.setText(getResources().getString(R.string.life_3));




        contentView.setOnChangeScoreListener(new CanvasView.OnChangeScoreListener() {
            @Override
            public void onChangeScore(long score, int life) {
                if(score > topScore)
                {
                    topScore = score;
                    textViewTopScore.setText(getResources().getString(R.string.high_score) + topScore);
                }
                textViewScore.setText(getResources().getString(R.string.score) + score);

                String itemsFound = getResources().getQuantityString(R.plurals.life, life);
                textViewLife.setText(itemsFound+ life);
            }
        });

    }


    @Override
    protected void onResume()
    {
        super.onResume();
        // Registrare questa classe come listener per il sensore accelerometro
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onStop() {
        // Annulla la registrazione del listener
        sensorManager.unregisterListener(this);
        super.onStop();
    }

    /**
     * Acquisisce i dati dal sensore accelerometro
     * @param sensorEvent evento del sensore
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];    //coordinata x
            float y = sensorEvent.values[1];    //coordinata y
            contentView.changeVelocity(y, x); //Invia i dati
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


}
