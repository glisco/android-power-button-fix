package it.glisco.powerbuttonbroken;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager.WakeLock;
import android.os.PowerManager;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import android.view.ViewConfiguration;


public class SensorService extends Service implements SensorEventListener {
    public static final String TAG = SensorService.class.getName();
    public static final int SCREEN_OFF_RECEIVER_DELAY = 500;

    private SensorManager mSensorManager = null;
    private WakeLock mWakeLock = null;
    private long tap1=0;

    /*
     * Register this as a sensor event listener.
     */
    private void registerListener() {
        Log.i(TAG, "registerListener().");


        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                SensorManager.SENSOR_DELAY_NORMAL);

        /*
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
         */
        /*
        proxSensor=sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sm.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);
        */

    }

    /*
     * Un-register this as a sensor event listener.
     */
    private void unregisterListener() {
        Log.i(TAG, "unregisterListener().");

        mSensorManager.unregisterListener(this);
    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive("+intent+")");

            if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                return;
            }

            Runnable runnable = new Runnable() {
                public void run() {
                    Log.i(TAG, "Runnable executing.");
                    unregisterListener();
                    registerListener();
                }
            };

            new Handler().postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
        }
    };

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG, "onAccuracyChanged().");
    }

    public void onSensorChanged(SensorEvent event) {
        Log.i(TAG, "onSensorChanged().");

        if (tap1 == 0) {
            Log.i(TAG, "First TAP");
            tap1 = SystemClock.elapsedRealtime();
        } else {
            if (SystemClock.elapsedRealtime() - tap1 <= ViewConfiguration.getDoubleTapTimeout()) {
                Log.i(TAG, "Second TAP!");
                tap1 = 0;
                /*
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                */


                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
                wakeLock.acquire();
                Log.i(TAG, "wakeLock acquire");
                wakeLock.release();
                Log.i(TAG, "wakeLock release");


            } else {
                tap1 = 0;
                Log.i(TAG, "Deleted TAP");
            }
        }





        tap1=SystemClock.elapsedRealtime();
        ViewConfiguration.getDoubleTapTimeout();

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate().");


        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        PowerManager manager =
                (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy().");

        unregisterReceiver(mReceiver);
        unregisterListener();
        mWakeLock.release();
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "onStartCommand().");
        tap1=0;


        startForeground(Process.myPid(), buildForegroundNotification());
        registerListener();
        mWakeLock.acquire();

        return START_STICKY;
    }


    private Notification buildForegroundNotification() {

        Intent resultIntent = new Intent(this, MyLockScreenActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        Intent doLockIntent = new Intent(this, doLockReceiver.class);
        PendingIntent doLockPendingIntent = PendingIntent.getBroadcast(this, 0, doLockIntent, PendingIntent.FLAG_UPDATE_CURRENT);



        Intent powerOffIntent = new Intent(this, doPowerOffReceiver.class);
        PendingIntent powerOffPendingIntent = PendingIntent.getBroadcast(this, 0, powerOffIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Notification.Builder b=new Notification.Builder(this);
        b.setContentIntent(resultPendingIntent);

        b.setOngoing(true);

        b.setContentTitle(getString(R.string.app_name))
                //.addAction(R.drawable.lock, "Lock", lockPendingIntent) // #0
                //.addAction(R.drawable.ic_launcher, "Power OFF", powerOffPendingIntent)  // #1
                //.addAction(R.drawable.restart, "Reboot", rebootPendingIntent)     // #2
                .addAction(R.drawable.lock,"Lock",doLockPendingIntent) // #0
                .addAction(R.drawable.ic_launcher_black_and_white, "PowerOFF", resultPendingIntent)  // #1
                //.addAction(R.drawable.restart, "Reboot", rebootPendingIntent)     // #2                     .setContentText("Tap here to switch off and lock your device")
                .setSmallIcon(R.drawable.lock)
                .setTicker("lock");

        return(b.build());
    }
    
}
