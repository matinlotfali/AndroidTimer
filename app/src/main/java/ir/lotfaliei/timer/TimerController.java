package ir.lotfaliei.timer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.ALARM_SERVICE;

abstract class TimerController {
    private final int RESTART_TIME = 60 * 60;
    private final int TEN_MIN = 10 * 60;
    private final int FIVE_MIN = 5 * 60;
    private final int SEC_TO_MILIS = 1000;

    private SpeechController speechController;
    private Timer timer;
    private int timerSeconds = RESTART_TIME;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private Integer[] alarms =
            {
                    TEN_MIN,
                    FIVE_MIN,
                    0
            };
    private LinkedList<Integer> alarmsQueue;

    private static TimerController instance;

    static TimerController getInstance() {
        return instance;
    }

    TimerController(Context context) {
        speechController = new SpeechController(context);

        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        restart();
        instance = this;
    }

    boolean getIsPaused() {
        return timer == null;
    }

    int getTimerSeconds() {
        return timerSeconds;
    }

    void restart() {
        timerSeconds = RESTART_TIME;
        alarmsQueue = new LinkedList<>(Arrays.asList(alarms));
    }

    int removeFromAlarmQueue() {
        return alarmsQueue.remove();
    }

    void setNextAlarm() {
        if (alarmsQueue.isEmpty())
            return;

        int a = alarmsQueue.element();
        int t = timerSeconds - a;
        Log.d(getClass().getName(), "Alarm " + Integer.toString(a) + " Sub:" + Integer.toString(t));
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + t * SEC_TO_MILIS, pendingIntent);
    }

    void resume() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timerSeconds--;
                //Log.d("TAG", "timer: " + timerSeconds);
                if (timerSeconds == 0) {
                    timer.cancel();
                    timer = null;
                }
                tick();
            }
        }, SEC_TO_MILIS, SEC_TO_MILIS);
        setNextAlarm();
    }

    void pause() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        alarmManager.cancel(pendingIntent);
    }

    void shutDown() {
        pause();
        speechController.shutDown();
        Log.i(getClass().getName(), "shutDown");
    }

    abstract void tick();
}
