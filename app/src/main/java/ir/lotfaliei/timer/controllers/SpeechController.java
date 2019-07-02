package ir.lotfaliei.timer.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

public class SpeechController implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private boolean isLoaded;
    private Context context;

    private static SpeechController instance;

    public static SpeechController getInstance() {
        return instance;
    }

    SpeechController(Context context) {
        tts = new TextToSpeech(context, this);
        this.context = context;
        instance = this;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.US);
            SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
            String voice = p.getString("voice", getDefaultVoice());
            setVoice(voice);

            int rate = p.getInt("speech-rate", 8);
            setSpeechRate(rate / 10f);

            int pitch = p.getInt("pitch", 10);
            setPitch(pitch / 10f);
            isLoaded = true;

        } else {
            Log.e(getClass().getName(), "Initialization Failed!");
        }
    }

    void shutDown() {
        tts.shutdown();
    }

    public void initQueue(String text) {
        if (isLoaded) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
            Log.i(getClass().getName(), "initQueue: " + text);
        } else
            Log.e(getClass().getName(), "TTS Not Initialized");
    }

    public String[] getVoices() {
        Object[] voices = tts.getVoices().toArray();
        ArrayList<String> voices_str = new ArrayList<String>();
        for (int i = 0; i < voices.length; i++) {
            Voice v = (Voice) voices[i];
            if (v.getName().contains("en-us"))
                voices_str.add(v.getName());
        }
        String[] r = new String[voices_str.size()];
        r = voices_str.toArray(r);
        return r;
    }

    public String getDefaultVoice() {
        return tts.getDefaultVoice().getName();
    }

    public void setVoice(String voice) {
        Object[] voices = tts.getVoices().toArray();
        for (int i = 0; i < voices.length; i++) {
            Voice v = (Voice) voices[i];
            if (v.getName().equals(voice)) {
                tts.setVoice(v);
                break;
            }
        }
    }

    public void setSpeechRate(float rate) {
        tts.setSpeechRate(rate);
    }

    public void setPitch(float pitch) {
        tts.setPitch(pitch);
    }
}
