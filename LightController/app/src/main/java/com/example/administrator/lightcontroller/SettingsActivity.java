package com.example.administrator.lightcontroller;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();

        getFragmentManager().beginTransaction()
                .add(R.id.container, GeneralPreferenceFragment.newInstance())
                .commit();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    public static class GeneralPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        //private IconPreference color;

        private EditTextPreference etpServerHost;
        private EditTextPreference etpSitePath;

        public static GeneralPreferenceFragment newInstance() {
            return new GeneralPreferenceFragment();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            etpServerHost=(EditTextPreference)findPreference("server_host");
            etpSitePath=(EditTextPreference)findPreference("site_path");

            if (!etpServerHost.getText().equals(""))
                etpServerHost.setSummary(etpServerHost.getText());

            if (!etpSitePath.getText().equals(""))
                etpSitePath.setSummary(etpSitePath.getText());

        }


        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (s.equals("server_host")) {
                if (!etpServerHost.getText().equals(""))
                    etpServerHost.setSummary(etpServerHost.getText());
                //color.setView();
            }
        }
    }
}
