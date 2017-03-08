package io.github.xudaojie.developersettings;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by xdj on 2017/3/4.
 */

public class DevelopmentSettingsActivity extends AppCompatPreferenceActivity {
    private static final String TAG = DevelopmentSettingsActivity.class.getSimpleName();

    private boolean mRootPermission = false;
    private Handler mRootHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mDebugLayoutPref.setEnabled(mRootPermission);
            mDebugHwOverdrawPref.setEnabled(mRootPermission);
            mDebugHwOverdraw.setEnabled(mRootPermission);
        }
    };

    private SwitchPreference mDebugLayoutPref;
    private ListPreference mDebugHwOverdrawPref;
    private ListPreference mDebugHwOverdraw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        addPreferencesFromResource(R.xml.pref_developerment_settings);

        addPreferencesFromResource(R.xml.pref_developerment_settings);

        mDebugLayoutPref = (SwitchPreference) findPreference("debug_layout");
        // DevelopmentSettings.DEBUG_HW_OVERDRAW_KEY 调试GPU过度绘制
        mDebugHwOverdrawPref = (ListPreference) findPreference("debug_hw_overdraw");
        // DevelopmentSettings.TRACK_FRAME_TIME_KEY GPU呈现模式分析
        mDebugHwOverdraw = (ListPreference) findPreference("track_frame_time");

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkRootPermission();
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

    private void checkRootPermission() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                // 耗时操作
                mRootPermission = ShellUtils.checkRootPermission();
                mRootHandler.sendEmptyMessage(0);
            }
        }.start();
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
        mDebugLayoutPref.setChecked(SystemProperties.getBoolean(Constants.VIEW_DEBUG_LAYOUT_PROPERTY));

        mDebugLayoutPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
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
        final CharSequence[] entries = mDebugHwOverdrawPref.getEntries();
        String value = SystemProperties.get(Constants.THREADED_RENDERER_DEBUG_OVERDRAW_PROPERTY);
        int idxOfValue = mDebugHwOverdrawPref.findIndexOfValue(value);
        if (idxOfValue != -1) {
            mDebugHwOverdrawPref.setValueIndex(idxOfValue);
            mDebugHwOverdrawPref.setSummary(entries[idxOfValue]);
        }

        mDebugHwOverdrawPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // ThreadedRenderer.DEBUG_OVERDRAW_PROPERTY
                String command = String.format("setprop %s %s",
                        Constants.THREADED_RENDERER_DEBUG_OVERDRAW_PROPERTY,
                        newValue.toString());
                ShellUtils.execCommand(command, true);
                new SystemPropPoker().execute();

                int indexOfValue = mDebugHwOverdrawPref.findIndexOfValue(newValue.toString());
                preference.setSummary(entries[indexOfValue]);

                return true;
            }
        });
    }

    private void updateTrackFrameTime() {

        final CharSequence[] entries = mDebugHwOverdraw.getEntries();
        String value = SystemProperties.get(Constants.THREADED_RENDERER_PROFILE_PROPERTY);
        int idxOfValue = mDebugHwOverdraw.findIndexOfValue(value);
        if (idxOfValue != -1) {
            mDebugHwOverdraw.setValueIndex(idxOfValue);
            mDebugHwOverdraw.setSummary(entries[idxOfValue]);
        }

        mDebugHwOverdraw.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
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
