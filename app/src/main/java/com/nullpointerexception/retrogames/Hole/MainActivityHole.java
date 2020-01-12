package com.nullpointerexception.retrogames.Hole;


import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.nullpointerexception.retrogames.R;


public class MainActivityHole extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private CanvasView contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen_hole);

        contentView = findViewById(R.id.fullscreen_content);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
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
