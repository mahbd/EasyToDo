package com.example.easytodo;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.easytodo.enums.ActionEnum;
import com.example.easytodo.enums.TableEnum;
import com.example.easytodo.models.Sync;
import com.example.easytodo.models.Token;
import com.example.easytodo.utils.Events;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("easy_todo.realm")
                .schemaVersion(1)
                .allowWritesOnUiThread(true)
                .allowQueriesOnUiThread(true)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        Realm realm = Realm.getDefaultInstance();

        Events.setTagListener((tagId, action) -> {
            if (action == ActionEnum.UPDATE) {
                RealmResults<Sync> syncs = realm.where(Sync.class)
                        .equalTo("table", TableEnum.TAG.getValue())
                        .equalTo("dataId", tagId).findAll();
                if (syncs != null && syncs.size() > 0) {
                    realm.executeTransaction(realm1 -> syncs.deleteAllFromRealm());
                }
                Sync sync = new Sync(TableEnum.TAG, tagId, ActionEnum.UPDATE);
                sync.save();
            }
        });

        Events.setProjectListener((projectId, action) -> {
            if (action == ActionEnum.UPDATE) {
                RealmResults<Sync> syncs = realm.where(Sync.class)
                        .equalTo("table", TableEnum.PROJECT.getValue())
                        .equalTo("dataId", projectId).findAll();
                if (syncs != null && syncs.size() > 0) {
                    realm.executeTransaction(realm1 -> syncs.deleteAllFromRealm());
                }
                Sync sync = new Sync(TableEnum.PROJECT, projectId, ActionEnum.UPDATE);
                sync.save();
            }
        });

        Events.setTaskListener((taskId, action) -> {
            if (action == ActionEnum.UPDATE) {
                RealmResults<Sync> syncs = realm.where(Sync.class)
                        .equalTo("table", TableEnum.TASK.getValue())
                        .equalTo("dataId", taskId).findAll();
                if (syncs != null && syncs.size() > 0) {
                    realm.executeTransaction(realm1 -> syncs.deleteAllFromRealm());
                }
                Sync sync = new Sync(TableEnum.TASK, taskId, ActionEnum.UPDATE);
                sync.save();
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Token.access = prefs.getString("access", "");
        Token.refresh = prefs.getString("refresh", "");
    }
}
