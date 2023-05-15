package com.example.easytodo.utils;

import com.example.easytodo.enums.ActionEnum;

import java.util.ArrayList;
import java.util.List;

public class Events {
    public interface TagListener {
        void onTagChanged(long tagId, ActionEnum action);
    }

    static private final List<TagListener> tagListeners = new ArrayList<>();

    public static void setTagListener(TagListener listener) {
        tagListeners.add(listener);
    }

    public static void notifyTagListeners(long tagId, ActionEnum action) {
        for (TagListener listener : tagListeners) {
            listener.onTagChanged(tagId, action);
        }
    }
    public interface ProjectListener {
        void onProjectChanged(long projectId, ActionEnum action);
    }

    static private final List<ProjectListener> projectListeners = new ArrayList<>();

    public static void setProjectListener(ProjectListener listener) {
        projectListeners.add(listener);
    }

    public static void notifyProjectListeners(long projectId, ActionEnum action) {
        for (ProjectListener listener : projectListeners) {
            listener.onProjectChanged(projectId, action);
        }
    }
    public interface TaskListener {
        void onTaskChanged(long taskId, ActionEnum action);
    }

    static private final List<TaskListener> taskListeners = new ArrayList<>();

    public static void setTaskListener(TaskListener listener) {
        taskListeners.add(listener);
    }

    public static void notifyTaskListeners(long taskId, ActionEnum action) {
        for (TaskListener listener : taskListeners) {
            listener.onTaskChanged(taskId, action);
        }
    }
}
