package ir.lotfaliei.timer.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
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
            speechText = "Time is up.";
        else
            speechText = String.format(Locale.ENGLISH, "You have %d minutes left.", alarmTime / 60);

        timerController.setNextAlarm();
        SpeechController.getInstance().initQueue(speechText);
    }
}