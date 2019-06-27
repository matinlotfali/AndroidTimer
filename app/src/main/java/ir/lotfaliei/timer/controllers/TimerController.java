package ir.lotfaliei.timer.controllers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import ir.lotfaliei.timer.R;

import static android.content.Context.ALARM_SERVICE;

public abstract class TimerController {
    private final int SEC_TO_MILIS = 1000;

    private SpeechController speechController;
    private Timer timer;
    private int timerSeconds;
    private int restartTime;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private Integer[] alarms =
            {
                    45 * 60,
                    30 * 60,
                    15 * 60,
                    5 * 60,
                    0
            };
    private LinkedList<Integer> alarmsQueue;

    private static TimerController instance;

    public static TimerController getInstance() {
        return instance;
    }

    public TimerController(Context context) {
        speechController = new SpeechController(context);

        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        String str = SettingsController.getPreference(context, "restart_time", R.string.restart_time_value);
        setRestartTime(str);

        restart();
        instance = this;
    }

    public void setRestartTime(int min, int sec) {
        restartTime = min * 60 + sec;
    }

    public void setRestartTime(String time) {
        String[] strSplit = time.split(":");
        int min = Integer.parseInt(strSplit[0]);
        int sec = Integer.parseInt(strSplit[1]);
        setRestartTime(min, sec);
    }

    public boolean getIsPaused() {
        return timer == null;
    }

    public int getTimerSeconds() {
        return timerSeconds;
    }

    public void restart() {
        timerSeconds = restartTime;
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

    public void resume() {
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

    public void pause() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        alarmManager.cancel(pendingIntent);
    }

    public void shutDown() {
        pause();
        speechController.shutDown();
        Log.i(getClass().getName(), "shutDown");
    }

    protected abstract void tick();
}
