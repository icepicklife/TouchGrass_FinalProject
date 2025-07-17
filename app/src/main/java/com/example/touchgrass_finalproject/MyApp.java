package com.example.touchgrass_finalproject;

import android.app.Application;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApp extends Application {

    public void onCreate()
    {
        super.onCreate();
        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .allowWritesOnUiThread(true)
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);
    }
}

