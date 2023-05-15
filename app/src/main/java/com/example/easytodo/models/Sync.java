package com.example.easytodo.models;

import com.example.easytodo.enums.ActionEnum;
import com.example.easytodo.enums.TableEnum;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Sync extends RealmObject {
    @PrimaryKey
    private long id;
    private String table;
    private long dataId;
    private String action;

    public Sync() {
    }

    public Sync(TableEnum table, long dataId, ActionEnum actionEnum) {
        this.table = table.getValue();
        this.dataId = dataId;
        this.action = actionEnum.getValue();
    }

    public void save() {
        if (id == 0) {
            Number maxId = Realm.getDefaultInstance().where(Sync.class).max("id");
            id = maxId == null ? 1 : maxId.longValue() + 1;
        }
        Realm.getDefaultInstance().executeTransaction(realm -> realm.copyToRealm(Sync.this));
    }

    public static void delete(long id) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            Sync sync = realm.where(Sync.class).equalTo("id", id).findFirst();
            if (sync != null) {
                sync.deleteFromRealm();
            }
        });
    }

    public static void deleteAll() {
        Realm.getDefaultInstance().executeTransaction(realm -> realm.delete(Sync.class));
    }

    public long getId() {
        return id;
    }

    public String getTable() {
        return table;
    }

    public long getDataId() {
        return dataId;
    }

    @SuppressWarnings("unused")
    public String getAction() {
        return action;
    }
}
