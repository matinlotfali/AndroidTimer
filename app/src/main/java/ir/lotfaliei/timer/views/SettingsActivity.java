package ir.lotfaliei.timer.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Locale;

import ir.lotfaliei.timer.controllers.SettingsController;
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

    public static class SettingsFragment extends PreferenceFragmentCompat implements MyTimePickerDialog.OnTimeSetListener {
        private Preference restart_time;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            restart_time = findPreference("restart_time");

            restart_time.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showTimeDialog();
                    return true;
                }
            });
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int min, int sec) {
            String str = String.format(Locale.ENGLISH, "%02d:%02d", min, sec);
            restart_time.persistStringSet(SettingsController.toStringSet(str));
            TimerController.getInstance().setRestartTime(min, sec);
        }

        private void showTimeDialog() {
            // Use the current date as the default date in the picker
            String str = SettingsController.getPreference(restart_time, R.string.restart_time_value);
            String[] strSplit = str.split(":");
            int min = Integer.parseInt(strSplit[0]);
            int sec = Integer.parseInt(strSplit[1]);
            MyTimePickerDialog dialog = new MyTimePickerDialog(restart_time.getContext(), this, min, sec);
            dialog.show();
        }
    }
}