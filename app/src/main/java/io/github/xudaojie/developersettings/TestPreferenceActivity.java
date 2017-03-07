package io.github.xudaojie.developersettings;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.preference.Preference;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
                Settings.System.putInt(getContentResolver(), "show_touches",
                        newValue == Boolean.TRUE ? 1 : 0);
                return true;
            }
        });
        findPreference("point_location").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Settings.System.putInt(getContentResolver(), "pointer_location",
                        newValue == Boolean.TRUE ? 1 : 0);
                return true;
            }
        });
        findPreference("debug_layout").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String command = String.format("setprop debug.layout %s",
                        newValue == Boolean.TRUE ? "true" : "false");
                ShellUtils.execCommand(command, true);
                // ThreadedRenderer.DEBUG_OVERDRAW_PROPERTY
//                ShellUtils.execCommand("setprop debug.hwui.overdraw show", true); //debug_hw_overdraw
//                ShellUtils.execCommand("setprop debug.hwui.profile visual_bars", true); // false,visual_bars,true
                new SystemPropPoker().execute();
                return true;
            }
        });
        // DevelopmentSettings.DEBUG_HW_OVERDRAW_KEY
        findPreference("debug_hw_overdraw").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // ThreadedRenderer.DEBUG_OVERDRAW_PROPERTY
                String command = String.format("setprop debug.hwui.overdraw %s",
                        newValue.toString());
                ShellUtils.execCommand(command, true);

//                ShellUtils.execCommand("setprop debug.hwui.overdraw show", true); //debug_hw_overdraw
//                ShellUtils.execCommand("setprop debug.hwui.profile visual_bars", true); // false,visual_bars,true
                new SystemPropPoker().execute();
                return true;
            }
        });
        // DevelopmentSettings.TRACK_FRAME_TIME_KEY GPU呈现模式分析
        findPreference("track_frame_time").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // ThreadedRenderer.PROFILE_PROPERTY
                String command = String.format("setprop debug.hwui.profile %s",
                        newValue.toString());
                ShellUtils.execCommand(command, true);

//                ShellUtils.execCommand("setprop debug.hwui.overdraw show", true); //debug_hw_overdraw
//                ShellUtils.execCommand("setprop debug.hwui.profile visual_bars", true); // false,visual_bars,true
                new SystemPropPoker().execute();
                return true;
            }
        });
    }

    public static class SystemPropPoker extends AsyncTask<Void, Void, Void> {
        // IBinder.SYSPROPS_TRANSACTION
        private static final int SYSPROPS_TRANSACTION = ('_'<<24)|('S'<<16)|('P'<<8)|'R';

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

//    public static class SystemPropPoker extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... params) {
//            String[] services = ServiceManager.listServices();
//            for (String service : services) {
//                IBinder obj = ServiceManager.checkService(service);
//                if (obj != null) {
//                    Parcel data = Parcel.obtain();
//                    try {
//                        obj.transact(IBinder.SYSPROPS_TRANSACTION, data, null, 0);
//                    } catch (RemoteException e) {
//                    } catch (Exception e) {
//                        Log.i(TAG, "Someone wrote a bad service '" + service
//                                + "' that doesn't like to be poked: " + e);
//                    }
//                    data.recycle();
//                }
//            }
//            return null;
//        }
//    }
}
