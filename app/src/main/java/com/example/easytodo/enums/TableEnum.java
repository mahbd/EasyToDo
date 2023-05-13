package com.example.easytodo.enums;

public enum TableEnum {
    TASK("task"), PROJECT("project"), TAG("tag"), USER("user"), SHARED("shared");

    private String value;

    TableEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
