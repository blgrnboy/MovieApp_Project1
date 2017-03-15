package com.latchkostov.android.movieapp_project1;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by latchk on 3/13/17.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.InitializerBuilder builder = Stetho.newInitializerBuilder(this);
        builder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this));
        // Use the InitializerBuilder to generate an Initializer
        Stetho.Initializer initializer = builder.build();

        // Initialize Stetho with the Initializer
        Stetho.initialize(initializer);
    }
}
