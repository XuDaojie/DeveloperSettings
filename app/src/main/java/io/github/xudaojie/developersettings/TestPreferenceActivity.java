package io.github.xudaojie.developersettings;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by xdj on 2017/3/4.
 */

public class TestPreferenceActivity extends AppCompatPreferenceActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addPreferencesFromResource(R.xml.pref_test);
    }

//    /**
//     * Set up the {@link android.app.ActionBar}, if the API is available.
//     */
//    private void setupActionBar() {
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            // Show the Up button in the action bar.
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//    }
}
