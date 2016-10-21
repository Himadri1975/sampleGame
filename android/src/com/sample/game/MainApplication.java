package com.sample.game;

import android.app.Application;
import com.google.android.gms.analytics.GoogleAnalytics;

/**
 * Created by ehimmaj on 10/18/2016.
 */

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        GoogleAnalytics.getInstance(this).newTracker(R.xml.app_tracker_config);
    }
}
