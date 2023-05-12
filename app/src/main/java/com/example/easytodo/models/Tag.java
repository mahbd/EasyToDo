package com.example.easytodo.models;


import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Tag extends RealmObject {
    @PrimaryKey
    private String title;

    public Tag(String title) {
        this.title = title;
    }

    public Tag() {
    }

    public String getTitle() {
        return title;
    }

    public void save() {
        Realm.getDefaultInstance().executeTransaction(realm -> realm.copyToRealm(Tag.this));
    }

    public static boolean exists(String title) {
        return Realm.getDefaultInstance().where(Tag.class).equalTo("title", title).count() > 0;
    }
}
