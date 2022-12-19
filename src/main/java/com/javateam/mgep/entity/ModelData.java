package com.javateam.mgep.entity;

import java.util.HashMap;

public class ModelData {
    public String message;
    public String error;
    public HashMap<String, String> data = new HashMap();

    public ModelData() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public void setData(HashMap<String, String> data) {
        this.data = data;
    }
}
