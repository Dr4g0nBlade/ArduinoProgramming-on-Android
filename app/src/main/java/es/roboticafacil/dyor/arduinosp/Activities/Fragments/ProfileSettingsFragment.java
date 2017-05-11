package es.roboticafacil.dyor.arduinosp.Activities.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;

import es.roboticafacil.dyor.arduinosp.Activities.MainActivity;
import es.roboticafacil.dyor.arduinosp.Activities.SetupArduino;
import es.roboticafacil.dyor.arduinosp.Activities.Tabs.ProfileSettings;
import es.roboticafacil.dyor.arduinosp.Activities.Tabs.Social;
import es.roboticafacil.dyor.arduinosp.R;

/**
 * Created by Dragos Dunareanu on 10-May-17.
 */

public class ProfileSettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.list_actions);

        Preference configComps = findPreference("intent_arduino_setup");
        configComps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), SetupArduino.class);
                startActivity(intent);
                return false;
            }
        });
    }
}
