package ir.lotfaliei.timer.controllers;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

import ir.lotfaliei.timer.R;

public class SettingsController {

    public static HashSet<String> toStringSet(String str) {
        HashSet<String> set = new HashSet<>();
        set.add(str);
        return set;
    }

    public static String fromStringSet(Set<String> set) {
        return (String) set.toArray()[0];
    }

    public static String getPreference(Context context, String key, int defaultValue) {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        String def = context.getString(defaultValue);
        return fromStringSet(p.getStringSet(key, toStringSet(def)));
    }

    public static String getPreference(Preference p, int defaultValue) {
        String def = p.getContext().getString(defaultValue);
        return fromStringSet(p.getPersistedStringSet(toStringSet(def)));
    }
}
