package com.example.easytodo.models;

import com.example.easytodo.enums.ActionEnum;
import com.example.easytodo.enums.TableEnum;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Share extends RealmObject {
    @PrimaryKey
    private long id;
    private long user;
    private long shared_with;
    private String table;
    private long data_id;
    private String title;

    public Share() {
    }

    public Share(long user, long shared_with, String table, long data_id, String title) {
        this.user = user;
        this.shared_with = shared_with;
        this.table = table;
        this.data_id = data_id;
        this.title = title;
    }

    public void save(boolean change) {
        if (this.id == 0) {
            Number maxId = Realm.getDefaultInstance().where(Share.class).max("id");
            this.id = maxId == null ? 1000_000_000 : maxId.longValue() + 1;
        }
        Realm.getDefaultInstance().executeTransaction(realm -> realm.copyToRealmOrUpdate(Share.this));

        if (change) {
            Sync sync = new Sync(TableEnum.SHARED, id, ActionEnum.CREATE);
            sync.save();
        }
    }

    public void save() {
        save(true);
    }

    public static boolean exists(long user, long shared_with, long table, long data_id) {
        return Realm.getDefaultInstance().where(Share.class)
                .equalTo("user", user)
                .equalTo("shared_with", shared_with)
                .equalTo("table", table)
                .equalTo("data_id", data_id)
                .count() > 0;
    }

    public long getUser() {
        return user;
    }

    public long getShared_with() {
        return shared_with;
    }

    public String getTitle() {
        return title;
    }
}
