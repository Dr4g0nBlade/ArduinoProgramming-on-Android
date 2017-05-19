package es.roboticafacil.dyor.arduinosp.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;

import es.roboticafacil.dyor.arduinosp.R;

public class SetUpArduinp extends Activity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_pins);
        getFragmentManager().beginTransaction().replace(R.id.placeholder, new PrefFrag()).commit();

    }

    public static class PrefFrag extends PreferenceFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.arduino_pins);

            PreferenceScreen preferenceScreen = this.getPreferenceScreen();

        }
    }
}
