package com.example.easytodo.models;

import com.example.easytodo.enums.ActionEnum;
import com.example.easytodo.enums.TableEnum;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Project extends RealmObject {
    @PrimaryKey
    private long id;
    private String title;
    private String description;
    private String deadline;

    public Project() {
    }

    public Project(String title, String description, String deadline) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
    }

    public void save(boolean change) {
        if (id == 0) {
            Number maxId = Realm.getDefaultInstance().where(Project.class).max("id");
            id = maxId == null ? 1000_000_000 : maxId.longValue() + 1;
        }

        Realm.getDefaultInstance().executeTransaction(realm -> realm.copyToRealm(Project.this));

        if (change) {
            Sync sync = new Sync(TableEnum.PROJECT, id, ActionEnum.CREATE);
            sync.save();
        }
    }

    public void save() {
        save(true);
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        if (title == null) {
            return "";
        }
        return title;
    }

    public String getDescription() {
        if (description == null) {
            return "";
        }
        return description;
    }

    public String getDeadline() {
        if (deadline == null) {
            return "";
        }
        return deadline;
    }

    public void setId(long projectId) {
        this.id = projectId;
    }
}
