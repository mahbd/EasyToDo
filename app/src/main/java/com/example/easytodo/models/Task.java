package com.example.easytodo.models;


import com.example.easytodo.enums.ActionEnum;
import com.example.easytodo.enums.TableEnum;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Task extends RealmObject {
    @PrimaryKey
    private long id;
    private String title;
    private String description;
    private String deadline;
    private int duration;
    private boolean completed;
    private int occurrence;
    private int priority;
    private RealmList<String> tags;
    private int reminder;
    private String project;
    private long user;

    public Task() {
    }

    public Task(String title, String description, String deadline, int duration, boolean completed, int occurrence, int priority, List<String> tags, int reminder, String project, long user) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.duration = duration;
        this.completed = completed;
        this.occurrence = occurrence;
        this.priority = priority;
        this.tags = new RealmList<>();
        this.tags.addAll(tags);
        this.reminder = reminder;
        this.project = project;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDeadline() {
        return deadline;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getOccurrence() {
        return occurrence;
    }

    public int getPriority() {
        return priority;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getTagsString() {
        StringBuilder tagsString = new StringBuilder();
        for (String tag : tags) {
            tagsString.append(tag).append(", ");
        }
        if (tagsString.length() > 0)
            tagsString.delete(tagsString.length() - 2, tagsString.length());
        return tagsString.toString();
    }

    public int getReminder() {
        return reminder;
    }

    public String getProject() {
        return project;
    }

    public long getUser() {
        return user;
    }

    public void save(boolean change) {
        if (id == 0) {
            Number maxId = Realm.getDefaultInstance().where(Task.class).max("id");
            id = maxId == null ? 1000_000_000 : maxId.longValue() + 1;
        }

        Realm.getDefaultInstance().executeTransaction(realm -> realm.copyToRealm(Task.this));

        if (change) {
            Sync sync = new Sync(TableEnum.TASK, id, ActionEnum.CREATE);
            sync.save();
        }
    }

    public void save() {
        save(true);
    }
}
