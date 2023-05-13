package com.example.easytodo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.easytodo.enums.ActionEnum;
import com.example.easytodo.enums.TableEnum;
import com.example.easytodo.models.Change;
import com.example.easytodo.models.Tag;
import com.example.easytodo.services.ChangeAPI;
import com.example.easytodo.services.GenAPIS;
import com.example.easytodo.services.TagAPI;

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
        if (lastSync == null) {
            ChangeAPI changeAPI = GenAPIS.getChangeAPI();
            H.enqueueReq(changeAPI.getChanges(), (call, response) -> {
                if (response.isSuccessful() && response.body() != null) {
                    List<Change> changes = response.body();
                    applyChanges(changes);
                    prefs.edit().putString("last_sync", H.currentUTCISO8601()).apply();
                }
            });
        } else {
            ChangeAPI changeAPI = GenAPIS.getChangeAPI();
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
}
