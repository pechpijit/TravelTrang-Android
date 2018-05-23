package com.khiancode.traveltrang;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class SetFonts extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/charm.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }
}
