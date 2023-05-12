package com.example.easytodo.models;

public class Token {
    private final String access;
    private final String refresh;

    public Token(String access, String refresh) {
        this.access = access;
        this.refresh = refresh;
    }

    public String getAccess() {
        return access;
    }

    public String getRefresh() {
        return refresh;
    }
}
