package com.example.easytodo.models;

import java.time.OffsetDateTime;


public class Project {
    public String id;
    public String title;
    public String description;
    public String deadline;

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
