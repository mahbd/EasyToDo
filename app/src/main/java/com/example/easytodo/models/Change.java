package com.example.easytodo.models;

public class Change {
    private long id;
    private String action;
    private String table;
    private long data_id;

    public Change(long id, String action, String table, long data_id) {
        this.id = id;
        this.action = action;
        this.table = table;
        this.data_id = data_id;
    }

    public String getAction() {
        return action;
    }

    public String getTable() {
        return table;
    }

    public long getData_id() {
        return data_id;
    }
}
