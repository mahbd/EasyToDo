package com.example.easytodo.models;


import com.example.easytodo.enums.ActionEnum;
import com.example.easytodo.enums.TableEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Tag extends RealmObject {
    @PrimaryKey
    private long id;
    private String title;

    public Tag(String title) {
        this.title = title;
    }

    public Tag() {
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (this.title.equals(title)) return;
        Realm.getDefaultInstance().beginTransaction();
        this.title = title;
        Realm.getDefaultInstance().commitTransaction();
    }

    public void save(boolean change) {
        if (exists(title)) {
            return;
        }
        if (id == 0) {
            Number maxId = Realm.getDefaultInstance().where(Tag.class).max("id");
            id = maxId == null ? 1000_000_000 : maxId.longValue() + 1;
        }

        Realm.getDefaultInstance().executeTransaction(realm -> realm.copyToRealmOrUpdate(Tag.this));
        if (change) {
            Sync sync = new Sync(TableEnum.TAG, id, ActionEnum.CREATE);
            sync.save();
        }
    }

    public void save() {
        save(true);
    }

    public static void delete(long id, boolean change) {
        AtomicBoolean deleted = new AtomicBoolean(false);
        Realm.getDefaultInstance().executeTransaction(realm -> {
            Tag tag = realm.where(Tag.class).equalTo("id", id).findFirst();
            if (tag != null) {
                tag.deleteFromRealm();
                deleted.set(true);
            }
        });

        if (change && deleted.get()) {
            Sync sync = new Sync(TableEnum.TAG, id, ActionEnum.DELETE);
            sync.save();
        }
    }

    public static void delete(long id) {
        delete(id, true);
    }

    public static boolean exists(String title) {
        return Realm.getDefaultInstance().where(Tag.class).equalTo("title", title).count() > 0;
    }

    public static Map<String, Object> getMap(Tag tag) {
        Map<String, Object> map = new HashMap<>();
        if (tag.getTitle() != null && !tag.getTitle().isEmpty()) {
            map.put("title", tag.getTitle());
        }
        return map;
    }
}
