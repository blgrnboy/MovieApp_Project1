package com.latchkostov.android.movieapp_project1;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

/**
 * Created by latchk on 3/13/17.
 */

public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Stetho.InitializerBuilder builder = Stetho.newInitializerBuilder(this);
        builder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this));
        // Use the InitializerBuilder to generate an Initializer
        Stetho.Initializer initializer = builder.build();

        // Initialize Stetho with the Initializer
        Stetho.initialize(initializer);
    }

    public static Context getContext() {
        return mContext;
    }
}
