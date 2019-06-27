package ir.lotfaliei.timer.controllers;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class SpeechController implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private boolean isLoaded;

    private static SpeechController instance;
    static SpeechController getInstance() {
        return instance;
    }

    SpeechController(Context context) {
        tts = new TextToSpeech(context, this);
        instance = this;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.US);
            tts.setSpeechRate(0.8f);
            isLoaded = true;

        } else {
            Log.e(getClass().getName(), "Initialization Failed!");
        }
    }

    void shutDown() {
        tts.shutdown();
    }

    void initQueue(String text) {
        if (isLoaded)
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        else
            Log.e(getClass().getName(), "TTS Not Initialized");
    }
}
