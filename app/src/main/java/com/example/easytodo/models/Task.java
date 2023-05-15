package com.example.easytodo.models;


import com.example.easytodo.enums.ActionEnum;
import com.example.easytodo.enums.TableEnum;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeadline(OffsetDateTime deadline) {
        this.deadline = deadline.toString();
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

    public int getDuration() {
        return duration;
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

    public static void delete(long id, boolean change) {
        AtomicBoolean deleted = new AtomicBoolean(false);
        Realm.getDefaultInstance().executeTransaction(realm -> {
            Task task = realm.where(Task.class).equalTo("id", id).findFirst();
            if (task != null) {
                task.deleteFromRealm();
                deleted.set(true);
            }
        });

        if (change && deleted.get()) {
            Sync sync = new Sync(TableEnum.TASK, id, ActionEnum.DELETE);
            sync.save();
        }
    }

    public static void delete(long id) {
        delete(id, true);
    }

    public static void deleteAll() {
        Realm.getDefaultInstance().executeTransaction(realm -> realm.delete(Task.class));
    }

    public static Map<String, Object> getMap(Task task) {
        Map<String, Object> taskMap = new HashMap<>();
        if (task.getTitle() != null && !task.getTitle().isEmpty())
            taskMap.put("title", task.getTitle());
        if (task.getDescription() != null && !task.getDescription().isEmpty())
            taskMap.put("description", task.getDescription());
        if (task.getDeadline() != null && !(task.getDeadline().getYear() < 2000))
            taskMap.put("deadline", task.getDeadlineStr());
        if (task.getDuration() > 0)
            taskMap.put("duration", task.getDuration());
        taskMap.put("completed", task.isCompleted());
        if (task.getOccurrence() > 0)
            taskMap.put("occurrence", task.getOccurrence());
        if (task.getPriority() > 0)
            taskMap.put("priority", task.getPriority());
        if (task.getTag_titles() != null && !task.getTag_titles().isEmpty())
            taskMap.put("tags", task.getTagsString());
        if (task.getReminder() > 0)
            taskMap.put("reminder", task.getReminder());
        if (task.getProject_title() != null && !task.getProject_title().isEmpty())
            taskMap.put("project", task.getProject_title());

        return taskMap;
    }
}
