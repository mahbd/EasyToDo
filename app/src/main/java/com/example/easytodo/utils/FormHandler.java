package com.example.easytodo.utils;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Response;

public class FormHandler<T> {
    private final Field[] fields;

    public static class Field {
        private final Object field;
        private final String type;
        private final String name;

        public Field(Object field, String type, String name) {
            this.field = field;
            this.type = type;
            this.name = name;
        }
    }

    String getValue(Field field) {
        switch (field.type) {
            case "text":
            case "password":
            case "email":
                return ((EditText) field.field).getText().toString();
            case "checkbox":
                return ((CheckBox) field.field).isChecked() ? "true" : "false";
            case "radio":
                return ((RadioButton) field.field).isChecked() ? "true" : "false";
            case "spinner":
                return ((Spinner) field.field).getSelectedItem().toString();
            default:
                return "";
        }
    }


    public FormHandler(Field[] fields) {
        this.fields = fields;
    }

    public Map<String, String> getFormData() {
        Map<String, String> body = new HashMap<>();
        for (Field field : fields) {
            body.put(field.name, getValue(field));
        }
        return body;
    }

    private void setError(Field field, String error) {
        switch (field.type) {
            case "text":
            case "password":
            case "email":
                ((EditText) field.field).setError(error);
                break;
            case "checkbox":
                ((CheckBox) field.field).setError(error);
                break;
            case "radio":
                ((RadioButton) field.field).setError(error);
                break;
        }
    }

    public void set400Error(Response<T> response) {
        if (response.code() == 400) {
            if (response.errorBody() != null) {
                try {
                    JsonObject error = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                    for (Field field : fields) {
                        if (error.has(field.name)) {
                            setError(field, error.get(field.name).getAsJsonArray().get(0).getAsString());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void clearField(Field field) {
        switch (field.type) {
            case "text":
            case "password":
            case "email":
                ((EditText) field.field).setText("");
                break;
            case "checkbox":
                ((CheckBox) field.field).setChecked(false);
                break;
            case "radio":
                ((RadioButton) field.field).setChecked(false);
                break;
        }
    }

    public void clearFields() {
        for (Field field : fields) {
            clearField(field);
        }
    }
}
