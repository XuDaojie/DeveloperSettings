package io.github.xudaojie.developertools;

import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.content.ContentValues.TAG;

/**
 * Created by xdj on 2017/3/3.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class DevTiles extends TileService {
    @Override
    public void onStartListening() {
        super.onStartListening();
        refresh();
    }

    private static final String VIEW_DEBUG_LAYOUT_PROPERTY = "debug.layout";
    private static int IBINDER_SYSPROPS_TRANSACTION = ('_'<<24)|('S'<<16)|('P'<<8)|'R';

    public void refresh() {
//        final boolean enabled = SystemProperties.getBoolean(View.DEBUG_LAYOUT_PROPERTY, false);
//        getQsTile().setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
//        getQsTile().updateTile();
    }

    @Override
    public void onClick() {
//        SystemProperties.set(View.DEBUG_LAYOUT_PROPERTY,
//                getQsTile().getState() == Tile.STATE_INACTIVE ? "true" : "false");
//        new DevelopmentSettings.SystemPropPoker().execute(); // Settings app magic
//        refresh();

        SystemProperties.set(VIEW_DEBUG_LAYOUT_PROPERTY, "true");
        new SystemPropPoker().execute();
        String tt = SystemProperties.get(VIEW_DEBUG_LAYOUT_PROPERTY);
        Log.i(TAG, "onClick: " + tt);
    }

    private static class SystemPropPoker extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Class serviceManager = null;
            Method listServices = null;
            Method checkService = null;

            try {
                serviceManager = Class.forName("android.os.ServiceManager");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (serviceManager != null) {

            }

            try {
                listServices = serviceManager.getMethod("listServices");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            String[] services = new String[0];
            try {
                services = (String[]) listServices.invoke(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
//            String[] services = ServiceManager.listServices();
            for (String service : services) {
                try {
                    checkService = serviceManager.getMethod("checkService", String.class);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
//                IBinder obj = ServiceManager.checkService(service);
                IBinder obj = null;
                try {
                    obj = (IBinder) checkService.invoke(null, service);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (obj != null) {
                    Parcel data = Parcel.obtain();
                    try {
                        obj.transact(IBINDER_SYSPROPS_TRANSACTION, data, null, 0);
                    } catch (RemoteException e) {
                    } catch (Exception e) {
                        Log.i(TAG, "Someone wrote a bad service '" + service
                                + "' that doesn't like to be poked: " + e);
                    }
                    data.recycle();
                }
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
