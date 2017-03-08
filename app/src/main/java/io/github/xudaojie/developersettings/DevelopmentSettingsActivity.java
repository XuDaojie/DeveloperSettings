package io.github.xudaojie.developersettings;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by xdj on 2017/3/4.
 */

public class DevelopmentSettingsActivity extends AppCompatPreferenceActivity {
    private static final String TAG = DevelopmentSettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        addPreferencesFromResource(R.xml.pref_developerment_settings);

        addPreferencesFromResource(R.xml.pref_developerment_settings);
        if (!ShellUtils.checkRootPermission()) {
            Toast.makeText(this, "本程序需要在 root 环境下运行", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAllOptions();
        findPreference("development_options").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                startActivity(i);
                return true;
            }
        });
        findPreference("about").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://github.com/XuDaojie/DeveloperSettings"));
                i.createChooser(i, null);
                startActivity(i);
                return true;
            }
        });
    }

    private void updateAllOptions() {
        updateShowTouchesOptions();
        updatePointerLocationOptions();
        updateDebugLayoutOptions();
        updateDebugHwOverdraw();
        updateTrackFrameTime();
    }

    private void updateShowTouchesOptions() {
        final SwitchPreference showTouchesPref = (SwitchPreference) findPreference("show_touches");
        int enable = 0;
        try {
            enable = Settings.System.getInt(getContentResolver(), Constants.SETTINGS_SYSTEM_SHOW_TOUCHES);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        showTouchesPref.setChecked(enable == 1);

        showTouchesPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Settings.System.putInt(getContentResolver(),
                        Constants.SETTINGS_SYSTEM_SHOW_TOUCHES,
                        newValue == Boolean.TRUE ? 1 : 0);
                return true;
            }
        });
    }

    private void updatePointerLocationOptions() {
        final SwitchPreference pointerLocationPref = (SwitchPreference) findPreference("pointer_location");
        int enable = 0;
        try {
            enable = Settings.System.getInt(getContentResolver(), Constants.SETTINGS_SYSTEM_POINTER_LOCATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        pointerLocationPref.setChecked(enable == 1);

        pointerLocationPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Settings.System.putInt(getContentResolver(),
                        Constants.SETTINGS_SYSTEM_POINTER_LOCATION,
                        newValue == Boolean.TRUE ? 1 : 0);
                return true;
            }
        });
    }

    private void updateDebugLayoutOptions() {
        final SwitchPreference debugLayoutPref = (SwitchPreference) findPreference("debug_layout");
        debugLayoutPref.setChecked(SystemProperties.getBoolean(Constants.VIEW_DEBUG_LAYOUT_PROPERTY));

        debugLayoutPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // setprop debug.layout true
                String command = String.format("setprop %s %s",
                        Constants.VIEW_DEBUG_LAYOUT_PROPERTY,
                        newValue == Boolean.TRUE ? "true" : "false");
                ShellUtils.execCommand(command, true);
                new SystemPropPoker().execute();
                return true;
            }
        });
    }

    private void updateDebugHwOverdraw() {
        // DevelopmentSettings.DEBUG_HW_OVERDRAW_KEY 调试GPU过度绘制
        final ListPreference debugHwOverdrawPref = (ListPreference) findPreference("debug_hw_overdraw");
        final CharSequence[] entries = debugHwOverdrawPref.getEntries();
        String value = SystemProperties.get(Constants.THREADED_RENDERER_DEBUG_OVERDRAW_PROPERTY);
        int idxOfValue = debugHwOverdrawPref.findIndexOfValue(value);
        if (idxOfValue != -1) {
            debugHwOverdrawPref.setValueIndex(idxOfValue);
            debugHwOverdrawPref.setSummary(entries[idxOfValue]);
        }

        debugHwOverdrawPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // ThreadedRenderer.DEBUG_OVERDRAW_PROPERTY
                String command = String.format("setprop %s %s",
                        Constants.THREADED_RENDERER_DEBUG_OVERDRAW_PROPERTY,
                        newValue.toString());
                ShellUtils.execCommand(command, true);
                new SystemPropPoker().execute();

                int indexOfValue = debugHwOverdrawPref.findIndexOfValue(newValue.toString());
                preference.setSummary(entries[indexOfValue]);

                return true;
            }
        });
    }

    private void updateTrackFrameTime() {
        // DevelopmentSettings.TRACK_FRAME_TIME_KEY GPU呈现模式分析
        final ListPreference debugHwOverdraw = (ListPreference) findPreference("track_frame_time");
        final CharSequence[] entries = debugHwOverdraw.getEntries();
        String value = SystemProperties.get(Constants.THREADED_RENDERER_PROFILE_PROPERTY);
        int idxOfValue = debugHwOverdraw.findIndexOfValue(value);
        if (idxOfValue != -1) {
            debugHwOverdraw.setValueIndex(idxOfValue);
            debugHwOverdraw.setSummary(entries[idxOfValue]);
        }

        debugHwOverdraw.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // ThreadedRenderer.PROFILE_PROPERTY
                String command = String.format("setprop %s %s",
                        Constants.THREADED_RENDERER_PROFILE_PROPERTY,
                        newValue.toString());
                ShellUtils.execCommand(command, true);
                new SystemPropPoker().execute();

                ListPreference listPreference = (ListPreference) preference;
                CharSequence[] entries = listPreference.getEntries();
                int indexOfValue = listPreference.findIndexOfValue(newValue.toString());
                preference.setSummary(entries[indexOfValue]);

                return true;
            }
        });
    }

    public static class SystemPropPoker extends AsyncTask<Void, Void, Void> {
        // IBinder.SYSPROPS_TRANSACTION
        private static final int SYSPROPS_TRANSACTION = ('_' << 24) | ('S' << 16) | ('P' << 8) | 'R';

        @Override
        protected Void doInBackground(Void... params) {
//            String[] services = ServiceManager.listServices();
            Method serviceManagerListServicesMethod = null;
            Method serviceManagerCheckServiceMethod = null;
            try {
                final Class serviceManagerClass = Class.forName("android.os.ServiceManager");
                serviceManagerListServicesMethod = serviceManagerClass.getMethod("listServices");
                serviceManagerCheckServiceMethod = serviceManagerClass.getMethod("checkService", String.class);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            try {
                String[] services = (String[]) serviceManagerListServicesMethod.invoke(null);
                for (String service : services) {
//                    IBinder obj = ServiceManager.checkService(service);
                    IBinder obj = (IBinder) serviceManagerCheckServiceMethod.invoke(null, service);
                    if (obj != null) {
                        Parcel data = Parcel.obtain();
                        try {
                            obj.transact(SYSPROPS_TRANSACTION, data, null, 0);
                        } catch (RemoteException e) {
                        } catch (Exception e) {
                            Log.i(TAG, "Someone wrote a bad service '" + service
                                    + "' that doesn't like to be poked: " + e);
                        }
                        data.recycle();
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
