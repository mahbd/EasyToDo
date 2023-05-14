package com.example.easytodo.utils;

import com.example.easytodo.enums.ActionEnum;

import java.util.ArrayList;
import java.util.List;

public class Events {
    public interface TagListener {
        void onTagChanged(long tagId, ActionEnum action);
    }

    static private final List<TagListener> tagListeners = new ArrayList<>();

    public static TagListener setTagListener(TagListener listener) {
        tagListeners.add(listener);
        return listener;
    }

    public static void removeDBTagListener(TagListener listener) {
        tagListeners.remove(listener);
    }

    public static void notifyTagListeners(long tagId, ActionEnum action) {
        for (TagListener listener : tagListeners) {
            listener.onTagChanged(tagId, action);
        }
    }
}
