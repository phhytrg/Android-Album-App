package com.example.album;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class CustomizeFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.custom_preferences);
    }
}
