package io.github.xudaojie.developersettings;

import android.os.Bundle;
import android.preference.Preference;
import android.provider.Settings;
import android.support.annotation.Nullable;

/**
 * Created by xdj on 2017/3/4.
 */

public class TestPreferenceActivity extends AppCompatPreferenceActivity {
    private static final String TAG = "TestPreferenceActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        addPreferencesFromResource(R.xml.pref_test);
        addPreferencesFromResource(R.xml.pref_test);
        findPreference("show_touches").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Settings.System.putInt(getContentResolver(), "pointer_location",
                        newValue == Boolean.TRUE ? 1 : 0);
                return true;
            }
        });
        findPreference("point_location").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Settings.System.putInt(getContentResolver(), "show_touches",
                        newValue == Boolean.TRUE ? 1 : 0);
                return true;
            }
        });
    }

}
