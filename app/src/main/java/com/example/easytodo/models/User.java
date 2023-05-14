package com.example.easytodo.models;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {
    @PrimaryKey
    private long id;
    private String username;
    private String first_name;
    private String last_name;

    public User() {

    }

    public User(long id, String username, String first_name, String last_name) {
        this.id = id;
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void save() {
        Realm.getDefaultInstance().executeTransaction(realm -> realm.copyToRealm(User.this));
    }
}
