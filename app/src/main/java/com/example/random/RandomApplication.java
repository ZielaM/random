package com.example.random;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

public class RandomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        
        int currentTheme = prefs.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(currentTheme);

        String currentLang = prefs.getString("lang", "");
        LocaleListCompat appLocale = currentLang.isEmpty() ? LocaleListCompat.getEmptyLocaleList() : LocaleListCompat.forLanguageTags(currentLang);
        AppCompatDelegate.setApplicationLocales(appLocale);
    }
}
