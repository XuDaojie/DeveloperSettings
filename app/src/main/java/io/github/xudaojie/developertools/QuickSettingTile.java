package io.github.xudaojie.developertools;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;

/**
 * Created by xdj on 2017/3/1.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class QuickSettingTile extends TileService {
    private static final String TAG = "QuickSettingTile";

    int i = 1;

    @Override
    public void onClick() {
        super.onClick();
//        Settings.ACTION_DREAM_SETTINGS;
//        Intent i = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
//        Intent i = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
//        startActivity(i);

        if (!Settings.System.canWrite(getApplicationContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
            startActivity(intent);
//            startActivityForResult(intent, 200);
        }

        Tile tile = getQsTile();
//        tile.setLabel(i++ + "");
        if (tile.getState() == Tile.STATE_ACTIVE) {
            tile.setState(Tile.STATE_INACTIVE);
        } else {
            tile.setState(Tile.STATE_ACTIVE);
        }
        tile.updateTile();
    }
}
