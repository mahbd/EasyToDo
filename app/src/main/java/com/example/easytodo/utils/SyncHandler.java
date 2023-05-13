package com.example.easytodo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.easytodo.enums.ActionEnum;
import com.example.easytodo.enums.TableEnum;
import com.example.easytodo.models.Change;
import com.example.easytodo.models.Project;
import com.example.easytodo.models.Tag;
import com.example.easytodo.models.Task;
import com.example.easytodo.services.ChangeAPI;
import com.example.easytodo.services.GenAPIS;
import com.example.easytodo.services.ProjectAPI;
import com.example.easytodo.services.TagAPI;
import com.example.easytodo.services.TaskAPI;

import java.util.List;

public class SyncHandler {
    Context context;
    SharedPreferences prefs;

    public SyncHandler(Context context) {
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void fetch() {
        String lastSync = prefs.getString("last_sync", null);
        ChangeAPI changeAPI = GenAPIS.getChangeAPI();
        if(lastSync == null) {
            H.enqueueReq(changeAPI.getChanges(), (call, response) -> {
                if (response.isSuccessful() && response.body() != null) {
                    List<Change> changes = response.body();
                    applyChanges(changes);
                    prefs.edit().putString("last_sync", H.currentUTCISO8601()).apply();
                }
            });
        } else {
            H.enqueueReq(changeAPI.getChangesAfter(lastSync), (call, response) -> {
                if (response.isSuccessful() && response.body() != null) {
                    List<Change> changes = response.body();
                    applyChanges(changes);
                    prefs.edit().putString("last_sync", H.currentUTCISO8601()).apply();
                }
            });
        }
    }

    public void applyChanges(List<Change> changes) {
        for (Change change : changes) {
            if (change.getAction().equals(ActionEnum.CREATE.getValue())) {
                create(change);
            } else if (change.getAction().equals(ActionEnum.UPDATE.getValue())) {
                update(change);
            } else if (change.getAction().equals(ActionEnum.DELETE.getValue())) {
                delete(change);
            }
        }
    }

    public void create(Change change) {
        if (change.getTable().equals(TableEnum.TAG.getValue())) {
            createTag(change.getData_id());
        } else if (change.getTable().equals(TableEnum.PROJECT.getValue())) {
            createProject(change.getData_id());
        } else if (change.getTable().equals(TableEnum.TASK.getValue())) {
            createTask(change.getData_id());
        }
    }

    public void update(Change change) {
    }

    public void delete(Change change) {
    }

    public void createTag(long tagId) {
        TagAPI tagAPI = GenAPIS.getTagAPI();
        H.enqueueReq(tagAPI.getTag(tagId), (call, response) -> {
            if (response.isSuccessful() && response.body() != null) {
                Tag tag = response.body();
                tag.setId(tagId);
                tag.save(false);
            }
        });
    }

    public void createProject(long projectId) {
        ProjectAPI projectAPI = GenAPIS.getProjectAPI();
        H.enqueueReq(projectAPI.getProject(projectId), (call, response) -> {
            if (response.isSuccessful() && response.body() != null) {
                Project project = response.body();
                project.setId(projectId);
                project.save(false);
            }
        });
    }

    public void createTask(long taskId) {
        TaskAPI taskAPI = GenAPIS.getTaskAPI();
        H.enqueueReq(taskAPI.getTask(taskId), (call, response) -> {
            if (response.isSuccessful() && response.body() != null) {
                Task task = response.body();
                task.setId(taskId);
                task.save(false);
            }
        });
    }
}
