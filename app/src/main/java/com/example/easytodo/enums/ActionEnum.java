package com.example.easytodo.enums;

public enum ActionEnum {
    CREATE("create"), UPDATE("update"), DELETE("delete");

    private String value;

    ActionEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

