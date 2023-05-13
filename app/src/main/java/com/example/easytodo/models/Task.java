package com.example.easytodo.models;


import com.example.easytodo.enums.ActionEnum;
import com.example.easytodo.enums.TableEnum;

import java.time.OffsetDateTime;
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
    private RealmList<String> tag_titles;
    private int reminder;
    private String project_title;
    private long user;

    public Task() {
    }

    public Task(String title, String description, OffsetDateTime deadline, int duration, boolean completed,
                int occurrence, int priority, List<String> tag_titles, int reminder, String project_title, long user) {
        this.title = title;
        this.description = description;
        this.deadline = deadline.toString();
        this.duration = duration;
        this.completed = completed;
        this.occurrence = occurrence;
        this.priority = priority;
        this.tag_titles = new RealmList<>();
        this.tag_titles.addAll(tag_titles);
        this.reminder = reminder;
        this.project_title = project_title;
        this.user = user;
    }

    public Task(String title, String description, OffsetDateTime deadline) {
        this.title = title;
        this.description = description;
        this.deadline = deadline.toString();
        this.duration = 0;
        this.completed = false;
        this.occurrence = 0;
        this.priority = 0;
        this.tag_titles = new RealmList<>();
        this.reminder = 0;
        this.project_title = "";
        this.user = 0;
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

    public OffsetDateTime getDeadline() {
        if (deadline == null || deadline.isEmpty()) {
            return null;
        }
        return OffsetDateTime.parse(deadline);
    }

    public String getDeadlineStr() {
        OffsetDateTime deadline = getDeadline();

        if (deadline == null || deadline.getYear() < 2000) {
            return "";
        }
        return deadline.toString();
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

    public List<String> getTag_titles() {
        return tag_titles;
    }

    public String getTagsString() {
        StringBuilder tagsString = new StringBuilder();
        for (String tag : tag_titles) {
            tagsString.append(tag).append(", ");
        }
        if (tagsString.length() > 0)
            tagsString.delete(tagsString.length() - 2, tagsString.length());
        return tagsString.toString();
    }

    public int getReminder() {
        return reminder;
    }

    public String getProject_title() {
        if (project_title == null) {
            return "";
        }
        return project_title;
    }

    public long getUser() {
        return user;
    }

    public void save(boolean change) {
        if (id == 0) {
            Number maxId = Realm.getDefaultInstance().where(Task.class).max("id");
            id = maxId == null ? 1000_000_000 : maxId.longValue() + 1;
        }

        Realm.getDefaultInstance().executeTransaction(realm -> realm.copyToRealmOrUpdate(Task.this));

        if (change) {
            Sync sync = new Sync(TableEnum.TASK, id, ActionEnum.CREATE);
            sync.save();
        }
    }

    public void save() {
        save(true);
    }

    public void setId(long taskId) {
        id = taskId;
    }
}
