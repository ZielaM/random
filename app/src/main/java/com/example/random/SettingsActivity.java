package com.example.random;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        prefs = getSharedPreferences("settings", MODE_PRIVATE);

        Spinner spinnerLanguage = findViewById(R.id.spinnerLanguage);
        Spinner spinnerTheme = findViewById(R.id.spinnerTheme);

        String[] langs = {getString(R.string.lang_system), getString(R.string.lang_en), getString(R.string.lang_pl)};
        String[] langCodes = {"", "en", "pl"};
        
        String[] themes = {getString(R.string.theme_system), getString(R.string.theme_light), getString(R.string.theme_dark)};
        int[] themeValues = {AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, AppCompatDelegate.MODE_NIGHT_NO, AppCompatDelegate.MODE_NIGHT_YES};

        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, langs);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(langAdapter);

        ArrayAdapter<String> themeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, themes);
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheme.setAdapter(themeAdapter);

        // Load selections
        String currentLang = prefs.getString("lang", "");
        for (int i = 0; i < langCodes.length; i++) {
            if (langCodes[i].equals(currentLang)) {
                spinnerLanguage.setSelection(i);
                break;
            }
        }

        int currentTheme = prefs.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        for (int i = 0; i < themeValues.length; i++) {
            if (themeValues[i] == currentTheme) {
                spinnerTheme.setSelection(i);
                break;
            }
        }

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String code = langCodes[position];
                if (!code.equals(prefs.getString("lang", ""))) {
                    prefs.edit().putString("lang", code).apply();
                    LocaleListCompat appLocale = code.isEmpty() ? LocaleListCompat.getEmptyLocaleList() : LocaleListCompat.forLanguageTags(code);
                    AppCompatDelegate.setApplicationLocales(appLocale);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int mode = themeValues[position];
                if (mode != prefs.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)) {
                    prefs.edit().putInt("theme", mode).apply();
                    AppCompatDelegate.setDefaultNightMode(mode);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        com.google.android.material.materialswitch.MaterialSwitch switchAnimations = findViewById(R.id.switchAnimations);
        switchAnimations.setChecked(prefs.getBoolean("animations", true));
        switchAnimations.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("animations", isChecked).apply();
        });
    }
}
