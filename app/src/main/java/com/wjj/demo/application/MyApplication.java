package com.wjj.demo.application;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.example.DaoMaster;
import com.example.DaoSession;

/**
 * Created by jiajiewang on 16/7/4.
 */
public class MyApplication extends Application {
    public DaoSession daoSession;
    public SQLiteDatabase db;
    public DaoMaster.DevOpenHelper helper;
    public DaoMaster daoMaster;

    @Override
    public void onCreate() {
        super.onCreate();
        helper = new DaoMaster.DevOpenHelper(this, "person", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}
