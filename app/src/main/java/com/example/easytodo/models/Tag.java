package com.example.easytodo.models;


import com.example.easytodo.enums.ActionEnum;
import com.example.easytodo.enums.TableEnum;

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

    public String getTitle() {
        return title;
    }

    public void save(boolean change) {
        if (exists(title)) {
            return;
        }
        if (id == 0) {
            Number maxId = Realm.getDefaultInstance().where(Tag.class).max("id");
            id = maxId == null ? 1000_000_000 : maxId.longValue() + 1;
        }

        Realm.getDefaultInstance().executeTransaction(realm -> realm.copyToRealm(Tag.this));
        if (change) {
            Sync sync = new Sync(TableEnum.TAG, id, ActionEnum.CREATE);
            sync.save();
        }
    }

    public void save() {
        save(true);
    }

    public static boolean exists(String title) {
        return Realm.getDefaultInstance().where(Tag.class).equalTo("title", title).count() > 0;
    }

    public void setId(long tagId) {
        this.id = tagId;
    }
}
