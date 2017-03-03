package io.github.xudaojie.developertools;

import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;

/**
 * Created by xdj on 2017/3/1.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class QuickSettingTile extends TileService {
    private static final String TAG = "QuickSettingTile";

    @Override
    public void onClick() {
        super.onClick();
        Tile tile = getQsTile();

        if (tile.getState() == Tile.STATE_ACTIVE) {
            tile.setState(Tile.STATE_INACTIVE);
        } else {
            tile.setState(Tile.STATE_ACTIVE);
        }
        tile.updateTile();
    }
}
