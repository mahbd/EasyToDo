package com.example.easytodo.models;

public class User {
    private final long id;
    private final String username;
    private final String email;
    private final String first_name;
    private final String last_name;

    public User(long id, String username, String email, String first_name, String last_name) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public long getId() {
        return id;
    }
}
