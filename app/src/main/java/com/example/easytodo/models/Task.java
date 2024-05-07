package com.example.easytodo.models;

import java.time.OffsetDateTime;
import java.util.List;


public class Task {
    public String id = null;
    public String title;
    public String description;
    public String deadline;
    public int duration;
    public boolean completed;
    public int occurrence;
    public int priority;
    public List<String> tags;
    public int reminder;
    public String project;

    public Task() {
        this.title = "";
        this.description = "";
        this.deadline = "";
        this.duration = 0;
        this.completed = false;
        this.occurrence = 0;
        this.priority = 0;
        this.tags = null;
        this.reminder = 0;
        this.project = "";
    }

    public String getDeadlineStr() {
        if (this.deadline == null || this.deadline.isEmpty()) {
            return "";
        }
        OffsetDateTime deadline = OffsetDateTime.parse(this.deadline);

        if (deadline == null || deadline.getYear() < 2000) {
            return "";
        }
        return deadline.toString();
    }
}
