package ir.lotfaliei.timer;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Locale;
import java.util.Timer;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, "Alarm! Wake up! Wake up!", Toast.LENGTH_LONG).show();
//        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        if (alarmUri == null)
//        {
//            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        }
//        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
//        ringtone.play();

        TimerController timerController = TimerController.getInstance();
        int alarmTime = timerController.removeFromAlarmQueue();
        String speechText;
        if (alarmTime == 0)
            speechText = "Time is up!";
        else
            speechText = String.format(Locale.ENGLISH, "%d minutes left!", alarmTime / 60);

        Log.i(getClass().getName(), "onReceive: " + speechText);

        timerController.setNextAlarm();
        SpeechController.getInstance().initQueue(speechText);
    }
}