package ir.lotfaliei.timer.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import java.util.Locale;

import ir.lotfaliei.timer.controllers.SettingsController;
import ir.lotfaliei.timer.controllers.SpeechController;
import ir.lotfaliei.timer.views.TimePicker.MyTimePickerDialog;
import ir.lotfaliei.timer.R;
import ir.lotfaliei.timer.views.TimePicker.TimePicker;
import ir.lotfaliei.timer.controllers.TimerController;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            final Preference restart_time = findPreference("restart_time");
            final ListPreference voice = findPreference("voice");
            final SeekBarPreference speechRate = findPreference("speech-rate");
            final SeekBarPreference pitch = findPreference("pitch");
            final SpeechController speechController = SpeechController.getInstance();

            restart_time.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String str = SettingsController.getPreference(preference, R.string.restart_time_value);
                    String[] strSplit = str.split(":");
                    int min = Integer.parseInt(strSplit[0]);
                    int sec = Integer.parseInt(strSplit[1]);
                    MyTimePickerDialog dialog = new MyTimePickerDialog(preference.getContext(), new MyTimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int min, int sec) {
                            String str = String.format(Locale.ENGLISH, "%02d:%02d", min, sec);
                            restart_time.persistStringSet(SettingsController.toStringSet(str));
                            TimerController.getInstance().setRestartTime(min, sec);
                        }
                    }, min, sec);
                    dialog.show();
                    return true;
                }
            });

            String[] voices = speechController.getVoices();
            String[] vnames = new String[voices.length];
            for (int i = 0; i < voices.length; i++)
                vnames[i] = voices[i].substring(12).
                        replace('_', ' ').
                        replace('-', ' ');
            voice.setEntries(vnames);
            voice.setEntryValues(voices);
            voice.setDefaultValue(speechController.getDefaultVoice());
            voice.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    speechController.setVoice((String) newValue);
                    speechController.initQueue("New voice is set.");
                    return true;
                }
            });

            speechRate.setMin(1);
            float value = speechRate.getValue();
            value = ((int) value) / 10f;
            speechRate.setTitle("Speech Rate: " + value);
            speechRate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    float value = ((int) newValue) / 10f;
                    speechController.setSpeechRate(value);
                    speechController.initQueue("Speech rate is set.");
                    speechRate.setTitle("Speech Rate: " + value);
                    return true;
                }
            });

            pitch.setMin(1);
            value = pitch.getValue();
            value = ((int) value) / 10f;
            pitch.setTitle("Pitch: " + value);
            pitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    float value = ((int) newValue) / 10f;
                    speechController.setPitch(value);
                    speechController.initQueue("Pitch is set.");
                    pitch.setTitle("Pitch: " + value);
                    return true;
                }
            });
        }
    }
}