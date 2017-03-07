package io.github.xudaojie.developersettings;

import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;

/**
 * Created by xdj on 2017/3/3.
 */

public class DeveloperTiles {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static class StartApp extends TileService {

        @Override
        public void onClick() {
//            Intent i = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
//            startActivity(i);
            Intent i = new Intent(this, DevelopmentSettingsActivity.class);
            startActivity(i);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static class ShowTouches extends TileService {

        @Override
        public void onStartListening() {
            refresh();
        }

        public void refresh() {
            int enabled = 0;
            try {
                enabled = Settings.System.getInt(getContentResolver(), "show_touches");
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            getQsTile().setState(enabled == 1 ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            getQsTile().updateTile();
        }

        @Override
        public void onClick() {
            Settings.System.putInt(getContentResolver(), "show_touches",
                    getQsTile().getState() == Tile.STATE_INACTIVE ? 1 : 0);
            refresh();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static class PointLocation extends TileService {
        @Override
        public void onStartListening() {
            refresh();
        }

        public void refresh() {
            int enabled = 0;
            try {
                enabled = Settings.System.getInt(getContentResolver(), "pointer_location");
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            getQsTile().setState(enabled == 1 ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            getQsTile().updateTile();
        }

        @Override
        public void onClick() {
            Settings.System.putInt(getContentResolver(), "pointer_location",
                    getQsTile().getState() == Tile.STATE_INACTIVE ? 1 : 0);
            refresh();
        }
    }

}
