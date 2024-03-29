package com.example.easytodo.utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.easytodo.enums.ActionEnum;
import com.example.easytodo.enums.TableEnum;
import com.example.easytodo.models.Change;
import com.example.easytodo.models.Project;
import com.example.easytodo.models.Sync;
import com.example.easytodo.models.Tag;
import com.example.easytodo.models.Task;
import com.example.easytodo.models.User;
import com.example.easytodo.services.ChangeAPI;
import com.example.easytodo.services.GenAPIS;
import com.example.easytodo.services.ProjectAPI;
import com.example.easytodo.services.TagAPI;
import com.example.easytodo.services.TaskAPI;
import com.example.easytodo.services.UserAPI;
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
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                if (networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    fetch();
                    push();
                    return;
                }
            }
        }
        H.showAlert(context, "No internet", "Connect to internet", () -> {
        });
    }

    public void fetch() {
        String lastSync = prefs.getString("last_sync", null);
        ChangeAPI changeAPI = GenAPIS.getAPI(ChangeAPI.class);
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
        System.out.println("createOrUpdate: " + change.getTable() + " " + change.getdataId() + " " + change.getAction());
        if (change.getTable().equals(TableEnum.TAG.getValue())) {
            createOrUpdateTag(change.getdataId());
        } else if (change.getTable().equals(TableEnum.PROJECT.getValue())) {
            createOrUpdateProject(change.getdataId());
        } else if (change.getTable().equals(TableEnum.TASK.getValue())) {
            createOrUpdateTask(change.getdataId());
        } else if (change.getTable().equals(TableEnum.USER.getValue())) {
            createOrUpdateUser(change.getdataId());
        }
    }

    public void delete(Change change) {
        if (change.getTable().equals(TableEnum.TAG.getValue())) {
            Tag.delete(change.getdataId(), false);
        } else if (change.getTable().equals(TableEnum.PROJECT.getValue())) {
            Project.delete(change.getdataId(), false);
        } else if (change.getTable().equals(TableEnum.TASK.getValue())) {
            Task.delete(change.getdataId(), false);
        } else if (change.getTable().equals(TableEnum.USER.getValue())) {
            User.delete(change.getdataId());
        }
    }

    public void createOrUpdateTag(long tagId) {
        TagAPI tagAPI = GenAPIS.getAPI(TagAPI.class);
        H.enqueueReq(tagAPI.getTag(tagId), (call, response) -> {
            if (response.isSuccessful() && response.body() != null) {
                Tag tag = response.body();
                tag.save(false);
            }
        });
    }

    public void createOrUpdateProject(long projectId) {
        ProjectAPI projectAPI = GenAPIS.getAPI(ProjectAPI.class);
        H.enqueueReq(projectAPI.getProject(projectId), (call, response) -> {
            if (response.isSuccessful() && response.body() != null) {
                Project project = response.body();
                project.save(false);
            }
        });
    }

    public void createOrUpdateUser(long userId) {
        UserAPI userAPI = GenAPIS.getAPI(UserAPI.class);
        H.enqueueReq(userAPI.getUser(userId), (call, response) -> {
            if (response.isSuccessful() && response.body() != null) {
                User user = response.body();
                user.save();
            }
        });
    }

    public void createOrUpdateTask(long taskId) {
        TaskAPI taskAPI = GenAPIS.getAPI(TaskAPI.class);
        H.enqueueReq(taskAPI.getTask(taskId), (call, response) -> {
            if (response.isSuccessful() && response.body() != null) {
                Task task = response.body();
                task.save(false);
            }
        });
    }

    public static void pushTask(Sync sync) {
        TaskAPI taskAPI = GenAPIS.getAPI(TaskAPI.class);
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
            H.enqueueReq(taskAPI.deleteTask(sync.getDataId()), (call, response) -> {
                if (sync.isValid()) {
                    Sync.delete(sync.getId());
                }
            });
        }
    }

    public static void pushProject(Sync sync) {
        ProjectAPI projectAPI = GenAPIS.getAPI(ProjectAPI.class);
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
            H.enqueueReq(projectAPI.deleteProject(sync.getDataId()), (call, response) -> {
                if (sync.isValid()) {
                    Sync.delete(sync.getId());
                }
            });
        }
    }

    public static void pushTag(Sync sync) {
        TagAPI tagAPI = GenAPIS.getAPI(TagAPI.class);
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
            H.enqueueReq(tagAPI.deleteTag(sync.getDataId()), (call, response) -> {
                if (sync.isValid()) {
                    Sync.delete(sync.getId());
                }
            });
        }
    }
}
