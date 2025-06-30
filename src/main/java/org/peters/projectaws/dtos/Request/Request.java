package org.peters.projectaws.dtos.Request;

import java.util.HashMap;


public class Request {
    String method;
    String path;
    String data;
    HashMap<String, String> headers;


    public Request(String method, String path, String data) {
        this.data = data;
        this.method = method;
        this.path = path;
        headers = new HashMap<>();
    }

    public Request(String method, String path, String data, HashMap<String,String> headers) {
        this.data = data;
        this.method = method;
        this.path = path;
        this.headers = headers;
    }


    public String getPath() {
        return path;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public String getData() {
        return data;
    }

    public String getMethod() {
        return method;
    }
}
