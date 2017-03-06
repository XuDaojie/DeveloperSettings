package io.github.xudaojie.developersettings;

import android.os.Build;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;

/**
 * Created by xdj on 2017/3/3.
 */

public class DeveloperTiles {

    private static final String VIEW_DEBUG_LAYOUT_PROPERTY = "debug.layout";

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static class ShowTouches extends TileService {
        @Override
        public void onClick() {
            super.onClick();
            int showTouches = 0;
            try {
                showTouches = Settings.System.getInt(getContentResolver(), "show_touches");
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            if (showTouches == 0) {
                Settings.System.putInt(getContentResolver(), "show_touches",
                        1);
                getQsTile().setState(Tile.STATE_ACTIVE);
            } else {
                Settings.System.putInt(getContentResolver(), "show_touches",
                        0);
                getQsTile().setState(Tile.STATE_INACTIVE);
            }
            getQsTile().updateTile();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static class PointLocation extends TileService {
        @Override
        public void onClick() {
            super.onClick();
            int showTouches = 0;
            try {
                showTouches = Settings.System.getInt(getContentResolver(), "pointer_location");
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            if (showTouches == 0) {
                Settings.System.putInt(getContentResolver(), "pointer_location",
                        1);
                getQsTile().setState(Tile.STATE_ACTIVE);
            } else {
                Settings.System.putInt(getContentResolver(), "pointer_location",
                        0);
                getQsTile().setState(Tile.STATE_INACTIVE);
            }
            getQsTile().updateTile();
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
