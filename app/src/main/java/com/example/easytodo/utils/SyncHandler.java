package com.example.easytodo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.Manifest;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.easytodo.enums.ActionEnum;
import com.example.easytodo.enums.TableEnum;
import com.example.easytodo.models.Change;
import com.example.easytodo.models.Project;
import com.example.easytodo.models.Sync;
import com.example.easytodo.models.Tag;
import com.example.easytodo.models.Task;
import com.example.easytodo.services.ChangeAPI;
import com.example.easytodo.services.GenAPIS;
import com.example.easytodo.services.ProjectAPI;
import com.example.easytodo.services.TagAPI;
import com.example.easytodo.services.TaskAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.realm.Realm;
import retrofit2.Call;

public class SyncHandler {
    Context context;
    SharedPreferences prefs;

    public SyncHandler(Context context) {
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void sync() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                if (networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    fetch();
                    push();
                }
            }
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.INTERNET}, 100);
        }
    }

    public void fetch() {
        String lastSync = prefs.getString("last_sync", null);
        ChangeAPI changeAPI = GenAPIS.getChangeAPI();
        if (lastSync == null) {
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

    public void push() {
        List<Sync> syncs = Realm.getDefaultInstance().where(Sync.class).findAll();
        for (Sync sync : syncs) {
            if (Objects.equals(sync.getTable(), TableEnum.TASK.getValue())) {
                pushTask(sync);
            } else if (Objects.equals(sync.getTable(), TableEnum.PROJECT.getValue())) {
                pushProject(sync);
            } else if (Objects.equals(sync.getTable(), TableEnum.TAG.getValue())) {
                pushTag(sync);
            }
        }
    }

    public void applyChanges(List<Change> changes) {
        for (Change change : changes) {
            if (change.getAction().equals(ActionEnum.CREATE.getValue()) || change.getAction().equals(ActionEnum.UPDATE.getValue())) {
                createOrUpdate(change);
            } else if (change.getAction().equals(ActionEnum.DELETE.getValue())) {
                delete(change);
            }
        }
    }

    public void createOrUpdate(Change change) {
        if (change.getTable().equals(TableEnum.TAG.getValue())) {
            createOrUpdateTag(change.getData_id());
        } else if (change.getTable().equals(TableEnum.PROJECT.getValue())) {
            createOrUpdateProject(change.getData_id());
        } else if (change.getTable().equals(TableEnum.TASK.getValue())) {
            createTask(change.getData_id());
        }
    }

    public void delete(Change change) {
        if (change.getTable().equals(TableEnum.TAG.getValue())) {
            Tag.delete(change.getData_id(), false);
        } else if (change.getTable().equals(TableEnum.PROJECT.getValue())) {
            Project.delete(change.getData_id(), false);
        } else if (change.getTable().equals(TableEnum.TASK.getValue())) {
            Task.delete(change.getData_id(), false);
        }
    }

    public void createOrUpdateTag(long tagId) {
        TagAPI tagAPI = GenAPIS.getTagAPI();
        H.enqueueReq(tagAPI.getTag(tagId), (call, response) -> {
            if (response.isSuccessful() && response.body() != null) {
                Tag tag = response.body();
                tag.save(false);
            }
        });
    }

    public void createOrUpdateProject(long projectId) {
        ProjectAPI projectAPI = GenAPIS.getProjectAPI();
        H.enqueueReq(projectAPI.getProject(projectId), (call, response) -> {
            if (response.isSuccessful() && response.body() != null) {
                Project project = response.body();
                project.save(false);
            }
        });
    }

    public void createTask(long taskId) {
        TaskAPI taskAPI = GenAPIS.getTaskAPI();
        H.enqueueReq(taskAPI.getTask(taskId), (call, response) -> {
            if (response.isSuccessful() && response.body() != null) {
                Task task = response.body();
                task.save(false);
            }
        });
    }

    public static void pushTask(Sync sync) {
        TaskAPI taskAPI = GenAPIS.getTaskAPI();
        Task task = Realm.getDefaultInstance().where(Task.class).equalTo("id", sync.getDataId()).findFirst();
        if (task != null) {
            Map<String, Object> taskMap = Task.getMap(task);
            Call<Task> taskCall;
            if (task.getId()  >= 1000_000_000) {
                taskCall = taskAPI.createTask(taskMap);
            } else {
                taskCall = taskAPI.updateTask(task.getId(), taskMap);
            }
            H.enqueueReq(taskCall, (call, response) -> {
                if (response.isSuccessful() && response.body() != null) {
                    Task newTask = response.body();
                    Task.delete(task.getId(), false);
                    newTask.save(false);
                    Sync.delete(sync.getId());
                } else {
                    if (response.code() == 400 && response.errorBody() != null) {
                        try {
                            JsonObject error = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                            System.out.println(error);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            H.enqueueReq(taskAPI.deleteTask(sync.getDataId()), (call, response) -> Sync.delete(sync.getId()));
        }
    }

    public static void pushProject(Sync sync) {
        ProjectAPI projectAPI = GenAPIS.getProjectAPI();
        Project project = Realm.getDefaultInstance().where(Project.class).equalTo("id", sync.getDataId()).findFirst();
        if (project != null) {
            Map<String, Object> projectMap = Project.getMap(project);
            Call<Project> projectCall;
            if (project.getId() >= 1000_000_000) {
                projectCall = projectAPI.createProject(projectMap);
            } else {
                projectCall = projectAPI.updateProject(project.getId(), projectMap);
            }
            H.enqueueReq(projectCall, (call, response) -> {
                if (response.isSuccessful() && response.body() != null) {
                    Project newProject = response.body();
                    Project.delete(project.getId(), false);
                    newProject.save(false);
                    Sync.delete(sync.getId());
                } else {
                    if (response.code() == 400 && response.errorBody() != null) {
                        try {
                            JsonObject error = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                            System.out.println(error);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            H.enqueueReq(projectAPI.deleteProject(sync.getDataId()), (call, response) -> Sync.delete(sync.getId()));
        }
    }

    public static void pushTag(Sync sync) {
        TagAPI tagAPI = GenAPIS.getTagAPI();
        Tag tag = Realm.getDefaultInstance().where(Tag.class).equalTo("id", sync.getDataId()).findFirst();
        if (tag != null) {
            Map<String, Object> tagMap = Tag.getMap(tag);
            Call<Tag> tagCall;
            if (tag.getId() >= 1000_000_000) {
                tagCall = tagAPI.createTag(tagMap);
            } else {
                tagCall = tagAPI.updateTag(tag.getId(), tagMap);
            }
            H.enqueueReq(tagCall, (call, response) -> {
                if (response.isSuccessful() && response.body() != null) {
                    Tag newTag = response.body();
                    Tag.delete(tag.getId(), false);
                    newTag.save(false);
                    Sync.delete(sync.getId());
                } else {
                    if (response.code() == 400 && response.errorBody() != null) {
                        Sync.delete(sync.getId());
                        try {
                            JsonObject error = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                            System.out.println(error);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            H.enqueueReq(tagAPI.deleteTag(sync.getDataId()), (call, response) -> Sync.delete(sync.getId()));
        }
    }
}
