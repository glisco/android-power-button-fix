package it.glisco.powerbuttonbroken;


import android.app.IntentService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class LogService extends IntentService implements SensorEventListener {

    Sensor proxSensor;
    SensorManager sm;

    public LogService() {
        super("LogService");


    }


    @Override
    protected void onHandleIntent(Intent i) {

        sm=(SensorManager) LogService.this.getSystemService(SENSOR_SERVICE);
        proxSensor=sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sm.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.i("sm",sm.toString());
        Log.i("Tocca il sensore","---");


        /*
        int n = 0;
        while (true) {
            Log.i("PROVA SERVICE", "Evento n." + n++);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
            }
        }
        */
    }

    @Override
    public void onDestroy() {
        Log.i("PROVA SERVICE", "Distruzione Service");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.i("PROVA SERVICE", "onSensorChanged");

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i("PROVA SERVICE", "onAccuracyChanged");

    }


}
