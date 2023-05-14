package com.example.easytodo.models;

import com.example.easytodo.enums.ActionEnum;
import com.example.easytodo.enums.TableEnum;
import com.example.easytodo.utils.H;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

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

    public Project(String title, String description, OffsetDateTime deadline) {
        this.title = title;
        this.description = description;
        this.deadline = deadline.toString();
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

    public void save(boolean change) {
        if (id == 0) {
            Number maxId = Realm.getDefaultInstance().where(Project.class).max("id");
            id = maxId == null ? 1000_000_000 : maxId.longValue() + 1;
        }

        Realm.getDefaultInstance().executeTransaction(realm -> realm.copyToRealmOrUpdate(Project.this));

        if (change) {
            Sync sync = new Sync(TableEnum.PROJECT, id, ActionEnum.CREATE);
            sync.save();
        }
    }

    public void save() {
        save(true);
    }

    public static void delete(long id, boolean change) {
        Realm.getDefaultInstance().executeTransaction(realm -> {
            Project project = realm.where(Project.class).equalTo("id", id).findFirst();
            if (project != null) {
                project.deleteFromRealm();
                if (change) {
                    Sync sync = new Sync(TableEnum.PROJECT, id, ActionEnum.DELETE);
                    sync.save();
                }
            }
        });
    }

    public static void delete(long id) {
        delete(id, true);
    }

    public static boolean exists(String title) {
        return Realm.getDefaultInstance().where(Project.class).equalTo("title", title).count() > 0;
    }

    public static Map<String, Object> getMap(Project project) {
        Map<String, Object> projectMap = new HashMap<>();
        if (project.getTitle() != null && !project.getTitle().isEmpty())
            projectMap.put("title", project.getTitle());
        if (project.getDescription() != null && !project.getDescription().isEmpty())
            projectMap.put("description", project.getDescription());
        if (project.getDeadline() != null && !(project.getDeadline().getYear() < 2000))
            projectMap.put("deadline", project.getDeadlineStr());
        return projectMap;
    }
}
